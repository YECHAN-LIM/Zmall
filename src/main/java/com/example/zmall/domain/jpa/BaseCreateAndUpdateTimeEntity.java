package com.example.zmall.domain.jpa;

import java.time.*;

import javax.persistence.*;

import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseCreateAndUpdateTimeEntity {
	@JsonFormat(pattern = "yyyy-MM--dd hh:mm:ss")
	@CreatedDate
	private LocalDateTime createTime;
	
	@JsonFormat(pattern = "yyyy-MM--dd hh:mm:ss")
	@LastModifiedDate
	private LocalDateTime updateTime;
}
