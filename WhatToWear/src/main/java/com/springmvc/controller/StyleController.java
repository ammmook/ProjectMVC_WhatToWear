package com.springmvc.controller;

import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.ClothingManager;
import com.springmvc.manager.StyleManager;
import com.springmvc.model.Category;
import com.springmvc.model.ClothingItem;
import com.springmvc.model.FormalityType;
import com.springmvc.model.MatchStyle;
import com.springmvc.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class StyleController {
	@RequestMapping(value = "addfavoritestyle", method = RequestMethod.GET)
	public ModelAndView addFavoriteStyle(HttpSession session, HttpServletRequest request) {
	    ModelAndView mav = new ModelAndView("redirect:/showstyles");
	    User user = (User) session.getAttribute("user");

	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }

	    try {
	        String indexStr = request.getParameter("index");
	        if (indexStr == null) {
	            mav.addObject("err_msg", "ไม่พบข้อมูลชุดที่เลือก");
	            return mav;
	        }

	        int styleIndex = Integer.parseInt(indexStr);
	        List<MatchStyle> allMatchedStyles = (List<MatchStyle>) session.getAttribute("matchedStyles");
	        
	        if (allMatchedStyles == null || styleIndex >= allMatchedStyles.size()) {
	            System.out.println("ไม่พบข้อมูลชุดที่เลือก");
	            mav.addObject("err_msg", "ไม่พบข้อมูลชุดที่เลือก");
	            return mav;
	        }

	        MatchStyle selectedStyle = allMatchedStyles.get(styleIndex);

	        StyleManager sm = new StyleManager();
	        boolean result = sm.saveFavoriteStyle(selectedStyle, user.getEmail());

	        if (result) {
	            mav = new ModelAndView("redirect:/favoritestyles");
	            System.out.println("✅ saved favorite");
	        } else {
	            System.out.println("❌ cant save favorite");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        mav.addObject("err_msg", "เกิดข้อผิดพลาด: " + e.getMessage());
	    }

	    return mav;
	}
	
	@RequestMapping(value = "favoritestyles", method = RequestMethod.GET)
	public ModelAndView loadFavoriteStylesPage(HttpSession session) {
	    ModelAndView mav = new ModelAndView("favoritestyles_page");
	    User user = (User) session.getAttribute("user");

	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }
	    
	    session.setAttribute("selectedType", "alltype");
	    mav.addObject("selectedType", "alltype");

	    StyleManager sm = new StyleManager();
	    List<FormalityType> types = sm.listFormalityTypes(user.getEmail());
	    List<MatchStyle> styles = sm.listFavoriteStylesByEmail(user.getEmail());
	    
	    mav.addObject("type", types);

	    if (styles == null || styles.isEmpty()) {
	        mav.addObject("err_msg", "ไม่พบสไตล์การแต่งตัว");
	    } else {
	        mav.addObject("styles", styles);
	    }

	    return mav;
	}
	
	@RequestMapping(value = "filterType", method = RequestMethod.POST)
	public ModelAndView filterTypeStyles(HttpSession session, HttpServletRequest request) {
	    ModelAndView mav = new ModelAndView("favoritestyles_page");
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }
	    
	    StyleManager sm = new StyleManager();
	    List<FormalityType> types = sm.listFormalityTypes(user.getEmail());
	    mav.addObject("type", types);
	    
	    // รับค่า sortType
	    String typeId = request.getParameter("sortType");
	    session.setAttribute("selectedType", typeId);
	    mav.addObject("selectedType", typeId);
	    
	    // รับค่า showOuterwear checkbox
	    boolean showOuterwear = request.getParameter("showOuterwear") != null;
	    session.setAttribute("showOuterwear", showOuterwear);
	    mav.addObject("showOuterwear", showOuterwear);
	    
	    // เรียกใช้ method ใหม่ที่รองรับการกรองเสื้อคลุม
	    List<MatchStyle> styles = sm.getFavoriteStylesWithOuterwear(user.getEmail(), typeId, showOuterwear);
	    
	    if (styles == null || styles.isEmpty()) {
	        mav.addObject("err_msg", "ไม่พบชุดที่ชื่นชอบ");
	    } else {
	        mav.addObject("styles", styles);
	    }
	    
	    return mav;
	}

	@RequestMapping(value = "deletefavorite", method = RequestMethod.GET)
	public ModelAndView deleteFavorite(HttpSession session, HttpServletRequest request) {
	    ModelAndView mav = new ModelAndView("favoritestyles_page");
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }
	    
	    Long styleId = Long.parseLong(request.getParameter("id"));
	    StyleManager sm = new StyleManager();
	    boolean result = sm.deleteFavorite(styleId);
	    if (!result) {
	        mav.addObject("err_msg", "ลบข้อมูลไม่สำเร็จ");
	    }
	    
	    // ดึงค่าจาก session
	    String selectedType = (String) session.getAttribute("selectedType");
	    if (selectedType == null) {
	        selectedType = "alltype";
	    }
	    
	    // ดึงค่า showOuterwear จาก session
	    Boolean showOuterwearObj = (Boolean) session.getAttribute("showOuterwear");
	    boolean showOuterwear;
	    if (showOuterwearObj != null) {
	        showOuterwear = showOuterwearObj;
	    } else {
	        showOuterwear = false;
	    }
	    
	    // ดึงข้อมูล types
	    List<FormalityType> types = sm.listFormalityTypes(user.getEmail());
	    List<MatchStyle> styles;
	    
	    // เรียกใช้ method ใหม่ที่รองรับการกรองเสื้อคลุม
	    styles = sm.getFavoriteStylesWithOuterwear(user.getEmail(), selectedType, showOuterwear);
	    
	    // ถ้าไม่มี styles แล้ว selectedType ไม่ใช่ alltype ให้ reset เป็น alltype
	    if ((styles == null || styles.isEmpty()) && !selectedType.equals("alltype")) {
	        selectedType = "alltype";
	        session.setAttribute("selectedType", selectedType);
	        // ลองดึงข้อมูลใหม่แบบ alltype
	        styles = sm.getFavoriteStylesWithOuterwear(user.getEmail(), selectedType, showOuterwear);
	    }
	    
	    // set attributes สำหรับ view
	    mav.addObject("type", types);
	    mav.addObject("selectedType", selectedType);
	    mav.addObject("showOuterwear", showOuterwear);
	    
	    if (styles == null || styles.isEmpty()) {
	        mav.addObject("err_msg", "ไม่พบชุดที่ชื่นชอบ");
	    } else {
	        mav.addObject("styles", styles);
	    }
	    
	    return mav;
	}
	
	@RequestMapping(value = "matchstyles", method = RequestMethod.GET)
	public ModelAndView loadMatchStylesPage(HttpSession session, HttpServletRequest request) {
	    ModelAndView mav = new ModelAndView("matchstyles_page");
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }

	    String categoryId = request.getParameter("id");
	    String[] clothesId = request.getParameterValues("clothid");
	    String clearClothId = request.getParameter("clear");
	    
	    if (clearClothId != null) {
	    	session.removeAttribute("selectedClothes");
	    }

	    if (categoryId == null) {
	        categoryId = "CG001";
	    }

	    List<String> selectedClothes = (List<String>) session.getAttribute("selectedClothes");
	    if (selectedClothes == null) {
	        selectedClothes = new ArrayList<String>();
	    }

	    if (clothesId != null) {
	        for (String id : clothesId) {
	        	System.out.println(id);
	            if (selectedClothes.contains(id)) {
	                selectedClothes.remove(id);
	            } else {
	                selectedClothes.add(id);
	            }
	        }
	    }

	    session.setAttribute("selectedClothes", selectedClothes);
	    mav.addObject("selectedClothes", selectedClothes);
	    
	    session.setAttribute("selectedCates", categoryId);
	    mav.addObject("selectedCates", categoryId);

	    StyleManager sm = new StyleManager();
	    List<ClothingItem> clothes = sm.getClothingByCategory(user.getEmail(), categoryId);

	    if (clothes == null || clothes.isEmpty()) {
	        mav.addObject("err_msg", "คุณยังไม่มีเสื้อผ้าในหมวดหมู่นี้");
	    } else {
	        mav.addObject("clothes", clothes);
	    }

	    return mav;
	}
	
	// วางทับเมธอด matchStyles เดิมของคุณ
	@RequestMapping(value = "matchstyles", method = RequestMethod.POST)
	public ModelAndView matchStyles(HttpSession session, HttpServletRequest request) {
	    ModelAndView mav = new ModelAndView("showstyles_page");
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }
	    
	    // --- โค้ดส่วนต้นของเมธอด (การดึงข้อมูลและจัดกลุ่มเสื้อผ้า) ยังคงเหมือนเดิม ---
	    List<MatchStyle> matchedStylesSession = (List<MatchStyle>) session.getAttribute("matchedStyles");
	    List<ClothingItem> selectedClothSession = (List<ClothingItem>) session.getAttribute("selectedCloth");
	    
	    if (matchedStylesSession != null) {
	        session.removeAttribute("matchedStyles");
	        if (selectedClothSession != null) {
	            session.removeAttribute("selectedCloth");
	        }
	    }
	    
	    List<String> currentSelection = (List<String>) session.getAttribute("selectedClothes");
	    String formalityTypeId = request.getParameter("formality_type");

	    Map<String, List<String>> selectedClothesMap = (Map<String, List<String>>) session.getAttribute("selectedClothesMap");
	    if (selectedClothesMap == null) {
	        selectedClothesMap = new HashMap<>();
	    }

	    String currentCategory = (String) session.getAttribute("selectedCates");
	    if (currentCategory == null) {
	        currentCategory = "CG001";
	    }
	    
	    selectedClothesMap.put(currentCategory, currentSelection);
	    session.setAttribute("selectedClothesMap", selectedClothesMap);

	    List<String> allSelectedClothesIds = new ArrayList<>();
	    for (List<String> clothesList : selectedClothesMap.values()) {
	        allSelectedClothesIds.addAll(clothesList);
	    }

	    String[] selectedClothesIds = allSelectedClothesIds.toArray(new String[0]);
	    
	    boolean hasSelectedClothes = false;
	    if (selectedClothesIds != null && selectedClothesIds.length > 0) {
	        hasSelectedClothes = true;
	    }
	    
	    ClothingManager cm = new ClothingManager();
	    StyleManager sm = new StyleManager();
	    List<ClothingItem> selectedClothes;
	    
	    if (hasSelectedClothes) {
	        selectedClothes = cm.getClothingItemsByIds(selectedClothesIds, user.getEmail());
	    } else {
	        selectedClothes = cm.getClothesByEmail(user.getEmail());
	    }
	    
	    List<ClothingItem> allUserClothes = cm.getClothesByEmail(user.getEmail());
	    
	    List<ClothingItem> selectedFormalTops = new ArrayList<>();
	    List<ClothingItem> selectedSemiFormalTops = new ArrayList<>();
	    List<ClothingItem> selectedCasualTops = new ArrayList<>();
	    List<ClothingItem> selectedFormalBottoms = new ArrayList<>();
	    List<ClothingItem> selectedSemiFormalBottoms = new ArrayList<>();
	    List<ClothingItem> selectedCasualBottoms = new ArrayList<>();
	    List<ClothingItem> selectedFormalDresses = new ArrayList<>();
	    List<ClothingItem> selectedSemiFormalDresses = new ArrayList<>();
	    List<ClothingItem> selectedCasualDresses = new ArrayList<>();
	    List<ClothingItem> selectedFormalOuterwears = new ArrayList<>();
	    List<ClothingItem> selectedSemiFormalOuterwears = new ArrayList<>();
	    List<ClothingItem> selectedCasualOuterwears = new ArrayList<>();
	    
	    List<ClothingItem> allFormalTops = new ArrayList<>();
	    List<ClothingItem> allSemiFormalTops = new ArrayList<>();
	    List<ClothingItem> allCasualTops = new ArrayList<>();
	    List<ClothingItem> allFormalBottoms = new ArrayList<>();
	    List<ClothingItem> allSemiFormalBottoms = new ArrayList<>();
	    List<ClothingItem> allCasualBottoms = new ArrayList<>();
	    List<ClothingItem> allFormalOuterwears = new ArrayList<>();
	    List<ClothingItem> allSemiFormalOuterwears = new ArrayList<>();
	    List<ClothingItem> allCasualOuterwears = new ArrayList<>();
	    
	    if (!hasSelectedClothes) {
	        for (ClothingItem item : allUserClothes) {
	            String categoryId = item.getSubCategory().getCategory().getCategoryId();
	            String formalityId = item.getFormalityType().getTypeId();
	            if ("CG001".equals(categoryId)) { //Top
	                if ("T01".equals(formalityId)) selectedFormalTops.add(item);
	                else if ("T02".equals(formalityId)) selectedSemiFormalTops.add(item);
	                else if ("T03".equals(formalityId)) selectedCasualTops.add(item);
	            } else if ("CG002".equals(categoryId)) { //Bottom
	                if ("T01".equals(formalityId)) selectedFormalBottoms.add(item);
	                else if ("T02".equals(formalityId)) selectedSemiFormalBottoms.add(item);
	                else if ("T03".equals(formalityId)) selectedCasualBottoms.add(item);
	            } else if ("CG003".equals(categoryId)) { //Dress
	                if ("T01".equals(formalityId)) selectedFormalDresses.add(item);
	                else if ("T02".equals(formalityId)) selectedSemiFormalDresses.add(item);
	                else if ("T03".equals(formalityId)) selectedCasualDresses.add(item);
	            }  else if ("CG004".equals(categoryId)) { //Outwear
	                if ("T01".equals(formalityId)) selectedFormalOuterwears.add(item);
	                else if ("T02".equals(formalityId)) selectedSemiFormalOuterwears.add(item);
	                else if ("T03".equals(formalityId)) selectedCasualOuterwears.add(item);
	            }
	        }
	    }

	    for (ClothingItem item : selectedClothes) {
	        try {
	            String categoryId = item.getSubCategory().getCategory().getCategoryId();
	            String formalityId = item.getFormalityType().getTypeId();
	            if ("CG001".equals(categoryId)) { // Top
	                if ("T01".equals(formalityId)) selectedFormalTops.add(item);
	                else if ("T02".equals(formalityId)) selectedSemiFormalTops.add(item);
	                else if ("T03".equals(formalityId)) selectedCasualTops.add(item);
	            } else if ("CG002".equals(categoryId)) { // Bottom
	                if ("T01".equals(formalityId)) selectedFormalBottoms.add(item);
	                else if ("T02".equals(formalityId)) selectedSemiFormalBottoms.add(item);
	                else if ("T03".equals(formalityId)) selectedCasualBottoms.add(item);
	            } else if ("CG003".equals(categoryId)) { // Dress
	                if ("T01".equals(formalityId)) selectedFormalDresses.add(item);
	                else if ("T02".equals(formalityId)) selectedSemiFormalDresses.add(item);
	                else if ("T03".equals(formalityId)) selectedCasualDresses.add(item);
	            } else if ("CG004".equals(categoryId)) { // Outerwear
	                if ("T01".equals(formalityId)) selectedFormalOuterwears.add(item);
	                else if ("T02".equals(formalityId)) selectedSemiFormalOuterwears.add(item);
	                else if ("T03".equals(formalityId)) selectedCasualOuterwears.add(item);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    for (ClothingItem item : allUserClothes) {
	        try {
	            String categoryId = item.getSubCategory().getCategory().getCategoryId();
	            String formalityId = item.getFormalityType().getTypeId();
	            if ("CG001".equals(categoryId)) { // Top
	                if ("T01".equals(formalityId)) allFormalTops.add(item);
	                else if ("T02".equals(formalityId)) allSemiFormalTops.add(item);
	                else if ("T03".equals(formalityId)) allCasualTops.add(item);
	            } else if ("CG002".equals(categoryId)) { // Bottom
	                if ("T01".equals(formalityId)) allFormalBottoms.add(item);
	                else if ("T02".equals(formalityId)) allSemiFormalBottoms.add(item);
	                else if ("T03".equals(formalityId)) allCasualBottoms.add(item);
	            } else if ("CG004".equals(categoryId)) { // Outerwear
	                if ("T01".equals(formalityId)) allFormalOuterwears.add(item);
	                else if ("T02".equals(formalityId)) allSemiFormalOuterwears.add(item);
	                else if ("T03".equals(formalityId)) allCasualOuterwears.add(item);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	   
	    // --- โค้ดส่วนสร้างชุด (Matching Logic) ที่แก้ไขแล้ว ---
	    List<MatchStyle> matchedStyles = new ArrayList<>();
	    FormalityType formalityType = sm.getFormalityTypeById(formalityTypeId);
	    
	    // Rule 1: Formal (T01) Combinations
	    if ("T01".equals(formalityTypeId)) {
	        // 1. Top(Formal) + Bottom(Formal)
	        for (ClothingItem selectedTop : selectedFormalTops) {
	            for (ClothingItem bottom : allFormalBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedTop);
	                potentialStyle.getClothingItems().add(bottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedFormalBottoms) {
	            for (ClothingItem top : allFormalTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(top);
	                potentialStyle.getClothingItems().add(selectedBottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        
	        // 2. Top(Formal) + Bottom(Formal) + Outerwear(Formal)
	        for (ClothingItem selectedTop : selectedFormalTops) {
	            for (ClothingItem bottom : allFormalBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                for (ClothingItem outerwear : allFormalOuterwears) {
	                    if (selectedTop.getClothid().equals(outerwear.getClothid()) || bottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(selectedTop);
	                    potentialStyle.getClothingItems().add(bottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedFormalBottoms) {
	            for (ClothingItem top : allFormalTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                for (ClothingItem outerwear : allFormalOuterwears) {
	                    if (top.getClothid().equals(outerwear.getClothid()) || selectedBottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(top);
	                    potentialStyle.getClothingItems().add(selectedBottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }

	        // 3. Dress(Formal) + Outerwear(Formal)
	        for (ClothingItem selectedDress : selectedFormalDresses) {
	            for (ClothingItem outerwear : allFormalOuterwears) {
	                if (selectedDress.getClothid().equals(outerwear.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedDress);
	                potentialStyle.getClothingItems().add(outerwear);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	    }

	    // Rule 2: Semi-formal (T02) Combinations
	    else if ("T02".equals(formalityTypeId)) {
	        List<ClothingItem> validOuterwears = new ArrayList<>();
	        validOuterwears.addAll(allFormalOuterwears);
	        validOuterwears.addAll(allSemiFormalOuterwears);
	        
	        // 1. Top(Formal) + Bottom(Semi-formal)
	        for (ClothingItem selectedTop : selectedFormalTops) {
	            for (ClothingItem bottom : allSemiFormalBottoms) {
	                 if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedTop);
	                potentialStyle.getClothingItems().add(bottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedSemiFormalBottoms) {
	            for (ClothingItem top : allFormalTops) {
	                 if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(top);
	                potentialStyle.getClothingItems().add(selectedBottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        
	        // 2. Top(Semi-formal) + Bottom(Formal)
	        for (ClothingItem selectedTop : selectedSemiFormalTops) {
	            for (ClothingItem bottom : allFormalBottoms) {
	                 if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedTop);
	                potentialStyle.getClothingItems().add(bottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedFormalBottoms) {
	            for (ClothingItem top : allSemiFormalTops) {
	                 if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(top);
	                potentialStyle.getClothingItems().add(selectedBottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        
	        // 3. Top(Semi-formal) + Bottom(Semi-formal)
	        for (ClothingItem selectedTop : selectedSemiFormalTops) {
	            for (ClothingItem bottom : allSemiFormalBottoms) {
	                 if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedTop);
	                potentialStyle.getClothingItems().add(bottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedSemiFormalBottoms) {
	            for (ClothingItem top : allSemiFormalTops) {
	                 if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(top);
	                potentialStyle.getClothingItems().add(selectedBottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        
	        // 4. Top(F) + Bottom(S) + Outerwear(F/S)
	        for (ClothingItem selectedTop : selectedFormalTops) {
	            for (ClothingItem bottom : allSemiFormalBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (selectedTop.getClothid().equals(outerwear.getClothid()) || bottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(selectedTop);
	                    potentialStyle.getClothingItems().add(bottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedSemiFormalBottoms) {
	            for (ClothingItem top : allFormalTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (top.getClothid().equals(outerwear.getClothid()) || selectedBottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(top);
	                    potentialStyle.getClothingItems().add(selectedBottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        
	        // 5. Top(S) + Bottom(F) + Outerwear(F/S)
	        for (ClothingItem selectedTop : selectedSemiFormalTops) {
	            for (ClothingItem bottom : allFormalBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (selectedTop.getClothid().equals(outerwear.getClothid()) || bottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(selectedTop);
	                    potentialStyle.getClothingItems().add(bottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedFormalBottoms) {
	            for (ClothingItem top : allSemiFormalTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (top.getClothid().equals(outerwear.getClothid()) || selectedBottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(top);
	                    potentialStyle.getClothingItems().add(selectedBottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        
	        // 6. Top(S) + Bottom(S) + Outerwear(F/S)
	        for (ClothingItem selectedTop : selectedSemiFormalTops) {
	            for (ClothingItem bottom : allSemiFormalBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (selectedTop.getClothid().equals(outerwear.getClothid()) || bottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(selectedTop);
	                    potentialStyle.getClothingItems().add(bottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedSemiFormalBottoms) {
	            for (ClothingItem top : allSemiFormalTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (top.getClothid().equals(outerwear.getClothid()) || selectedBottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(top);
	                    potentialStyle.getClothingItems().add(selectedBottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        
	        // 7. Dress(S) + Outerwear(F/S)
	        for (ClothingItem selectedDress : selectedSemiFormalDresses) {
	            for (ClothingItem outerwear : validOuterwears) {
	                if (selectedDress.getClothid().equals(outerwear.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedDress);
	                potentialStyle.getClothingItems().add(outerwear);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	    }

	    // Rule 3: Casual (T03) Combinations
	    else if ("T03".equals(formalityTypeId)) {
	        List<ClothingItem> validOuterwears = new ArrayList<>();
	        validOuterwears.addAll(allSemiFormalOuterwears);
	        validOuterwears.addAll(allCasualOuterwears);
	        
	        // 1. Top(S) + Bottom(C)
	        for (ClothingItem selectedTop : selectedSemiFormalTops) {
	            for (ClothingItem bottom : allCasualBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedTop);
	                potentialStyle.getClothingItems().add(bottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedCasualBottoms) {
	            for (ClothingItem top : allSemiFormalTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(top);
	                potentialStyle.getClothingItems().add(selectedBottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        
	        // 2. Top(C) + Bottom(S)
	        for (ClothingItem selectedTop : selectedCasualTops) {
	            for (ClothingItem bottom : allSemiFormalBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedTop);
	                potentialStyle.getClothingItems().add(bottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedSemiFormalBottoms) {
	            for (ClothingItem top : allCasualTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(top);
	                potentialStyle.getClothingItems().add(selectedBottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        
	        // 3. Top(C) + Bottom(C)
	        for (ClothingItem selectedTop : selectedCasualTops) {
	            for (ClothingItem bottom : allCasualBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedTop);
	                potentialStyle.getClothingItems().add(bottom);
	                if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                    matchedStyles.add(potentialStyle);
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedCasualBottoms) {
	            for (ClothingItem top : allCasualTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(top);
	                potentialStyle.getClothingItems().add(selectedBottom);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        
	        // 4. Top(S) + Bottom(C) + Outerwear(S/C)
	        for (ClothingItem selectedTop : selectedSemiFormalTops) {
	            for (ClothingItem bottom : allCasualBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (selectedTop.getClothid().equals(outerwear.getClothid()) || bottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(selectedTop);
	                    potentialStyle.getClothingItems().add(bottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedCasualBottoms) {
	            for (ClothingItem top : allSemiFormalTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (top.getClothid().equals(outerwear.getClothid()) || selectedBottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(top);
	                    potentialStyle.getClothingItems().add(selectedBottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        
	        // 5. Top(C) + Bottom(S) + Outerwear(S/C)
	        for (ClothingItem selectedTop : selectedCasualTops) {
	            for (ClothingItem bottom : allSemiFormalBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (selectedTop.getClothid().equals(outerwear.getClothid()) || bottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(selectedTop);
	                    potentialStyle.getClothingItems().add(bottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedSemiFormalBottoms) {
	            for (ClothingItem top : allCasualTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (top.getClothid().equals(outerwear.getClothid()) || selectedBottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(top);
	                    potentialStyle.getClothingItems().add(selectedBottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        
	        // 6. Top(C) + Bottom(C) + Outerwear(S/C)
	        for (ClothingItem selectedTop : selectedCasualTops) {
	            for (ClothingItem bottom : allCasualBottoms) {
	                if (selectedTop.getClothid().equals(bottom.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (selectedTop.getClothid().equals(outerwear.getClothid()) || bottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(selectedTop);
	                    potentialStyle.getClothingItems().add(bottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        for (ClothingItem selectedBottom : selectedCasualBottoms) {
	            for (ClothingItem top : allCasualTops) {
	                if (selectedBottom.getClothid().equals(top.getClothid())) continue;
	                for (ClothingItem outerwear : validOuterwears) {
	                    if (top.getClothid().equals(outerwear.getClothid()) || selectedBottom.getClothid().equals(outerwear.getClothid())) continue;
	                    MatchStyle potentialStyle = new MatchStyle();
	                    potentialStyle.setFormalityType(formalityType);
	                    potentialStyle.getClothingItems().add(top);
	                    potentialStyle.getClothingItems().add(selectedBottom);
	                    potentialStyle.getClothingItems().add(outerwear);
	                    
	                    if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                        // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                        if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                            matchedStyles.add(potentialStyle);
	                        }
	                    }
	                }
	            }
	        }
	        
	        // 7. Dress(S) + Outerwear(S/C)
	        for (ClothingItem selectedDress : selectedSemiFormalDresses) {
	            for (ClothingItem outerwear : validOuterwears) {
	                if (selectedDress.getClothid().equals(outerwear.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedDress);
	                potentialStyle.getClothingItems().add(outerwear);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	        
	        // 8. Dress(C) + Outerwear(S/C)
	        for (ClothingItem selectedDress : selectedCasualDresses) {
	            for (ClothingItem outerwear : validOuterwears) {
	                if (selectedDress.getClothid().equals(outerwear.getClothid())) continue;
	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(selectedDress);
	                potentialStyle.getClothingItems().add(outerwear);
	                
	                if (!sm.isStyleAlreadyFavorited(potentialStyle, user.getEmail())) {
	                    // 2. เช็คว่าเพิ่งสร้างซ้ำในรอบนี้หรือไม่ (A+B vs B+A)
	                    if (!isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                        matchedStyles.add(potentialStyle);
	                    }
	                }
	            }
	        }
	    }
	    
	    // --- โค้ดส่วนท้ายของเมธอด (การเรียงลำดับและส่งข้อมูล) ยังคงเหมือนเดิม ---
	    mav.addObject("selectedClothes", selectedClothes);
	    
	    if (matchedStyles.isEmpty()) {
	        mav.addObject("err_msg", "ไม่พบการจับคู่ที่เหมาะสม");
	    } else {
	        mav.addObject("styles", matchedStyles);
	        mav.addObject("totalStyles", matchedStyles.size());
	    }
	    
	    for (MatchStyle style : matchedStyles) {
	        Collections.sort(style.getClothingItems(), new Comparator<ClothingItem>() {
	            @Override
	            public int compare(ClothingItem item1, ClothingItem item2) {
	                String cat1 = item1.getSubCategory().getCategory().getCategoryId();
	                String cat2 = item2.getSubCategory().getCategory().getCategoryId();
	                int order1 = getCategoryOrder(cat1);
	                int order2 = getCategoryOrder(cat2);
	                return Integer.compare(order1, order2);
	            }
	            private int getCategoryOrder(String categoryId) {
	                switch (categoryId) {
	                    case "CG004": return 1;
	                    case "CG001": return 2;
	                    case "CG002": return 3;
	                    case "CG003": return 4;
	                    default: return 5;
	                }
	            }
	        });
	    }

	    Collections.sort(matchedStyles, new Comparator<MatchStyle>() {
	        @Override
	        public int compare(MatchStyle style1, MatchStyle style2) {
	            boolean isDress1 = hasDress(style1);
	            boolean isDress2 = hasDress(style2);
	            int itemCount1 = style1.getClothingItems().size();
	            int itemCount2 = style2.getClothingItems().size();
	            int priority1 = getStylePriority(isDress1, itemCount1);
	            int priority2 = getStylePriority(isDress2, itemCount2);
	            if (priority1 == priority2) {
	                return Integer.compare(itemCount1, itemCount2);
	            }
	            return Integer.compare(priority1, priority2);
	        }
	        private boolean hasDress(MatchStyle style) {
	            for (ClothingItem item : style.getClothingItems()) {
	                if ("CG003".equals(item.getSubCategory().getCategory().getCategoryId())) {
	                    return true;
	                }
	            }
	            return false;
	        }
	        private int getStylePriority(boolean hasDress, int itemCount) {
	            if (hasDress) {
	                if (itemCount == 1) return 1;
	                if (itemCount == 2) return 2;
	            } else {
	                if (itemCount == 2) return 3;
	                if (itemCount == 3) return 4;
	            }
	            return 5;
	        }
	    });
	    
	    Collections.sort(selectedClothes, new Comparator<ClothingItem>() {
	        @Override
	        public int compare(ClothingItem item1, ClothingItem item2) {
	            String subcateId1 = item1.getSubCategory().getSubCategoryId();
	            String subcateId2 = item2.getSubCategory().getSubCategoryId();
	            return subcateId1.compareTo(subcateId2);
	        }
		});

	    session.setAttribute("matchedStyles", matchedStyles);
	    session.setAttribute("selectedCloth", selectedClothes);
	    session.removeAttribute("selectedClothes");
	    session.removeAttribute("selectedClothesMap");
	    
	    return mav;
	}

	/**
	 * Helper Method (ผู้คุม): ตรวจสอบว่าชุดใหม่ (newStyle) มีเสื้อผ้าซ้ำกับชุดที่มีอยู่แล้วในลิสต์ (existingStyles) หรือไม่
	 * โดยไม่สนใจลำดับของเสื้อผ้า
	 */
	private boolean isStyleAlreadyAdded(List<MatchStyle> existingStyles, MatchStyle newStyle) {
	    // 1. ดึง ID เสื้อผ้าทั้งหมดจาก "ชุดใหม่" แล้วเรียงลำดับ
	    List<Long> newItemIds = new ArrayList<>();
	    for (ClothingItem item : newStyle.getClothingItems()) {
	        newItemIds.add(item.getClothid());
	    }
	    Collections.sort(newItemIds);

	    // 2. วนลูปตรวจ "ชุดเก่า" ที่มีอยู่แล้วในลิสต์ทีละชุด
	    for (MatchStyle existingStyle : existingStyles) {
	        // 3. ดึง ID เสื้อผ้าทั้งหมดจาก "ชุดเก่า" แล้วเรียงลำดับ
	        List<Long> existingItemIds = new ArrayList<>();
	        for (ClothingItem item : existingStyle.getClothingItems()) {
	            existingItemIds.add(item.getClothid());
	        }
	        Collections.sort(existingItemIds);

	        // 4. เปรียบเทียบ ID ที่เรียงลำดับแล้ว ถ้าเหมือนกันเป๊ะ = ชุดซ้ำ
	        if (newItemIds.equals(existingItemIds)) {
	            return true; // เจอชุดซ้ำ, ส่งค่า true
	        }
	    }

	    return false; // ไม่เจอชุดซ้ำ, ส่งค่า false
	}
	
	@RequestMapping(value = "showstyles", method = RequestMethod.GET)
	public ModelAndView showStylesFiltered(HttpServletRequest request, HttpSession session) {
	    ModelAndView mav = new ModelAndView("showstyles_page");
	    User user = (User) session.getAttribute("user");
	    
	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }
	    
	    // รับ clothingId ที่ต้องการกรอง (ถ้ามี)
	    String filterClothingId = request.getParameter("id");

	    List<MatchStyle> allMatchedStyles = (List<MatchStyle>) session.getAttribute("matchedStyles");
	    List<ClothingItem> selectedClothes = (List<ClothingItem>) session.getAttribute("selectedCloth");
	    
	    if (allMatchedStyles == null || selectedClothes == null) {
	        return new ModelAndView("redirect:/matchstyles_page");
	    }
	    
	    List<MatchStyle> filteredStyles = new ArrayList<>();
	    
	    if (filterClothingId != null && !filterClothingId.trim().isEmpty()) {
	        Long clothingId = Long.parseLong(filterClothingId);
	        
	        for (MatchStyle style : allMatchedStyles) {
	            boolean containsItem = false;
	            for (ClothingItem item : style.getClothingItems()) {
	                if (item.getClothid().equals(clothingId)) {
	                    containsItem = true;
	                    break;
	                }
	            }
	            if (containsItem) {
	                filteredStyles.add(style);
	            }
	        }
	        
	        // หาเสื้อผ้าที่เลือกเพื่อแสดงใน UI
	        ClothingItem selectedItem = null;
	        for (ClothingItem item : selectedClothes) {
	            if (item.getClothid().equals(clothingId)) {
	                selectedItem = item;
	                break;
	            }
	        }
	        
	        if (selectedItem != null) {
	            mav.addObject("filterMessage", "แสดงสไตล์ที่มี " + selectedItem.getSubCategory().getCategory().getCategoryName() + 
	                " (" + selectedItem.getFormalityType().getTypeName() + ")");
	        }
	        
	    } else {
	        filteredStyles = allMatchedStyles;
	    }
	    
	    // ส่งข้อมูลไปยัง view
	    mav.addObject("selectedClothes", selectedClothes);
	    mav.addObject("styles", filteredStyles);
	    mav.addObject("totalStyles", filteredStyles.size());
	    mav.addObject("filterClothingId", filterClothingId);
	    
	    // เพิ่มข้อมูลสำหรับการแสดงผล
	    if (filteredStyles.isEmpty() && filterClothingId != null) {
	        mav.addObject("err_msg", "ไม่พบสไตล์ที่มีเสื้อผ้าชิ้นที่เลือก");
	    }

	    return mav;
	}
}
