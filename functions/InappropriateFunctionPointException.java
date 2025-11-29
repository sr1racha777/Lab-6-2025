package functions;

public class InappropriateFunctionPointException extends RuntimeException {
    public InappropriateFunctionPointException() {
        super("Некорректная операция с точкой функции");
    }

    public InappropriateFunctionPointException(String message) {
        super(message);
    }
}
