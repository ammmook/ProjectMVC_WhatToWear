package com.springmvc.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.ClothingManager;
import com.springmvc.model.Category;
import com.springmvc.model.ClothingItem;
import com.springmvc.model.FormalityType;
import com.springmvc.model.SubCategory;
import com.springmvc.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ClothingController {
	@RequestMapping(value = "addcloth", method = RequestMethod.GET)
	public ModelAndView loadAddClothPage(HttpSession session) {
		ModelAndView mav = new ModelAndView("addcloth_page");
		User user = (User) session.getAttribute("user");

		if (user == null) {
			return new ModelAndView("redirect:/logout");
		}
		
		mav.addObject("userGender", user.getGender());

		ClothingManager cm = new ClothingManager();
		List<FormalityType> types = cm.listFormalityTypes();
		List<SubCategory> subcates = cm.listSubcategories();
		mav.addObject("types", types);
		mav.addObject("subcates", subcates);

		return mav;
	}

	public static final String saveimage = "C:\\Users\\Lenovo\\eclipse-workspace\\WhatToWear\\src\\main\\webapp\\assets\\img\\clothes";

	@RequestMapping(value = "addcloth", method = RequestMethod.POST)
	public ModelAndView addCloth(HttpSession session, HttpServletRequest request,
	        @RequestParam("imagefile") MultipartFile imageFile) throws IOException {
	    
	    ModelAndView mav = new ModelAndView();
	    ClothingManager cm = new ClothingManager();
	    User user = (User) session.getAttribute("user");

	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }

	    if (imageFile == null || imageFile.isEmpty()) {
	        mav.addObject("showAlert", true);
	        mav.addObject("alertType", "error");
	        mav.addObject("alertTitle", "ไม่สำเร็จ!");
	        mav.addObject("alertMessage", "กรุณาเลือกไฟล์รูปภาพ");
	        mav.setViewName("redirect:/addcloth");
	        mav.addObject("userGender", user.getGender());
	        return mav;
	    }

	    String subcateId = request.getParameter("subcategory");
	    int pattern = Integer.parseInt(request.getParameter("pattern"));
	    String typeId = request.getParameter("formalitytype");

	    SubCategory subcate = cm.getSubCategoryById(subcateId);
	    FormalityType type = cm.getFormalityTypeById(typeId);

	    ClothingItem cloth = new ClothingItem();
	    cloth.setHavePattern(pattern == 1);
	    cloth.setUser(user);
	    cloth.setSubCategory(subcate);
	    cloth.setFormalityType(type);
	    cloth.setImgPath("");

	    boolean firstResult = cm.addClothingItem(cloth);

	    if (!firstResult) {
	        mav.addObject("showAlert", true);
	        mav.addObject("alertType", "error");
	        mav.addObject("alertTitle", "เกิดข้อผิดพลาด!");
	        mav.addObject("alertMessage", "ไม่สามารถบันทึกข้อมูลเบื้องต้นได้");
	        mav.setViewName("redirect:/addcloth");
	        return mav;
	    }

	    String originalFilename = imageFile.getOriginalFilename();
	    String fileExtension = "";
	    if (originalFilename.lastIndexOf(".") != -1 && originalFilename.lastIndexOf(".") != 0) {
	        fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
	    }
	    
	    String newFilename = "cloth_" + cloth.getClothid() + fileExtension;
	    Path imgPath = Paths.get(saveimage, newFilename);
	    Files.write(imgPath, imageFile.getBytes());
	    
	    cloth.setImgPath(newFilename);
	    boolean finalSaveResult = cm.addClothingItem(cloth);

	    if (finalSaveResult) {
	        mav.setViewName("redirect:/listclothes?add=success");
	    } else {
	        mav.addObject("showAlert", true);
	        mav.addObject("alertType", "error");
	        mav.addObject("alertTitle", "ไม่สำเร็จ!");
	        mav.addObject("alertMessage", "บันทึกข้อมูลสำเร็จ แต่เกิดข้อผิดพลาดในการอัปเดตที่อยู่รูปภาพ");
	        mav.setViewName("redirect:/addcloth");
	    }

	    return mav;
	}

	@RequestMapping(value = "analyzeClothing", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> analyzeClothing(@RequestParam("imagefile") MultipartFile imageFile) {
		try {
			if (imageFile == null || imageFile.isEmpty()) {
				return ResponseEntity.badRequest().body("{\"success\": false, \"error\": \"No image file provided\"}");
			}

			String apiUrl = "http://127.0.0.1:5000/predict";
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			try (OutputStream out = connection.getOutputStream()) {
				// Headers
				out.write(("--" + boundary + "\r\n").getBytes());
				out.write(("Content-Disposition: form-data; name=\"file\"; filename=\""
						+ imageFile.getOriginalFilename() + "\"\r\n").getBytes());
				out.write(("Content-Type: " + imageFile.getContentType() + "\r\n\r\n").getBytes());

				// File content
				out.write(imageFile.getBytes());
				out.write(("\r\n--" + boundary + "--\r\n").getBytes());
				out.flush();
			}

			int responseCode = connection.getResponseCode();
			System.out.println("API Response Code: " + responseCode); // Debug log

			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					StringBuilder responseString = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						responseString.append(line);
					}

					String apiResponse = responseString.toString();
					System.out.println("API Response: " + apiResponse); // Debug log

					// สร้าง JSONObject จาก String ที่อ่านมา
					JSONObject json = new JSONObject(apiResponse);

					// ดึงค่าจาก JSONObject
					String subjectId = json.getString("subject_id");
					String categoryId = json.getString("category_id");
					int pattern = json.getInt("pattern");
					String formalityId = json.getString("formality_id");

					System.out.println(
							"Parsed - Subject: " + subjectId + ", Pattern: " + pattern + ", Formality: " + formalityId); // Debug
																															// log

					// สร้าง JSON response กลับไปยัง JavaScript
					// ✅ ใช้ status_id เพื่อให้ตรงกับ JavaScript
					String result = String.format(
							"{\"success\": true, \"subject_id\": \"%s\", \"category_id\": \"%s\", \"pattern\": %d, \"formality_id\": \"%s\"}",
							subjectId, categoryId, pattern, formalityId);

					System.out.println("Final Response: " + result); // Debug log

					return ResponseEntity.ok(result);
				}
			} else {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
					StringBuilder errorResponse = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						errorResponse.append(line);
					}
					System.out.println("API Error: " + errorResponse.toString()); // Debug log
					return ResponseEntity.ok("{\"success\": false, \"error\": \"API call failed with code "
							+ responseCode + ": " + errorResponse.toString() + "\"}");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Controller Error: " + e.getMessage()); // Debug log
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
		}
	}

	@RequestMapping(value = "listclothes", method = RequestMethod.GET)
	public ModelAndView loadListClothesPage(HttpSession session) {
	    ModelAndView mav = new ModelAndView("listclothes_page");
	    User user = (User) session.getAttribute("user");

	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }

	    // Set default selections
	    session.setAttribute("selectedCategory", "allcategories");
	    session.setAttribute("selectedSubcate", "allsubcategories");
	    mav.addObject("selectedCategory", "allcategories");
	    mav.addObject("selectedSubcate", "allsubcategories");

	    ClothingManager cm = new ClothingManager();
	    
	    List<Category> categories = cm.listCategoriesByEmail(user.getEmail());
	    List<SubCategory> subcates = cm.listSubcatesByEmail(user.getEmail());
	    List<ClothingItem> clothes = cm.getClothesByEmail(user.getEmail());
	    
	    mav.addObject("categories", categories);
	    mav.addObject("subcates", subcates);

	    if (clothes == null || clothes.isEmpty()) {
	        mav.addObject("err_msg", "ไม่พบเสื้อผ้า");
	    } else {
	        mav.addObject("clothes", clothes);
	    }

	    return mav;
	}

	@RequestMapping(value = "filterclothes", method = RequestMethod.POST)
	public ModelAndView filterClothes(HttpSession session, HttpServletRequest request) {
	    ModelAndView mav = new ModelAndView("listclothes_page");
	    User user = (User) session.getAttribute("user");

	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }

	    ClothingManager cm = new ClothingManager();
	    
	    String categoryId = request.getParameter("category");
	    String subcateId = request.getParameter("subcategory");

	    List<Category> categories = cm.listCategoriesByEmail(user.getEmail());
	    mav.addObject("categories", categories);

	    List<SubCategory> subcates;
	    if (categoryId != null && !categoryId.equals("allcategories")) {
	        subcates = cm.listSubcatesByEmailAndCategory(user.getEmail(), categoryId);
	    } else {
	        subcates = cm.listSubcatesByEmail(user.getEmail());
	    }
	    mav.addObject("subcates", subcates);

	    boolean isSubcateStillValid = false;
	    for (SubCategory sc : subcates) {
	        if (sc.getSubCategoryId().equals(subcateId)) {
	            isSubcateStillValid = true;
	            break;
	        }
	    }

	    if (!"allsubcategories".equals(subcateId) && !isSubcateStillValid) {
	        subcateId = "allsubcategories";
	    }

	    session.setAttribute("selectedCategory", categoryId);
	    session.setAttribute("selectedSubcate", subcateId);
	    mav.addObject("selectedCategory", categoryId);
	    mav.addObject("selectedSubcate", subcateId);
	    
	    List<ClothingItem> clothes;
	    if (subcateId != null && !subcateId.equals("allsubcategories")) {
	        clothes = cm.getClothesByEmailAndSubCate(user.getEmail(), subcateId);
	    } else if (categoryId != null && !categoryId.equals("allcategories")) {
	        clothes = cm.getClothesByEmailAndCategory(user.getEmail(), categoryId);
	    } else {
	        clothes = cm.getClothesByEmail(user.getEmail());
	    }

	    if (clothes == null || clothes.isEmpty()) {
	        mav.addObject("err_msg", "ไม่พบเสื้อผ้าตามหมวดหมู่ที่เลือก");
	    } else {
	        mav.addObject("clothes", clothes);
	    }

	    return mav;
	}

	@RequestMapping(value = "deleteclothes", method = RequestMethod.POST)
	public ModelAndView deleteClothes(HttpSession session, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("listclothes_page");
		User user = (User) session.getAttribute("user");

		if (user == null) {
			return new ModelAndView("redirect:/logout");
		}

		ClothingManager cm = new ClothingManager();
		String[] idStr = request.getParameterValues("selectedItems");

		if (idStr != null && idStr.length > 0) {
			Long[] idLong = new Long[idStr.length];
			List<ClothingItem> clothesDelete = new ArrayList<>();

			for (int i = 0; i < idStr.length; i++) {
				idLong[i] = Long.parseLong(idStr[i]);
			}

			for (Long id : idLong) {
				ClothingItem clothingItem = cm.getClothesById(id);
				if (clothingItem != null) {
					clothesDelete.add(clothingItem);
				}
			}

			boolean result = cm.deleteClothes(idLong);
			if (result) {
				for (ClothingItem item : clothesDelete) {
					String imgPath = item.getImgPath();
					if (imgPath != null && !imgPath.isEmpty()) {
						try {
							Path filePath = Paths.get(saveimage, imgPath);

							if (Files.exists(filePath)) {
								Files.delete(filePath);
								System.out.println("ลบไฟล์สำเร็จ: " + imgPath);
							} else {
								System.out.println("ไม่พบไฟล์: " + imgPath);
							}
						} catch (IOException e) {
							System.err.println("เกิดข้อผิดพลาดในการลบไฟล์: " + imgPath);
							e.printStackTrace();
						}
					}
				}
			} else {
				mav.addObject("err_msg", "เกิดข้อผิดพลาดในการลบเสื้อผ้า");
			}
		} else {
			mav.addObject("err_msg", "กรุณาเลือกเสื้อผ้าที่ต้องการลบ");
		}

		String selectedSubcate = (String) session.getAttribute("selectedSubcate");
		List<SubCategory> subcates = cm.listSubcatesByEmail(user.getEmail());

		if (selectedSubcate != null && !selectedSubcate.equals("allclothes")) {
			List<ClothingItem> filteredClothes = cm.getClothesByEmailAndSubCate(user.getEmail(), selectedSubcate);

			if (filteredClothes == null || filteredClothes.isEmpty()) {
				selectedSubcate = "allclothes";
				session.setAttribute("selectedSubcate", selectedSubcate);
				mav.addObject("clothes", cm.getClothesByEmail(user.getEmail()));
				mav.setViewName("redirect:/listclothes?delete=success");
			} else {
				mav.addObject("clothes", filteredClothes);
			}
		} else {
			mav.addObject("clothes", cm.getClothesByEmail(user.getEmail()));
		}

		mav.addObject("subcates", subcates);
		mav.addObject("selectedSubcate", selectedSubcate);

		if (mav.getModel().get("clothes") == null) {
			mav.addObject("err_msg", "ไม่พบเสื้อผ้า");
		}

		return mav;
	}
}
