package functions;

public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {
    public FunctionPointIndexOutOfBoundsException() {
        super("Индекс точки вне диапазона допустимых значений");
    }

    public FunctionPointIndexOutOfBoundsException(String message) {
        super(message);
    }
}
