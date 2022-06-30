package com.example.zmall.security;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.security.core.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.savedrequest.*;
import org.springframework.stereotype.*;


import com.example.zmall.domain.member.entity.*;

import lombok.*;

@RequiredArgsConstructor
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final MemberRepository dao;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		Member member= dao.findById(authentication.getName()).get();
		member.loginSuccess();
		
		
		// RequestCache : 사용자가 가려던 목적지를 저장하는 인터페이스
		SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
					
		RedirectStrategy rs = new DefaultRedirectStrategy();
		
		String password = request.getParameter("password");
		
		if(password.length()>=20)
			rs.sendRedirect(request, response, "/member/change_password");
		else {
			if(savedRequest!=null)
				rs.sendRedirect(request, response, savedRequest.getRedirectUrl());
			else
				rs.sendRedirect(request, response, "/");
		}
		// 임시비밀번호를 이용한 로그인인 경우 /member/change_password로 이동
		// 정상비밀번호를 이용한 로그인인 경우
		//		이동할 주소가 있으면 그곳으로
		//		없으면 /로 보내자
		
	}
}