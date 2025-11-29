package functions;

import functions.meta.*;

public class Functions {
    // Приватный конструктор, чтобы нельзя было создавать объект
    private Functions() {} //нельзя создать объект

    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    public static Function composition(Function outer, Function inner) {
        return new Composition(outer, inner);
    }
    public static double Integrate(Function function, double leftBorder, double rightBorder, double discretizationStep) {
        // Проверка корректности границ интегрирования
        if (leftBorder >= rightBorder) {
            throw new IllegalArgumentException("Левая граница интегрирования должна быть меньше правой");
        }

        // Проверка, что границы интегрирования входят в область определения функции
        if (leftBorder < function.getLeftDomainBorder() || rightBorder > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы интегрирования выходят за область определения функции");
        }

        // Проверка корректности шага дискретизации
        if (discretizationStep <= 0) {
            throw new IllegalArgumentException("Шаг дискретизации должен быть положительным");
        }

        double integral = 0.0;
        double currentX = leftBorder;

        // Проходим по всем полным шагам
        while (currentX + discretizationStep <= rightBorder) {
            double f1 = function.getFunctionValue(currentX);
            double f2 = function.getFunctionValue(currentX + discretizationStep);
            integral += (f1 + f2) * discretizationStep / 2.0;
            currentX += discretizationStep;
        }

        // Обрабатываем последний неполный шаг (если есть)
        if (currentX < rightBorder) {
            double lastStep = rightBorder - currentX;
            double f1 = function.getFunctionValue(currentX);
            double f2 = function.getFunctionValue(rightBorder);
            integral += (f1 + f2) * lastStep / 2.0;
        }

        return integral;
    }
}
