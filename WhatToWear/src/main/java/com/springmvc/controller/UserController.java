package com.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springmvc.manager.UserManager;
import com.springmvc.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String loadIndexPage() {	
		return "index";
	}
	
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String loadRegisterPage() {	
		return "register_page";
	}
	
	@RequestMapping(value = "register_form", method = RequestMethod.POST)
	public ModelAndView registerUser(HttpServletRequest request) {
	    ModelAndView mv = new ModelAndView();
	    UserManager um = new UserManager();
	    User user = new User();
	    boolean result;

	    String email = request.getParameter("email").trim();
	    String username = request.getParameter("username").trim();
	    int gender = Integer.parseInt(request.getParameter("gender"));
	    String pwd = request.getParameter("pwd").trim();

	    try {
	        pwd = PasswordUtil.getInstance().createPassword(pwd, "1234");
	        user.setPassword(pwd);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    
	    user.setEmail(email);
	    user.setUsername(username);
	    user.setGender(gender);
	    user.setPassword(pwd);

	    result = um.saveorUpdateUser(user);
	    if (result) {
	        mv.setViewName("redirect:/login?register=success");
	    } else {
	        mv.addObject("showAlert", true);
	        mv.addObject("alertType", "error");
	        mv.addObject("alertTitle", "ไม่สำเร็จ!");
	        mv.addObject("alertMessage", "ไม่สามารถลงทะเบียนได้ กรุณาลองใหม่อีกครั้ง");
	        mv.setViewName("register_page");
	    }
	    return mv;
	}

	
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String loadLoginPage() {	
		return "login_page";
	}
	
	@RequestMapping(value = "login_form", method = RequestMethod.POST)
	public ModelAndView loginUser(HttpServletRequest request, HttpSession session) {	
		ModelAndView mv = new ModelAndView();
		UserManager um = new UserManager();
		
		String email = request.getParameter("email");
		String pwd = request.getParameter("pwd");
		
	    try {
	        pwd = PasswordUtil.getInstance().createPassword(pwd, "1234");
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    
		User user = um.getLogin(email);

		if(user != null && user.getPassword().equals(pwd)) {
			session.setAttribute("user", user);
	        session.setMaxInactiveInterval(15 * 60);
	        mv.addObject("user", session.getAttribute("user"));
	        mv.addObject("showAlert", true);
	        mv.addObject("alertType", "success");
	        mv.addObject("alertTitle", "สำเร็จ!");
	        mv.addObject("alertMessage", "เข้าสู่ระบบเสร็จสมบูรณ์");
	        mv.setViewName("index"); 
		} else {
	        mv.addObject("showAlert", true);
	        mv.addObject("alertType", "error");
	        mv.addObject("alertTitle", "ไม่สำเร็จ!");
	        mv.addObject("alertMessage", "ไม่สามารถเข้าสู่ระบบได้ กรุณาลองใหม่อีกครั้ง");
			mv.setViewName("login_page");
		}
		return mv;
	}
	
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public ModelAndView logoutProfile(HttpSession session) {	
		session.invalidate();
		return new ModelAndView("redirect:/");
	}
	
	@RequestMapping(value = "yourprofile", method = RequestMethod.GET)
	public String loadProfilePage(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user  == null) {
			return "index";
		} else {
			return "profile_page";
		}
	}
	
	@RequestMapping(value = "update_profile", method = RequestMethod.POST)
	public ModelAndView updateUser(HttpServletRequest request, HttpSession session) {
	    ModelAndView mv = new ModelAndView();
	    UserManager um = new UserManager();
	    boolean result = false;

	    try {
	        User currentUser = (User) session.getAttribute("user");
	        if (currentUser == null) {
	            mv.setViewName("redirect:/login");
	            return mv;
	        }
	        
	        String email = request.getParameter("email").trim();
	        String username = request.getParameter("username").trim();
	        int gender = Integer.parseInt(request.getParameter("gender"));
	        String pwd = request.getParameter("pwd");

	        User updatedUser = new User();
	        updatedUser.setEmail(email);
	        updatedUser.setUsername(username);
	        updatedUser.setGender(gender);
	        
	        if (pwd != null && !pwd.trim().isEmpty()) {
	            try {
	                String encryptedPwd = PasswordUtil.getInstance().createPassword(pwd, "1234");
	                updatedUser.setPassword(encryptedPwd);
	            } catch (Exception ex) {
	                ex.printStackTrace();
	                mv.addObject("showAlert", true);
	                mv.addObject("alertType", "error");
	                mv.addObject("alertTitle", "ผิดพลาด!");
	                mv.addObject("alertMessage", "เกิดข้อผิดพลาดในการเข้ารหัสรหัสผ่าน กรุณาลองใหม่อีกครั้ง");
	                mv.setViewName("profile_edit");
	                return mv;
	            }
	        } else {
	            updatedUser.setPassword(currentUser.getPassword());
	        }

	        result = um.updateUser(updatedUser);

	        if (result) {
	            session.setAttribute("user", updatedUser);
	            mv.setViewName("redirect:/yourprofile?update=success");
	        } else {
	            mv.addObject("showAlert", true);
	            mv.addObject("alertType", "error");
	            mv.addObject("alertTitle", "ไม่สำเร็จ!");
	            mv.addObject("alertMessage", "ไม่สามารถแก้ไขข้อมูลได้ กรุณาลองใหม่อีกครั้ง");
	            mv.addObject("user", currentUser);
	            mv.setViewName("profile_edit");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        mv.addObject("showAlert", true);
	        mv.addObject("alertType", "error");
	        mv.addObject("alertTitle", "ผิดพลาด!");
	        mv.addObject("alertMessage", "เกิดข้อผิดพลาดในระบบ กรุณาลองใหม่อีกครั้ง");
	        mv.setViewName("profile_page");
	    }

	    return mv;
	}
}
