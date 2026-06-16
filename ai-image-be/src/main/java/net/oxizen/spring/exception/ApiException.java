package net.oxizen.spring.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
	final private HttpStatus status;
	final private Body body;

	public ApiException(HttpStatus status, String code, String message) {
		super(message);
		this.status = status;
		body = new Body(code, message);
	}

	public ApiException(HttpStatus status, String code, String message, Object data) {
		super(message);
		this.status = status;
		body = new Body(code, message, data);
	}

	public ApiException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		body = new Body(this.status.toString(), message);
	}

	public ApiException(String message) {
		super(message);
		this.status = HttpStatus.BAD_REQUEST;
		body = new Body(this.status.toString(), message);
	}

	public ApiException(HttpStatus status) {
		super();
		this.status = status;
		body = null;
	}

	public Body getBody() {
		return body;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public static class Body {
		final private String code;
		final private String message;
		private Object data;

		private Body(String code, String message) {
			this.code = code;
			this.message = message;
		}

		private Body(String code, String message, Object data) {
			this.code = code;
			this.message = message;
			this.data = data;
		}

		public String getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}

		public Object getData() {
			return data;
		}
	}

}
