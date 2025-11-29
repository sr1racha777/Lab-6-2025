package functions;

import java.io.*;

public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable {
    private static final long serialVersionUID = 1L;

    private FunctionPoint[] points;      // массив точек функции
    private int pointsCount;             // текущее количество точек
    private static final double EPS = Math.ulp(1.0); // машинный эпсилон для сравнения double

    // кэш последнего обращения
    private transient int lastAccessedIndex = -1;      // индекс последней использованной точки
    private transient FunctionPoint lastAccessedPoint = null; // последняя использованная точка

    // ОБЯЗАТЕЛЬНЫЙ конструктор без параметров для Externalizable
    public ArrayTabulatedFunction() {
    }

    // конструктор 1: равномерное распределение точек по X
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX)
            throw new IllegalArgumentException("левая граница >= правая граница");
        if (pointsCount < 2)
            throw new IllegalArgumentException("Кол-во точек < 2");

        this.pointsCount = pointsCount;
        points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1); // шаг по X между точками
        for (int i = 0; i < pointsCount; i++)
            points[i] = new FunctionPoint(leftX + i * step, 0); // создаем точки с Y=0
    }

    // конструктор 2: по массиву Y-значений
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX)
            throw new IllegalArgumentException("левая граница >= правая граница");
        if (values.length < 2)
            throw new IllegalArgumentException("Кол-во точек < 2");

        pointsCount = values.length;
        points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1); // шаг по X между точками
        for (int i = 0; i < pointsCount; i++)
            points[i] = new FunctionPoint(leftX + i * step, values[i]); // создаем точки с заданными Y
    }

    // новый конструктор: по массиву FunctionPoint
    public ArrayTabulatedFunction(FunctionPoint[] pointsArray) {
        if (pointsArray == null || pointsArray.length < 2)
            throw new IllegalArgumentException("Массив точек пустой или содержит меньше 2 точек");

        // проверка сортировки по X
        for (int i = 1; i < pointsArray.length; i++) {
            if (pointsArray[i].getX() <= pointsArray[i - 1].getX())
                throw new IllegalArgumentException("Массив точек не отсортирован по X");
        }

        this.pointsCount = pointsArray.length;
        this.points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(pointsArray[i]); // копирование для инкапсуляции
        }
    }

    public int getPointsCount() { return pointsCount; } // вернуть текущее количество точек
    public double getLeftDomainBorder() { return points[0].getX(); } // левая граница области определения
    public double getRightDomainBorder() { return points[pointsCount - 1].getX(); } // правая граница области определения

    // получение значения функции в точке x (линейная интерполяция)
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() - EPS || x > getRightDomainBorder() + EPS)
            return Double.NaN; // x вне области определения

        for (int i = 0; i < pointsCount - 1; i++) {
            if (Math.abs(x - points[i].getX()) < EPS)
                return points[i].getY(); // если совпадает с точкой, вернуть её Y

            if (x > points[i].getX() - EPS && x < points[i + 1].getX() + EPS) {
                // линейная интерполяция между соседними точками
                double x1 = points[i].getX();
                double x2 = points[i + 1].getX();
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }

        if (Math.abs(x - points[pointsCount - 1].getX()) < EPS)
            return points[pointsCount - 1].getY(); // если совпадает с последней точкой

        return Double.NaN; // если x не найден
    }

    // проверка корректности индекса
    private void checkIndex(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("Индекс" + index + "выходит за границы");
    }

    // оптимизированный доступ к точке с использованием кэша
    private FunctionPoint getCachedPoint(int index) {
        checkIndex(index);
        if (index == lastAccessedIndex && lastAccessedPoint != null)
            return lastAccessedPoint; // вернуть кэшированную точку
        lastAccessedIndex = index;
        lastAccessedPoint = points[index]; // обновить кэш
        return lastAccessedPoint;
    }

    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getCachedPoint(index)); // вернуть копию точки
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        double x = point.getX();
        if ((index > 0 && x <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && x >= points[index + 1].getX()))
            throw new InappropriateFunctionPointException("X вне порядка"); // проверка порядка X
        points[index] = new FunctionPoint(point); // заменить точку
        lastAccessedIndex = index; // обновить кэш
        lastAccessedPoint = points[index];
    }

    public double getPointX(int index) { return getCachedPoint(index).getX(); } // получить X точки
    public double getPointY(int index) { return getCachedPoint(index).getY(); } // получить Y точки

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        if ((index > 0 && x <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && x >= points[index + 1].getX()))
            throw new InappropriateFunctionPointException("Х вне порядка"); // проверка порядка X
        points[index].setX(x); // установить новое X
        lastAccessedIndex = index; // обновить кэш
        lastAccessedPoint = points[index];
    }

    public void setPointY(int index, double y) {
        checkIndex(index);
        points[index].setY(y); // установить новое Y
        lastAccessedIndex = index; // обновить кэш
        lastAccessedPoint = points[index];
    }

    // добавление новой точки
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        for (int i = 0; i < pointsCount; i++)
            if (Math.abs(points[i].getX() - point.getX()) < EPS)
                throw new InappropriateFunctionPointException("дубликат Х"); // проверка на дублирование X

        if (pointsCount == points.length) {
            // расширение массива при необходимости
            FunctionPoint[] newPoints = new FunctionPoint[pointsCount + 1];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }

        // найти позицию для вставки
        int index = 0;
        while (index < pointsCount && points[index].getX() < point.getX())
            index++;

        // сдвинуть точки вправо для вставки
        System.arraycopy(points, index, points, index + 1, pointsCount - index);
        points[index] = new FunctionPoint(point); // вставить точку
        pointsCount++;

        lastAccessedIndex = index; // обновить кэш
        lastAccessedPoint = points[index];
    }

    // удаление точки
    public void deletePoint(int index) {
        checkIndex(index);
        if (pointsCount <= 2)
            throw new IllegalStateException("удаление невозможно: кол-во точек < 3"); // минимальное количество точек

        // сдвинуть оставшиеся точки влево
        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;

        lastAccessedIndex = -1; // сброс кэша
        lastAccessedPoint = null;
    }

    // Реализация Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pointsCount = in.readInt();
        points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointsCount; i++) {
            sb.append(points[i].toString()); // используем toString() точки
            if (i < pointsCount - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof TabulatedFunction)) return false;

        TabulatedFunction that = (TabulatedFunction) o;

        // Проверка количества точек
        if (this.getPointsCount() != that.getPointsCount()) return false;

        // Оптимизация для ArrayTabulatedFunction
        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction other = (ArrayTabulatedFunction) o;
            for (int i = 0; i < pointsCount; i++) {
                // Используем getPoint() вместо прямого доступа к points[i]
                if (!this.getPoint(i).equals(other.getPoint(i))) {
                    return false;
                }
            }
        }
        // Оптимизация для LinkedListTabulatedFunction
        else if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction other = (LinkedListTabulatedFunction) o;
            for (int i = 0; i < pointsCount; i++) {
                // Используем getPoint() для обеих функций
                if (!this.getPoint(i).equals(other.getPoint(i))) {
                    return false;
                }
            }
        } else {
            // Общий случай для любого TabulatedFunction
            for (int i = 0; i < pointsCount; i++) {
                FunctionPoint thisPoint = this.getPoint(i);
                FunctionPoint thatPoint = that.getPoint(i);
                if (!thisPoint.equals(thatPoint)) {
                    return false;
                }
            }
        }

        return true;
    }

    // Переопределение метода hashCode()
    @Override
    public int hashCode() {
        int hash = pointsCount; // включаем количество точек в хэш

        for (int i = 0; i < pointsCount; i++) {
            hash ^= points[i].hashCode(); // XOR с хэш-кодом каждой точки
        }

        return hash;
    }

    // Переопределение метода clone()
    @Override
    public Object clone() {
        // Глубокое клонирование
        FunctionPoint[] clonedPoints = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            clonedPoints[i] = (FunctionPoint) points[i].clone(); // клонируем каждую точку
        }

        try {
            ArrayTabulatedFunction clone = (ArrayTabulatedFunction) super.clone();
            clone.points = clonedPoints;
            clone.pointsCount = this.pointsCount;
            // transient поля сбрасываем
            clone.lastAccessedIndex = -1;
            clone.lastAccessedPoint = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            // fallback - создаем через конструктор
            return new ArrayTabulatedFunction(clonedPoints);
        }
    }
}