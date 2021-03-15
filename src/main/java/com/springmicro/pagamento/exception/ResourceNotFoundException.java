package com.springmicro.pagamento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {


	private static final long serialVersionUID = 8374803010969862068L;

	public ResourceNotFoundException(String exception) {
		super(exception);
	}
	
}
