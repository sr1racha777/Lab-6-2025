package functions;

import java.io.*;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 1L;
    // Вложенный класс узла
    private static class FunctionNode implements Serializable {
        FunctionPoint point;     // точка функции
        FunctionNode next;       // ссылка на следующий узел
        FunctionNode prev;       // ссылка на предыдущий узел

        FunctionNode(FunctionPoint p) { point = p; }
    }

    // Поля класса
    private final FunctionNode head = new FunctionNode(null); // фиктивный узел
    private int pointsCount;                                    // количество точек
    private static final double EPS = Math.ulp(1.0);           // машинный эпсилон

    // кэш последнего доступа
    private transient FunctionNode lastAccessedNode = null;
    private transient int lastAccessedIndex = -1;

    // Конструкторы

    // 1) по диапазону и количеству точек (равномерное распределение X)
    public LinkedListTabulatedFunction(double leftX, double rightX, int count) {
        if (leftX >= rightX)
            throw new IllegalArgumentException("левый >= правый");
        if (count < 2)
            throw new IllegalArgumentException("кол-во точек < 2");

        head.next = head.prev = head;
        pointsCount = 0;

        double step = (rightX - leftX) / (count - 1);
        for (int i = 0; i < count; i++) {
            addNodeToTail().point = new FunctionPoint(leftX + i * step, 0.0);
        }
    }

    // 2) по массиву значений Y
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX)
            throw new IllegalArgumentException("левый >= правый");
        if (values.length < 2)
            throw new IllegalArgumentException("кол-во точек < 2");

        head.next = head.prev = head;
        pointsCount = 0;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            addNodeToTail().point = new FunctionPoint(leftX + i * step, values[i]);
        }
    }

    // 3) по массиву FunctionPoint
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points == null || points.length < 2)
            throw new IllegalArgumentException("массив точек пуст или содержит меньше двух элементов");

        // Проверка сортировки по X
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i - 1].getX())
                throw new IllegalArgumentException("точки не упортированы по X");
        }

        head.next = head.prev = head;
        pointsCount = 0;

        for (int i = 0; i < points.length; i++) {
            addNodeToTail().point = new FunctionPoint(points[i]);
        }
    }

    // Основные методы работы с узлами

    // добавление узла в конец списка
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(new FunctionPoint(0, 0));
        if (head.next == head) { // список пуст
            head.next = head.prev = newNode;
            newNode.next = newNode.prev = head;
        } else {
            newNode.prev = head.prev;
            newNode.next = head;
            head.prev.next = newNode;
            head.prev = newNode;
        }

        pointsCount++;
        lastAccessedNode = newNode;
        lastAccessedIndex = pointsCount - 1;

        return newNode;
    }

    // получение узла по индексу с кэшем
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " вне диапазона");

        if (lastAccessedNode != null && Math.abs(index - lastAccessedIndex) <= pointsCount / 2) {
            FunctionNode node = lastAccessedNode;
            if (index > lastAccessedIndex) {
                for (int i = lastAccessedIndex; i < index; i++) node = node.next;
            } else if (index < lastAccessedIndex) {
                for (int i = lastAccessedIndex; i > index; i--) node = node.prev;
            }
            lastAccessedNode = node;
            lastAccessedIndex = index;
            return node;
        }

        FunctionNode node = head.next;
        for (int i = 0; i < index; i++) node = node.next;
        lastAccessedNode = node;
        lastAccessedIndex = index;
        return node;
    }

    // удаление узла
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " вне диапазона");
        if (pointsCount <= 2)
            throw new IllegalStateException("нельзя удалить: останется меньше 2 точек");

        FunctionNode node = getNodeByIndex(index);

        node.prev.next = node.next;
        node.next.prev = node.prev;

        pointsCount--;

        if (lastAccessedIndex == index) {
            lastAccessedNode = null;
            lastAccessedIndex = -1;
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }

        return node;
    }

    // Методы TabulatedFunction
    @Override
    public int getPointsCount() { return pointsCount; }

    @Override
    public double getLeftDomainBorder() { return head.next.point.getX(); }

    @Override
    public double getRightDomainBorder() { return head.prev.point.getX(); }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() - EPS || x > getRightDomainBorder() + EPS) return Double.NaN;

        FunctionNode node = head.next;
        while (node.next != head) {
            double x1 = node.point.getX();
            double x2 = node.next.point.getX();
            if (Math.abs(x - x1) < EPS) return node.point.getY();
            if (x > x1 - EPS && x < x2 + EPS)
                return node.point.getY() + (node.next.point.getY() - node.point.getY()) * (x - x1) / (x2 - x1);
            node = node.next;
        }
        if (Math.abs(x - head.prev.point.getX()) < EPS) return head.prev.point.getY();
        return Double.NaN;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double x = point.getX();
        if ((node.prev != head && x <= node.prev.point.getX()) ||
                (node.next != head && x >= node.next.point.getX()))
            throw new InappropriateFunctionPointException("X вне порядка");
        node.point = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) { return getNodeByIndex(index).point.getX(); }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        if ((node.prev != head && x <= node.prev.point.getX()) ||
                (node.next != head && x >= node.next.point.getX()))
            throw new InappropriateFunctionPointException("X вне порядка");
        node.point.setX(x);
    }

    @Override
    public double getPointY(int index) { return getNodeByIndex(index).point.getY(); }

    @Override
    public void setPointY(int index, double y) { getNodeByIndex(index).point.setY(y); }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = head.next;
        while (node != head) {
            if (Math.abs(node.point.getX() - point.getX()) < EPS)
                throw new InappropriateFunctionPointException("дубликат X");
            node = node.next;
        }

        FunctionNode newNode = new FunctionNode(new FunctionPoint(point));

        node = head.next;
        while (node != head && node.point.getX() < point.getX())
            node = node.next;

        newNode.prev = node.prev;
        newNode.next = node;
        node.prev.next = newNode;
        node.prev = newNode;

        pointsCount++;
    }

    @Override
    public void deletePoint(int index) { deleteNodeByIndex(index); }

    // Переопределение метода toString()
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        FunctionNode current = head.next;
        while (current != head) {
            sb.append(current.point.toString());
            if (current.next != head) {
                sb.append(", ");
            }
            current = current.next;
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
                // Используем getPoint() для обеих функций
                if (!this.getPoint(i).equals(other.getPoint(i))) {
                    return false;
                }
            }
        }
        // Оптимизация для LinkedListTabulatedFunction
        else if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction other = (LinkedListTabulatedFunction) o;
            // Используем прямое сравнение узлов через приватный метод getNodeByIndex()
            for (int i = 0; i < pointsCount; i++) {
                if (!this.getNodeByIndex(i).point.equals(other.getNodeByIndex(i).point)) {
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

        FunctionNode current = head.next;
        while (current != head) {
            hash ^= current.point.hashCode(); // XOR с хэш-кодом каждой точки
            current = current.next;
        }

        return hash;
    }

    // Переопределение метода clone()
    @Override
    public Object clone() {
        // "Пересборка" нового списка без использования методов добавления
        FunctionPoint[] pointsArray = new FunctionPoint[pointsCount];

        // Собираем массив точек из текущего списка
        FunctionNode current = head.next;
        int index = 0;
        while (current != head) {
            pointsArray[index++] = (FunctionPoint) current.point.clone(); // клонируем точку
            current = current.next;
        }

        // Создаем новый список через конструктор
        return new LinkedListTabulatedFunction(pointsArray);
    }
}