package com.example.zmall.web.dto;

import java.time.*;

import javax.validation.constraints.*;

import org.springframework.format.annotation.*;
import org.springframework.web.multipart.*;

import com.example.zmall.domain.member.entity.*;

import lombok.*;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class MemberDto {
	@Data
	@AllArgsConstructor
	public static class Join {
		@NotBlank
		@Pattern(regexp="^[A-Z0-9]{8,10}$", message="아이디는 대문자와 숫자 8~10자입니다")
		private String username;
		
		@NotBlank
		@Pattern(regexp="^[가-힣]{2,10}$", message="아이디는 한글 2~10자입니다")
		private String irum;
		
		@NotBlank
		@Email
		private String email;
		
		private String password;
		
		@DateTimeFormat(pattern="yyyy-MM-dd")
		private LocalDate birthday;
		
		private MultipartFile sajin;

		public Member toEntity() {
			return Member.builder().username(username).irum(irum).email(email).password(password).birthday(birthday).build();
		}
	}

	@Data
	public class Update {
		private String email;
		
		private String password;
		
		private String newPassword;
		
		private LocalDate birthday;
		
		private MultipartFile sajin;

		public boolean passwordCheck() {
			if(this.password!=null && this.newPassword!=null) 
				return this.password.equals(this.newPassword);
			return false;
		}
	}
	
	@Data
	public class ResetPwd {
		private String username;
		private String email;
	}
	
	@Data
	public class ChangePwd {
		private String password;
		private String newPassword;
	}
}
