package com.ecomerce.src.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
		OffsetDateTime timestamp,
		int status,
		String error,
        String message,
        String message2,
		String path) {
}