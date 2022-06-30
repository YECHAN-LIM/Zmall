package com.example.zmall.web.controller.mvc;

import javax.servlet.http.*;

import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class MemberMvcController {
	
	@GetMapping("/")
	public String index(HttpSession session, Model model) {
		if(session.getAttribute("msg")!=null) {
			model.addAttribute("msg", session.getAttribute("msg"));
			session.removeAttribute("msg");
		}
		return "/index";
	}
	
	
	@PreAuthorize("isAnonymous()")
	@GetMapping("/member/join")
	public void join() {
	}
	
	
	@PreAuthorize("isAnonymous()")
	@GetMapping("/member/login")
	public void login() {
	}
	
	@PreAuthorize("isAnonymous()")
	@GetMapping("/member/find")
	public void find() {
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/member/change_password")
	public void changePassword() {
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/member/password_check")
	public String passwordCheck(HttpSession session) {
		// 비밀번호 확인 여부를 체크해서 확인을 이미 했다면 read로 보내버리자
		if(session.getAttribute("passwordCheck")!=null)
			return "redirect:/member/read";
		return "/member/password_check";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/member/read")
	public String read(HttpSession session) {
		// 비밀번호 확인 여부를 체크해서 확인을 안했다면 read로 보내버리자
		if(session.getAttribute("passwordCheck")==null)
			return "redirect:/member/password_check";
		return "/member/read";
	}
}
