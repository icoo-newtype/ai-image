package com.aiimage.exception;

import com.nhncorp.lucy.security.xss.XssPreventer;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiException extends RuntimeException {
	final transient private HttpStatus status;
	final transient private Body body;

	public ApiException(HttpStatus status, String code, String message) {
		super(message);
		this.status = status;
		body = new Body(code, message);
	}

	public ApiException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		body = new Body(this.status.toString(), message);
	}

	public ApiException(String code, String message) {
		super(message);
		this.status = HttpStatus.BAD_REQUEST;
		body = new Body(code, message);
	}

	@Data
	public static class Body {
		private String code;
		private String message;

		private Body(String code, String message) {
			this.code = code;
			this.message = message;
		}
	}

	public ResponseEntity<?> getEntity() {
		Body result = new Body(XssPreventer.escape(body.getCode()), XssPreventer.escape(body.getMessage()));
		return ResponseEntity.status(status).body(result);
	}
}
