package com.example.zmall.web.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class BindExceptionMessage {
	private String fieldName;
	private String errorMessage;
}
