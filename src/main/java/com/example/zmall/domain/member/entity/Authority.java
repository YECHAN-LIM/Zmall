package com.example.zmall.domain.member.entity;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@ToString(exclude="member")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@IdClass(AuthorityId.class)
public class Authority {
	@Id
	@ManyToOne
	@JoinColumn(name="username")
	private Member member;
	
	@Id
	private String authorityName;
}
//@EqualsAndHashCode 
// equals와 hashcode를 자동으로 생성해주는 어노테이션. 
