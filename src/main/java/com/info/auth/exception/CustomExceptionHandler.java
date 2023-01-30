package com.info.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.info.auth.model.Message;
import com.info.auth.model.RestResponse;
import com.info.auth.util.RestHelper;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler({ ApplicationException.class })
	@ResponseBody
	public ResponseEntity<RestResponse> handleEmptyListException(final Exception ex) {
		Message msg = resolveError(ex);
		return RestHelper.responseError(msg.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	private static Message resolveError(final Exception e) {
		return new Message(e.getMessage());
	}

}
