package com.example.zmall.security;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.web.servlet.mvc.support.*;

import com.example.zmall.domain.member.entity.*;

import lombok.*;

@RequiredArgsConstructor
@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private final MemberRepository dao;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		// 로그인에 실패한 경우 오류 메시지를 세션에 담아서 전달(RedirectAttribute를 사용할 수 없다)
		HttpSession session = request.getSession();
		//아이디가 없는 경우 
		if(exception instanceof InternalAuthenticationServiceException) 
			session.setAttribute("msg", "아이디를 확인하세요");
		else if(exception instanceof BadCredentialsException) {
			// 로그인에 실패했으므로 인증 객체는 존재하지 않는다. 아이디는 request.getParameter()로 꺼내오자 
			String username = request.getParameter("username");
			// Optional.get() -> null인 경우 NoSuchElementException 발생
			Member member = dao.findById(username).get();
			
			//로그인 실패 횟수와 아이디 활성화 업데이트는 member에서 처리 
			member.loginFail();
			
			// 세션에 오류 메시지를 저장
			if(member.getLoginFailCnt()<4) 
				session.setAttribute("msg", "로그인에 " + member.getLoginFailCnt() + "회 실패했습니다");
			 else 
				session.setAttribute("msg", "로그인에 5회이상 실패해 계정이 블록되었습니다");
		} else if(exception instanceof DisabledException) 
			session.setAttribute("msg", "블록된 계정입니다. 관리자에게 연락하세요");
		new DefaultRedirectStrategy().sendRedirect(request, response, "/member/login?error");
	}
}





