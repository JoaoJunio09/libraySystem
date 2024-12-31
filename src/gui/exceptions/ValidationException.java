package gui.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> errors = new HashMap<>();
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	public void addError(String field1, String field2) {
		errors.put(field1, field2);
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
