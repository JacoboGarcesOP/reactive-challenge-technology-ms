package co.com.bancolombia.usecase.exception;

public class BussinessException extends RuntimeException {
  public BussinessException(String message) {
    super(message);
  }

  public BussinessException(String message, Throwable cause) {
    super(message, cause);
  }

  public BussinessException(Throwable cause) {
    super(cause);
  }
}
