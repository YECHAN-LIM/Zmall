package com.example.zmall.domain.member.entity;

import java.time.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import org.springframework.format.annotation.*;

import com.example.zmall.domain.jpa.*;
import com.fasterxml.jackson.annotation.*;

import lombok.*;
import lombok.experimental.*;


// 클래스의 필드를 초기화하는 방법 : instance 초기화 -> static 초기화 -> 생성자
// lombok의 빌더를 사용하면 instance 초기화 동작 안함
@Getter
@Setter
@ToString(exclude="authorities")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain=true)
@Entity
@DynamicUpdate
public class Member extends BaseCreateTimeEntity {
	@PrePersist
	public void init() {
		buyCount = 0;
		buyMoney = 0;
		loginFailCnt = 0;
		enabled = false;
		level = Level.BRONZE;
	}
	
	@Id
	@Column(length=10)
	private String username;
	
	@Column(length=10)
	private String irum;
	
	@Column(length=30)
	private String email;
	
	@JsonIgnore
	@Column(length=60)
	private String password;
	
	//@DateTimeFormat(pattern="yyyy-MM-dd")
	//@JsonFormat(pattern = "yyyy년 MM월 dd일")			사용자  -> 스프링 ProperyEditor			<- MessageConverter
	private LocalDate birthday;
	
	private String profile;
	
	private Integer buyCount;
	
	private Integer buyMoney;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name="levels")
	private Level level;
	
	@JsonIgnore
	private Integer loginFailCnt;
	
	@JsonIgnore
	private Boolean enabled;
	
	@JsonIgnore
	@Column(length=20)
	private String checkcode;
	
	@JsonIgnore
	@OneToMany(mappedBy="member", cascade={CascadeType.MERGE, CascadeType.REMOVE})
	private Set<Authority> authorities;
	
	//DB에 저장되지 않는 필드 
	@Transient
	private Long days;
	
	public void addJoinInfo(String profile, String checkcode, String encodedPassword, List<String> authorities) {
		if(this.authorities==null)
			this.authorities = new HashSet<Authority>();
		this.profile = profile;
		this.checkcode = checkcode;
		this.password = encodedPassword;
		authorities.forEach(authorityName->this.authorities.add(new Authority(this, authorityName)));
	}
	
	public void loginFail() {
		if(this.loginFailCnt<4)
			this.loginFailCnt++;
		else {
			this.loginFailCnt++;
			this.enabled = false;
		}
	}
	public void loginSuccess() {
		this.loginFailCnt = 0;
		this.enabled = true;
	}
	
}













