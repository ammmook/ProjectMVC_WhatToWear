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

	    // --- Part 1: Session Data & User Input Processing ---
	    clearSessionAttributes(session);
	    
	    Map<String, List<String>> selectedClothesMap = getOrCreateSelectedClothesMap(session);
	    String formalityTypeId = request.getParameter("formality_type");

	    List<String> allSelectedClothesIds = new ArrayList<>();
	    for (List<String> clothesList : selectedClothesMap.values()) {
	        allSelectedClothesIds.addAll(clothesList);
	    }
	    boolean hasSelectedClothes = !allSelectedClothesIds.isEmpty();
	    String[] selectedClothesIds = allSelectedClothesIds.toArray(new String[0]);

	    ClothingManager cm = new ClothingManager();
	    StyleManager sm = new StyleManager();
	    List<ClothingItem> selectedClothes = hasSelectedClothes ? cm.getClothingItemsByIds(selectedClothesIds, user.getEmail()) : cm.getClothesByEmail(user.getEmail());
	    List<ClothingItem> allUserClothes = cm.getClothesByEmail(user.getEmail());

	    // --- Part 2: Categorize Clothing Items ---
	    Map<String, List<ClothingItem>> selectedItemsByFormality = categorizeClothes(selectedClothes);
	    Map<String, List<ClothingItem>> allItemsByFormality = categorizeClothes(allUserClothes);
	    
	    List<ClothingItem> selectedFormalTops = selectedItemsByFormality.getOrDefault("T01_CG001", new ArrayList<>());
	    List<ClothingItem> selectedSemiFormalTops = selectedItemsByFormality.getOrDefault("T02_CG001", new ArrayList<>());
	    List<ClothingItem> selectedCasualTops = selectedItemsByFormality.getOrDefault("T03_CG001", new ArrayList<>());
	    List<ClothingItem> selectedFormalBottoms = selectedItemsByFormality.getOrDefault("T01_CG002", new ArrayList<>());
	    List<ClothingItem> selectedSemiFormalBottoms = selectedItemsByFormality.getOrDefault("T02_CG002", new ArrayList<>());
	    List<ClothingItem> selectedCasualBottoms = selectedItemsByFormality.getOrDefault("T03_CG002", new ArrayList<>());
	    List<ClothingItem> selectedFormalDresses = selectedItemsByFormality.getOrDefault("T01_CG003", new ArrayList<>());
	    List<ClothingItem> selectedSemiFormalDresses = selectedItemsByFormality.getOrDefault("T02_CG003", new ArrayList<>());
	    List<ClothingItem> selectedCasualDresses = selectedItemsByFormality.getOrDefault("T03_CG003", new ArrayList<>());
	    List<ClothingItem> selectedFormalOuterwears = selectedItemsByFormality.getOrDefault("T01_CG004", new ArrayList<>());
	    List<ClothingItem> selectedSemiFormalOuterwears = selectedItemsByFormality.getOrDefault("T02_CG004", new ArrayList<>());
	    List<ClothingItem> selectedCasualOuterwears = selectedItemsByFormality.getOrDefault("T03_CG004", new ArrayList<>());
	    
	    List<ClothingItem> allFormalTops = allItemsByFormality.getOrDefault("T01_CG001", new ArrayList<>());
	    List<ClothingItem> allSemiFormalTops = allItemsByFormality.getOrDefault("T02_CG001", new ArrayList<>());
	    List<ClothingItem> allCasualTops = allItemsByFormality.getOrDefault("T03_CG001", new ArrayList<>());
	    List<ClothingItem> allFormalBottoms = allItemsByFormality.getOrDefault("T01_CG002", new ArrayList<>());
	    List<ClothingItem> allSemiFormalBottoms = allItemsByFormality.getOrDefault("T02_CG002", new ArrayList<>());
	    List<ClothingItem> allCasualBottoms = allItemsByFormality.getOrDefault("T03_CG002", new ArrayList<>());
	    List<ClothingItem> allFormalOuterwears = allItemsByFormality.getOrDefault("T01_CG004", new ArrayList<>());
	    List<ClothingItem> allSemiFormalOuterwears = allItemsByFormality.getOrDefault("T02_CG004", new ArrayList<>());
	    List<ClothingItem> allCasualOuterwears = allItemsByFormality.getOrDefault("T03_CG004", new ArrayList<>());
	    
	    // If no clothes were manually selected, categorize all user clothes.
	    if (!hasSelectedClothes) {
	        selectedFormalTops.addAll(allFormalTops);
	        selectedSemiFormalTops.addAll(allSemiFormalTops);
	        selectedCasualTops.addAll(allCasualTops);
	        selectedFormalBottoms.addAll(allFormalBottoms);
	        selectedSemiFormalBottoms.addAll(allSemiFormalBottoms);
	        selectedCasualBottoms.addAll(allCasualBottoms);
	        selectedFormalDresses.addAll(selectedItemsByFormality.getOrDefault("T01_CG003", new ArrayList<>()));
	        selectedSemiFormalDresses.addAll(selectedItemsByFormality.getOrDefault("T02_CG003", new ArrayList<>()));
	        selectedCasualDresses.addAll(selectedItemsByFormality.getOrDefault("T03_CG003", new ArrayList<>()));
	        selectedFormalOuterwears.addAll(allFormalOuterwears);
	        selectedSemiFormalOuterwears.addAll(allSemiFormalOuterwears);
	        selectedCasualOuterwears.addAll(allCasualOuterwears);
	    }
	    
	    // --- Part 3: Matching Logic ---
	    List<MatchStyle> matchedStyles = new ArrayList<>();
	    FormalityType formalityType = sm.getFormalityTypeById(formalityTypeId);

	    if ("T01".equals(formalityTypeId)) {
	        matchFormalStyles(selectedFormalTops, selectedFormalBottoms, selectedFormalDresses,
	            allFormalTops, allFormalBottoms, allFormalOuterwears, matchedStyles, formalityType, sm, user.getEmail());
	    } else if ("T02".equals(formalityTypeId)) {
	        matchSemiFormalStyles(selectedFormalTops, selectedSemiFormalTops, selectedFormalBottoms, selectedSemiFormalBottoms,
	            selectedSemiFormalDresses, allFormalTops, allSemiFormalTops, allFormalBottoms, allSemiFormalBottoms,
	            allFormalOuterwears, allSemiFormalOuterwears, matchedStyles, formalityType, sm, user.getEmail());
	    } else if ("T03".equals(formalityTypeId)) {
	        matchCasualStyles(selectedSemiFormalTops, selectedCasualTops, selectedSemiFormalBottoms, selectedCasualBottoms,
	            selectedSemiFormalDresses, selectedCasualDresses, allSemiFormalTops, allCasualTops, allSemiFormalBottoms,
	            allCasualBottoms, allSemiFormalOuterwears, allCasualOuterwears, matchedStyles, formalityType, sm, user.getEmail());
	    }
	    
	    // --- Part 4: Sorting & Final View Setup ---
	    sortClothingItemsInStyles(matchedStyles);
	    sortMatchedStyles(matchedStyles);
	    sortSelectedClothes(selectedClothes);
	    
	    mav.addObject("selectedClothes", selectedClothes);
	    if (matchedStyles.isEmpty()) {
	        mav.addObject("err_msg", "ไม่พบการจับคู่ที่เหมาะสม");
	    } else {
	        mav.addObject("styles", matchedStyles);
	        mav.addObject("totalStyles", matchedStyles.size());
	    }

	    session.setAttribute("matchedStyles", matchedStyles);
	    session.setAttribute("selectedCloth", selectedClothes);
	    session.removeAttribute("selectedClothes");
	    session.removeAttribute("selectedClothesMap");

	    return mav;
	}

	/**
	 * Clears old session attributes to prepare for a new matching run.
	 */
	private void clearSessionAttributes(HttpSession session) {
	    session.removeAttribute("matchedStyles");
	    session.removeAttribute("selectedCloth");
	}

	/**
	 * Retrieves or creates the map of selected clothes from the session.
	 */
	private Map<String, List<String>> getOrCreateSelectedClothesMap(HttpSession session) {
	    Map<String, List<String>> selectedClothesMap = (Map<String, List<String>>) session.getAttribute("selectedClothesMap");
	    if (selectedClothesMap == null) {
	        selectedClothesMap = new HashMap<>();
	    }
	    
	    List<String> currentSelection = (List<String>) session.getAttribute("selectedClothes");
	    String currentCategory = (String) session.getAttribute("selectedCates");
	    if (currentCategory == null) {
	        currentCategory = "CG001";
	    }
	    selectedClothesMap.put(currentCategory, currentSelection);
	    session.setAttribute("selectedClothesMap", selectedClothesMap);

	    return selectedClothesMap;
	}

	/**
	 * Categorizes a list of ClothingItem objects into a map based on their formality type and category.
	 */
	private Map<String, List<ClothingItem>> categorizeClothes(List<ClothingItem> clothes) {
	    Map<String, List<ClothingItem>> categorizedMap = new HashMap<>();
	    for (ClothingItem item : clothes) {
	        try {
	            String categoryId = item.getSubCategory().getCategory().getCategoryId();
	            String formalityId = item.getFormalityType().getTypeId();
	            String key = formalityId + "_" + categoryId;
	            categorizedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return categorizedMap;
	}

	/**
	 * Creates formal styles (T01).
	 */
	private void matchFormalStyles(List<ClothingItem> selectedFormalTops, List<ClothingItem> selectedFormalBottoms,
	                               List<ClothingItem> selectedFormalDresses, List<ClothingItem> allFormalTops,
	                               List<ClothingItem> allFormalBottoms, List<ClothingItem> allFormalOuterwears,
	                               List<MatchStyle> matchedStyles, FormalityType formalityType, StyleManager sm, String userEmail) {
	    // 1. Top(Formal) + Bottom(Formal)
	    addCombinations(selectedFormalTops, allFormalBottoms, matchedStyles, formalityType, sm, userEmail);
	    addCombinations(allFormalTops, selectedFormalBottoms, matchedStyles, formalityType, sm, userEmail);

	    // 2. Top(Formal) + Bottom(Formal) + Outerwear(Formal)
	    addThreePieceCombinations(selectedFormalTops, allFormalBottoms, allFormalOuterwears, matchedStyles, formalityType, sm, userEmail);
	    addThreePieceCombinations(allFormalTops, selectedFormalBottoms, allFormalOuterwears, matchedStyles, formalityType, sm, userEmail);

	    // 3. Dress(Formal) + Outerwear(Formal)
	    addCombinations(selectedFormalDresses, allFormalOuterwears, matchedStyles, formalityType, sm, userEmail);
	}

	/**
	 * Creates semi-formal styles (T02).
	 */
	private void matchSemiFormalStyles(List<ClothingItem> selectedFormalTops, List<ClothingItem> selectedSemiFormalTops,
	                                   List<ClothingItem> selectedFormalBottoms, List<ClothingItem> selectedSemiFormalBottoms,
	                                   List<ClothingItem> selectedSemiFormalDresses, List<ClothingItem> allFormalTops,
	                                   List<ClothingItem> allSemiFormalTops, List<ClothingItem> allFormalBottoms,
	                                   List<ClothingItem> allSemiFormalBottoms, List<ClothingItem> allFormalOuterwears,
	                                   List<ClothingItem> allSemiFormalOuterwears, List<MatchStyle> matchedStyles,
	                                   FormalityType formalityType, StyleManager sm, String userEmail) {
	    List<ClothingItem> validOuterwears = new ArrayList<>();
	    validOuterwears.addAll(allFormalOuterwears);
	    validOuterwears.addAll(allSemiFormalOuterwears);

	    // 1. Top(F) + Bottom(S)
	    addCombinations(selectedFormalTops, allSemiFormalBottoms, matchedStyles, formalityType, sm, userEmail);
	    addCombinations(allFormalTops, selectedSemiFormalBottoms, matchedStyles, formalityType, sm, userEmail);

	    // 2. Top(S) + Bottom(F)
	    addCombinations(selectedSemiFormalTops, allFormalBottoms, matchedStyles, formalityType, sm, userEmail);
	    addCombinations(allSemiFormalTops, selectedFormalBottoms, matchedStyles, formalityType, sm, userEmail);

	    // 3. Top(S) + Bottom(S)
	    addCombinations(selectedSemiFormalTops, allSemiFormalBottoms, matchedStyles, formalityType, sm, userEmail);
	    addCombinations(allSemiFormalTops, selectedSemiFormalBottoms, matchedStyles, formalityType, sm, userEmail);
	    
	    // 4. Top(F) + Bottom(S) + Outerwear(F/S)
	    addThreePieceCombinations(selectedFormalTops, allSemiFormalBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    addThreePieceCombinations(allFormalTops, selectedSemiFormalBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    
	    // 5. Top(S) + Bottom(F) + Outerwear(F/S)
	    addThreePieceCombinations(selectedSemiFormalTops, allFormalBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    addThreePieceCombinations(allSemiFormalTops, selectedFormalBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);

	    // 6. Top(S) + Bottom(S) + Outerwear(F/S)
	    addThreePieceCombinations(selectedSemiFormalTops, allSemiFormalBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    addThreePieceCombinations(allSemiFormalTops, selectedSemiFormalBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);

	    // 7. Dress(S) + Outerwear(F/S)
	    addCombinations(selectedSemiFormalDresses, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	}

	/**
	 * Creates casual styles (T03).
	 */
	private void matchCasualStyles(List<ClothingItem> selectedSemiFormalTops, List<ClothingItem> selectedCasualTops,
	                               List<ClothingItem> selectedSemiFormalBottoms, List<ClothingItem> selectedCasualBottoms,
	                               List<ClothingItem> selectedSemiFormalDresses, List<ClothingItem> selectedCasualDresses,
	                               List<ClothingItem> allSemiFormalTops, List<ClothingItem> allCasualTops,
	                               List<ClothingItem> allSemiFormalBottoms, List<ClothingItem> allCasualBottoms,
	                               List<ClothingItem> allSemiFormalOuterwears, List<ClothingItem> allCasualOuterwears,
	                               List<MatchStyle> matchedStyles, FormalityType formalityType, StyleManager sm, String userEmail) {
	    List<ClothingItem> validOuterwears = new ArrayList<>();
	    validOuterwears.addAll(allSemiFormalOuterwears);
	    validOuterwears.addAll(allCasualOuterwears);

	    // 1. Top(S) + Bottom(C)
	    addCombinations(selectedSemiFormalTops, allCasualBottoms, matchedStyles, formalityType, sm, userEmail);
	    addCombinations(allSemiFormalTops, selectedCasualBottoms, matchedStyles, formalityType, sm, userEmail);
	    
	    // 2. Top(C) + Bottom(S)
	    addCombinations(selectedCasualTops, allSemiFormalBottoms, matchedStyles, formalityType, sm, userEmail);
	    addCombinations(allCasualTops, selectedSemiFormalBottoms, matchedStyles, formalityType, sm, userEmail);

	    // 3. Top(C) + Bottom(C)
	    addCombinations(selectedCasualTops, allCasualBottoms, matchedStyles, formalityType, sm, userEmail);
	    addCombinations(allCasualTops, selectedCasualBottoms, matchedStyles, formalityType, sm, userEmail);
	    
	    // 4. Top(S) + Bottom(C) + Outerwear(S/C)
	    addThreePieceCombinations(selectedSemiFormalTops, allCasualBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    addThreePieceCombinations(allSemiFormalTops, selectedCasualBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    
	    // 5. Top(C) + Bottom(S) + Outerwear(S/C)
	    addThreePieceCombinations(selectedCasualTops, allSemiFormalBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    addThreePieceCombinations(allCasualTops, selectedSemiFormalBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    
	    // 6. Top(C) + Bottom(C) + Outerwear(S/C)
	    addThreePieceCombinations(selectedCasualTops, allCasualBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    addThreePieceCombinations(allCasualTops, selectedCasualBottoms, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    
	    // 7. Dress(S) + Outerwear(S/C)
	    addCombinations(selectedSemiFormalDresses, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	    
	    // 8. Dress(C) + Outerwear(S/C)
	    addCombinations(selectedCasualDresses, validOuterwears, matchedStyles, formalityType, sm, userEmail);
	}

	/**
	 * A generic helper method to add two-item combinations to the matched styles list.
	 */
	private void addCombinations(List<ClothingItem> list1, List<ClothingItem> list2, List<MatchStyle> matchedStyles,
	                             FormalityType formalityType, StyleManager sm, String userEmail) {
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
	 * A generic helper method to add three-item combinations to the matched styles list.
	 */
	private void addThreePieceCombinations(List<ClothingItem> list1, List<ClothingItem> list2, List<ClothingItem> list3,
	                                       List<MatchStyle> matchedStyles, FormalityType formalityType, StyleManager sm, String userEmail) {
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
	 * Sorts the clothing items within each matched style based on a predefined category order.
	 */
	private void sortClothingItemsInStyles(List<MatchStyle> matchedStyles) {
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

	/**
	 * Sorts the list of matched styles based on a custom priority (dress/non-dress and item count).
	 */
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

	/**
	 * Sorts the selected clothing items by sub-category ID.
	 */
	private void sortSelectedClothes(List<ClothingItem> selectedClothes) {
	    Collections.sort(selectedClothes, new Comparator<ClothingItem>() {
	        @Override
	        public int compare(ClothingItem item1, ClothingItem item2) {
	            String subcateId1 = item1.getSubCategory().getSubCategoryId();
	            String subcateId2 = item2.getSubCategory().getSubCategoryId();
	            return subcateId1.compareTo(subcateId2);
	        }
	    });
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
