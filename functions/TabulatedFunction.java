package functions;

public interface TabulatedFunction extends Function, Cloneable {
    //возвращает количество точек в функции
    int getPointsCount();

    //возвращает точку по указанному индексу
    //выбрасывает исключение, если индекс выходит за границы
    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;

    //заменяет точку по указанному индексу
    //выбрасывает исключения при недопустимом индексе или нарушении порядка точек
    void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    //возвращает координату x точки по указанному индексу
    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;

    //устанавливает координату x точки по указанному индексу
    //проверяет сохранение упорядоченности точек по x
    void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    //возвращает координату y точки по указанному индексу
    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;

    //устанавливает координату y точки по указанному индексу
    void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException;

    //добавляет новую точку в функцию
    //проверяет отсутствие дублирования координат x и сохраняет упорядоченность
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;

    //удаляет точку по указанному индексу
    //требует наличия минимум 2 точек после удаления
    void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException;

    //возвращает копию объекта табулированной функции
    Object clone();
}