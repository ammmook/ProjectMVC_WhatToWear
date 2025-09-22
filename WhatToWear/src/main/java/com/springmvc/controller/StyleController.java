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

	@RequestMapping(value = "matchstyles", method = RequestMethod.POST)
	public ModelAndView matchStyles(HttpSession session, HttpServletRequest request) {
	    ModelAndView mav = new ModelAndView("showstyles_page");
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return new ModelAndView("redirect:/logout");
	    }

	    // --- 1. จัดการข้อมูลจาก Session และ Request ---
	    // เคลียร์ข้อมูลเก่าใน session
	    session.removeAttribute("matchedStyles");
	    session.removeAttribute("selectedCloth");

	    // ดึงข้อมูลเสื้อผ้าที่ถูกเลือกจาก session และ request
	    List<String> currentSelection = (List<String>) session.getAttribute("selectedClothes");
	    Map<String, List<String>> selectedClothesMap = (Map<String, List<String>>) session.getAttribute("selectedClothesMap");
	    if (selectedClothesMap == null) {
	        selectedClothesMap = new HashMap<>();
	    }
	    
	    String currentCategory = (String) session.getAttribute("selectedCates");
	    if (currentCategory == null) {
	        currentCategory = "CG001"; // Default category
	    }
	    selectedClothesMap.put(currentCategory, currentSelection);

	    // รวม ID เสื้อผ้าที่เลือกทั้งหมดจากทุกหมวดหมู่
	    List<String> allSelectedClothesIds = new ArrayList<>();
	    for (List<String> clothesList : selectedClothesMap.values()) {
	        if (clothesList != null) {
	            allSelectedClothesIds.addAll(clothesList);
	        }
	    }
	    String[] selectedClothesIds = allSelectedClothesIds.toArray(new String[0]);
	    boolean hasSelectedClothes = (selectedClothesIds != null && selectedClothesIds.length > 0);

	    // --- 2. ดึงข้อมูลเสื้อผ้าทั้งหมดและจัดกลุ่ม ---
	    ClothingManager cm = new ClothingManager();
	    List<ClothingItem> allUserClothes = cm.getClothesByEmail(user.getEmail());
	    List<ClothingItem> selectedClothes = hasSelectedClothes
	            ? cm.getClothingItemsByIds(selectedClothesIds, user.getEmail())
	            : allUserClothes;
	    
	    // ใช้เมธอด classifyClothes เพื่อจัดกลุ่มเสื้อผ้าทั้งหมดและที่เลือกไว้
	    Map<String, List<ClothingItem>> allClothesByType = classifyClothes(allUserClothes);
	    Map<String, List<ClothingItem>> selectedClothesByType = classifyClothes(selectedClothes);
	    
	    // --- 3. สร้างชุดตามกฎ (Matching Logic) ---
	    List<MatchStyle> matchedStyles = new ArrayList<>();
	    StyleManager sm = new StyleManager();
	    String formalityTypeId = request.getParameter("formality_type");
	    FormalityType formalityType = sm.getFormalityTypeById(formalityTypeId);

	    if ("T01".equals(formalityTypeId)) { // Rule 1: Formal
	        // Top(F) + Bottom(F)
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("FormalTops"), allClothesByType.get("FormalBottoms"), formalityType, sm, user.getEmail());
	        generateTwoItemStyles(matchedStyles, allClothesByType.get("FormalTops"), selectedClothesByType.get("FormalBottoms"), formalityType, sm, user.getEmail());
	        // Top(F) + Bottom(F) + Outerwear(F)
	        generateThreeItemStyles(matchedStyles, selectedClothesByType.get("FormalTops"), allClothesByType.get("FormalBottoms"), allClothesByType.get("FormalOuterwears"), formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, allClothesByType.get("FormalTops"), selectedClothesByType.get("FormalBottoms"), allClothesByType.get("FormalOuterwears"), formalityType, sm, user.getEmail());
	        // Dress(F) + Outerwear(F)
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("FormalDresses"), allClothesByType.get("FormalOuterwears"), formalityType, sm, user.getEmail());
	    
	    } else if ("T02".equals(formalityTypeId)) { // Rule 2: Semi-formal
	        List<ClothingItem> validOuterwears = new ArrayList<>();
	        validOuterwears.addAll(allClothesByType.get("FormalOuterwears"));
	        validOuterwears.addAll(allClothesByType.get("SemiFormalOuterwears"));
	        
	        // Top(F) + Bottom(S)
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("FormalTops"), allClothesByType.get("SemiFormalBottoms"), formalityType, sm, user.getEmail());
	        generateTwoItemStyles(matchedStyles, allClothesByType.get("FormalTops"), selectedClothesByType.get("SemiFormalBottoms"), formalityType, sm, user.getEmail());
	        // Top(S) + Bottom(F)
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("SemiFormalTops"), allClothesByType.get("FormalBottoms"), formalityType, sm, user.getEmail());
	        generateTwoItemStyles(matchedStyles, allClothesByType.get("SemiFormalTops"), selectedClothesByType.get("FormalBottoms"), formalityType, sm, user.getEmail());
	        // Top(S) + Bottom(S)
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("SemiFormalTops"), allClothesByType.get("SemiFormalBottoms"), formalityType, sm, user.getEmail());
	        generateTwoItemStyles(matchedStyles, allClothesByType.get("SemiFormalTops"), selectedClothesByType.get("SemiFormalBottoms"), formalityType, sm, user.getEmail());
	        // Dress(S) + Outerwear(F/S)
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("SemiFormalDresses"), validOuterwears, formalityType, sm, user.getEmail());
	        
	        // Combinations with Outerwear
	        generateThreeItemStyles(matchedStyles, selectedClothesByType.get("FormalTops"), allClothesByType.get("SemiFormalBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, allClothesByType.get("FormalTops"), selectedClothesByType.get("SemiFormalBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, selectedClothesByType.get("SemiFormalTops"), allClothesByType.get("FormalBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, allClothesByType.get("SemiFormalTops"), selectedClothesByType.get("FormalBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, selectedClothesByType.get("SemiFormalTops"), allClothesByType.get("SemiFormalBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, allClothesByType.get("SemiFormalTops"), selectedClothesByType.get("SemiFormalBottoms"), validOuterwears, formalityType, sm, user.getEmail());

	    } else if ("T03".equals(formalityTypeId)) { // Rule 3: Casual
	        List<ClothingItem> validOuterwears = new ArrayList<>();
	        validOuterwears.addAll(allClothesByType.get("SemiFormalOuterwears"));
	        validOuterwears.addAll(allClothesByType.get("CasualOuterwears"));

	        // Top(S) + Bottom(C)
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("SemiFormalTops"), allClothesByType.get("CasualBottoms"), formalityType, sm, user.getEmail());
	        generateTwoItemStyles(matchedStyles, allClothesByType.get("SemiFormalTops"), selectedClothesByType.get("CasualBottoms"), formalityType, sm, user.getEmail());
	        // Top(C) + Bottom(S)
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("CasualTops"), allClothesByType.get("SemiFormalBottoms"), formalityType, sm, user.getEmail());
	        generateTwoItemStyles(matchedStyles, allClothesByType.get("CasualTops"), selectedClothesByType.get("SemiFormalBottoms"), formalityType, sm, user.getEmail());
	        // Top(C) + Bottom(C)
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("CasualTops"), allClothesByType.get("CasualBottoms"), formalityType, sm, user.getEmail());
	        generateTwoItemStyles(matchedStyles, allClothesByType.get("CasualTops"), selectedClothesByType.get("CasualBottoms"), formalityType, sm, user.getEmail());
	        
	        // Dress Combinations
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("SemiFormalDresses"), validOuterwears, formalityType, sm, user.getEmail());
	        generateTwoItemStyles(matchedStyles, selectedClothesByType.get("CasualDresses"), validOuterwears, formalityType, sm, user.getEmail());

	        // Combinations with Outerwear
	        generateThreeItemStyles(matchedStyles, selectedClothesByType.get("SemiFormalTops"), allClothesByType.get("CasualBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, allClothesByType.get("SemiFormalTops"), selectedClothesByType.get("CasualBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, selectedClothesByType.get("CasualTops"), allClothesByType.get("SemiFormalBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, allClothesByType.get("CasualTops"), selectedClothesByType.get("SemiFormalBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, selectedClothesByType.get("CasualTops"), allClothesByType.get("CasualBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	        generateThreeItemStyles(matchedStyles, allClothesByType.get("CasualTops"), selectedClothesByType.get("CasualBottoms"), validOuterwears, formalityType, sm, user.getEmail());
	    }

	    // --- 4. จัดเรียงผลลัพธ์และส่งข้อมูลไปยัง View ---
	    sortMatchedStyles(matchedStyles);
	    sortClothingItemsByCategory(matchedStyles);

	    Collections.sort(selectedClothes, Comparator.comparing(item -> item.getSubCategory().getSubCategoryId()));

	    mav.addObject("selectedClothes", selectedClothes);
	    if (matchedStyles.isEmpty()) {
	        mav.addObject("err_msg", "ไม่พบการจับคู่ที่เหมาะสม");
	    } else {
	        mav.addObject("styles", matchedStyles);
	        mav.addObject("totalStyles", matchedStyles.size());
	    }

	    // เก็บผลลัพธ์และล้างค่าที่ไม่จำเป็นออกจาก session
	    session.setAttribute("matchedStyles", matchedStyles);
	    session.setAttribute("selectedCloth", selectedClothes);
	    session.removeAttribute("selectedClothes");
	    session.removeAttribute("selectedClothesMap");

	    return mav;
	}

	/**
	 * Helper Method: จัดกลุ่มเสื้อผ้าตาม Category และ Formality
	 * เพื่อให้ง่ายต่อการดึงไปใช้งานใน logic การจับคู่
	 */
	private Map<String, List<ClothingItem>> classifyClothes(List<ClothingItem> clothes) {
	    Map<String, List<ClothingItem>> clothesByType = new HashMap<>();
	    // สร้าง key ทั้งหมดไว้ล่วงหน้าเพื่อป้องกัน NullPointerException
	    String[] keys = {
	        "FormalTops", "SemiFormalTops", "CasualTops",
	        "FormalBottoms", "SemiFormalBottoms", "CasualBottoms",
	        "FormalDresses", "SemiFormalDresses", "CasualDresses",
	        "FormalOuterwears", "SemiFormalOuterwears", "CasualOuterwears"
	    };
	    for (String key : keys) {
	        clothesByType.put(key, new ArrayList<>());
	    }

	    for (ClothingItem item : clothes) {
	        String categoryId = item.getSubCategory().getCategory().getCategoryId();
	        String formalityId = item.getFormalityType().getTypeId();

	        if ("CG001".equals(categoryId)) { // Top
	            if ("T01".equals(formalityId)) clothesByType.get("FormalTops").add(item);
	            else if ("T02".equals(formalityId)) clothesByType.get("SemiFormalTops").add(item);
	            else if ("T03".equals(formalityId)) clothesByType.get("CasualTops").add(item);
	        } else if ("CG002".equals(categoryId)) { // Bottom
	            if ("T01".equals(formalityId)) clothesByType.get("FormalBottoms").add(item);
	            else if ("T02".equals(formalityId)) clothesByType.get("SemiFormalBottoms").add(item);
	            else if ("T03".equals(formalityId)) clothesByType.get("CasualBottoms").add(item);
	        } else if ("CG003".equals(categoryId)) { // Dress
	            if ("T01".equals(formalityId)) clothesByType.get("FormalDresses").add(item);
	            else if ("T02".equals(formalityId)) clothesByType.get("SemiFormalDresses").add(item);
	            else if ("T03".equals(formalityId)) clothesByType.get("CasualDresses").add(item);
	        } else if ("CG004".equals(categoryId)) { // Outerwear
	            if ("T01".equals(formalityId)) clothesByType.get("FormalOuterwears").add(item);
	            else if ("T02".equals(formalityId)) clothesByType.get("SemiFormalOuterwears").add(item);
	            else if ("T03".equals(formalityId)) clothesByType.get("CasualOuterwears").add(item);
	        }
	    }
	    return clothesByType;
	}


	/**
	 * Helper Method: สร้างชุดแบบ 2 ชิ้น (เช่น เสื้อ + กางเกง)
	 */
	private void generateTwoItemStyles(List<MatchStyle> matchedStyles, List<ClothingItem> list1, List<ClothingItem> list2, 
	                                   FormalityType formalityType, StyleManager sm, String userEmail) {
	    if (list1 == null || list2 == null || list1.isEmpty() || list2.isEmpty()) {
	        return; // ไม่ต้องทำอะไรถ้า list เป็นค่าว่าง
	    }
	    
	    for (ClothingItem item1 : list1) {
	        for (ClothingItem item2 : list2) {
	            if (item1.getClothid().equals(item2.getClothid())) continue;

	            MatchStyle potentialStyle = new MatchStyle();
	            potentialStyle.setFormalityType(formalityType);
	            potentialStyle.getClothingItems().add(item1);
	            potentialStyle.getClothingItems().add(item2);

	            if (!sm.isStyleAlreadyFavorited(potentialStyle, userEmail) && !isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                matchedStyles.add(potentialStyle);
	            }
	        }
	    }
	}

	/**
	 * Helper Method: สร้างชุดแบบ 3 ชิ้น (เช่น เสื้อ + กางเกง + เสื้อคลุม)
	 */
	private void generateThreeItemStyles(List<MatchStyle> matchedStyles, List<ClothingItem> list1, List<ClothingItem> list2, List<ClothingItem> list3,
	                                     FormalityType formalityType, StyleManager sm, String userEmail) {
	    if (list1 == null || list2 == null || list3 == null || list1.isEmpty() || list2.isEmpty() || list3.isEmpty()) {
	        return; // ไม่ต้องทำอะไรถ้า list ใด list หนึ่งเป็นค่าว่าง
	    }

	    for (ClothingItem item1 : list1) {
	        for (ClothingItem item2 : list2) {
	            if (item1.getClothid().equals(item2.getClothid())) continue;
	            for (ClothingItem item3 : list3) {
	                if (item1.getClothid().equals(item3.getClothid()) || item2.getClothid().equals(item3.getClothid())) continue;

	                MatchStyle potentialStyle = new MatchStyle();
	                potentialStyle.setFormalityType(formalityType);
	                potentialStyle.getClothingItems().add(item1);
	                potentialStyle.getClothingItems().add(item2);
	                potentialStyle.getClothingItems().add(item3);

	                if (!sm.isStyleAlreadyFavorited(potentialStyle, userEmail) && !isStyleAlreadyAdded(matchedStyles, potentialStyle)) {
	                    matchedStyles.add(potentialStyle);
	                }
	            }
	        }
	    }
	}

	/**
	 * Helper Method: ตรวจสอบว่าชุดใหม่ (newStyle) มีเสื้อผ้าซ้ำกับชุดที่มีอยู่แล้วในลิสต์ (existingStyles) หรือไม่
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

	// --- โค้ดส่วนท้ายของเมธอด (การเรียงลำดับ) ยังคงเหมือนเดิม แต่แยกออกมาเป็นเมธอดเพื่อความชัดเจน ---

	private void sortClothingItemsByCategory(List<MatchStyle> matchedStyles) {
	    for (MatchStyle style : matchedStyles) {
	        Collections.sort(style.getClothingItems(), new Comparator<ClothingItem>() {
	            @Override
	            public int compare(ClothingItem item1, ClothingItem item2) {
	                String cat1 = item1.getSubCategory().getCategory().getCategoryId();
	                String cat2 = item2.getSubCategory().getCategory().getCategoryId();
	                return Integer.compare(getCategoryOrder(cat1), getCategoryOrder(cat2));
	            }

	            private int getCategoryOrder(String categoryId) {
	                switch (categoryId) {
	                    case "CG004": return 1; // Outerwear
	                    case "CG001": return 2; // Top
	                    case "CG002": return 3; // Bottom
	                    case "CG003": return 4; // Dress
	                    default: return 5;
	                }
	            }
	        });
	    }
	}

	private void sortMatchedStyles(List<MatchStyle> matchedStyles) {
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
