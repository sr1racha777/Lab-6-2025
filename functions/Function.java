package functions;

public interface Function {
    // Возвращает левую границу области определения функции
    double getLeftDomainBorder();

    // Возвращает правую границу области определения функции
    double getRightDomainBorder();

    // Возвращает значение функции в точке x
    double getFunctionValue(double x);
}
