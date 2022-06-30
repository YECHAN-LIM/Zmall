package com.example.zmall.web.controller.rest;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;

import javax.servlet.http.*;
import javax.validation.*;

import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.logout.*;
import org.springframework.validation.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.*;

import com.example.zmall.domain.member.service.*;
import com.example.zmall.util.*;
import com.example.zmall.web.dto.*;
import com.example.zmall.util.*;

import lombok.*;

@RequiredArgsConstructor
@RestController
public class MemberController {
	private final MemberService service;
	
	// 아이디 사용 여부
	@GetMapping(path="/members/username/check", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> idAvailableCheck(@RequestParam @NonNull String username) {
		service.idAvailabelCheck(username);
		return ResponseEntity.ok(ZmallResponseConstant.ID_AVAILABLE_MSG);
	}
	
	// 이메일 사용 여부
	@GetMapping(path="/members/email/check", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> emailAvailableCheck(@RequestParam @NonNull String email) {
		service.emailAvailabelCheck(email);
		return ResponseEntity.ok(ZmallResponseConstant.EMAIL_AVAILABLE_MSG);
	}
	
	// 회원 가입
	@PostMapping(path="/members/new", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> join(@ModelAttribute @Valid MemberDto.Join dto, BindingResult bindingResult) throws BindException {
		if(bindingResult.hasErrors())
			throw new BindException(bindingResult);
		service.join(dto);
		return ResponseEntity.ok(ZmallResponseConstant.JOIN_MSG);
	}
	
	// 회원 가입 확인 : 체크 코드를 확인한 다음 REST로 화면 이동을 시키자
	// 300대 상태 코드는 redirect를 의미한다
	@GetMapping("/members/join/check")
	public ResponseEntity<?> joinCheck(@RequestParam @NonNull String checkcode) {
		service.joinCheck(checkcode);
		URI uri = UriComponentsBuilder.newInstance().path("/member/login").build().toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}
	
	// 아이디 찾기
	@GetMapping(path="/members/member/email", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> findId(@RequestParam @NonNull String email) {
		URI uri = UriComponentsBuilder.newInstance().path("/member/login").build().toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}
	
	// 비밀번호 리셋
	@PatchMapping(path="/members/member/password/temp", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> resetPassword(@ModelAttribute @Valid MemberDto.ResetPwd dto, BindingResult bindingResult) throws BindException {
		if(bindingResult.hasErrors())
			throw new BindException(bindingResult);
		service.resetPassword(dto);
		return ResponseEntity.ok(ZmallResponseConstant.RESET_PASSWORD_MSG);
	}
	
	// 임시 비밀번호로 로그인했을 때 비밀번호 변경
	@PreAuthorize("isAuthenticated()")
	@PatchMapping(path="/members/member/password", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> changePassword(@ModelAttribute @Valid MemberDto.ChangePwd dto, BindingResult bindingResult, Principal principal) throws BindException {
		if(bindingResult.hasErrors())
			throw new BindException(bindingResult);
		service.changePassword(dto, principal.getName());
		return ResponseEntity.ok(ZmallResponseConstant.CHANGE_PASSWORD_MSG);
	}
	
	//비밀번호 확인
	@PreAuthorize("isAuthenticated()")
	@GetMapping(path="/members/member/password/check", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> checkPassword(@RequestParam String password, Principal principal, HttpSession session) {
		service.checkPassword(password, principal.getName());
		session.setAttribute("passwordCheck", true);
		return ResponseEntity.ok(null);
	}
	
	// 내 정보 보기
	@PreAuthorize("isAuthenticated()")
	@GetMapping(path="/members/member", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<?> read(Principal principal, HttpSession session) {
		return ResponseEntity.ok(service.read(principal.getName()));
	}
	
	
	// 내 정보 변경
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/members/member")
	public ResponseEntity<?> update(@ModelAttribute @Valid MemberDto.Update dto, BindingResult bindingResult, Principal principal) throws BindException {
		if(bindingResult.hasErrors())
			throw new BindException(bindingResult);
		service.update(dto, principal.getName());
		return ResponseEntity.ok(null);
	}
	
	//회원 탈퇴 후 루트 페이지로 이동(회원 탈퇴 버튼이 내정보에 있다. 따라서 루트로 강제이동 시키자)
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/members/member")
	public ResponseEntity<?> resign(SecurityContextLogoutHandler handler, HttpServletRequest req, HttpServletResponse res, Authentication authentication) {
		service.resign(authentication.getName());
		handler.logout(req, res, authentication);
		return ResponseEntity.ok(null);
	}

	
	// 프사 출력
	@GetMapping(path="/display", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<?> showProfile(@RequestParam String imagename) {
		File file = new File(ZmallConstant.PROFILE_FOLDER + imagename);
		// 헤더를 만들어 MediaType을 이미지로 지정하고 Content-Disposition을 inline으로 주면 브라우저가 처리한다
		// Content-Disposition를 attachment로 브라우저가 다운로드한다
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(ZmallUtil.getMediaType(imagename));
		headers.add("Content-Disposition", "inline;filename="  + imagename);
		try {
			// 파일을 바이트 배열로 바꾸는 방법들이 여러가지 나오는데...
			return ResponseEntity.ok().headers(headers).body(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
