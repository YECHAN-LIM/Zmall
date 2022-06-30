package com.example.zmall.security;

import java.util.*;
import java.util.stream.*;

import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.bind.annotation.*;

import com.example.zmall.domain.member.entity.*;


import lombok.*;


@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository dao;

	@Transactional(readOnly=true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		Member member = dao.findById(username).orElseThrow(()-> new InternalAuthenticationServiceException("USER NOT FOUND"));
		
		Account account = Account.builder().username(member.getUsername()).password(member.getPassword()).isEnabled(member.getEnabled()).build();

		Collection<GrantedAuthority> authorities = member.getAuthorities().stream().map(a->new SimpleGrantedAuthority(a.getAuthorityName())).collect(Collectors.toList());
		
		account.setAuthorities(authorities);
		return account;
	}

	
}