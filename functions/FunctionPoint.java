package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable {
    private double x; //координата по оси x
    private double y; //координата по оси y

    public FunctionPoint(double x, double y) {//конструктор с двумя параметрами
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point) {//конструктор копирования
        this.x = point.x;
        this.y = point.y;
    }

    public FunctionPoint() {//конструктор по умолчанию (0;0)
        this.x = 0;
        this.y = 0;
    }

    public double getX() {//геттер для x
        return x;
    }

    public void setX(double x) {//сеттер для x
        this.x = x;
    }

    public double getY() { //геттер для y
        return y;
    }

    public void setY(double y) {//cеттер для y
        this.y = y;
    }

    // Переопределение метода toString()
    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    // Переопределение метода equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionPoint that = (FunctionPoint) o;

        // Сравнение с учетом точности чисел с плавающей точкой
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    // Переопределение метода hashCode()
    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);

        int xHash = (int)(xBits ^ (xBits >>> 32));
        int yHash = (int)(yBits ^ (yBits >>> 32));

        return xHash ^ yHash;
    }

    // Переопределение метода clone()
    @Override
    public Object clone() {
        return new FunctionPoint(this.x, this.y);
    }
}