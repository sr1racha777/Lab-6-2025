package functions.basic;

import functions.Function;

public class Log implements Function {
    private final double base;

    public Log(double base) {
        if (base <= 0 || base == 1) {
            throw new IllegalArgumentException("Основание логарифма должно быть > 0 и != 1");
        }
        this.base = base;
    }

    public double getLeftDomainBorder() {
        return 0;
    }

    public double getRightDomainBorder() {
        return Double.MAX_VALUE;
    }

    public double getFunctionValue(double x) {
        if (x <= 0) {
            throw new IllegalArgumentException("Аргумент логарифма должен быть > 0");
        }
        return Math.log(x) / Math.log(base);
    }
}
