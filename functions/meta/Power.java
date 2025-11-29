package functions.meta;

import functions.Function;

public class Power implements Function {
    private final Function base;
    private final double exponent;


    public Power(Function base, double exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    public double getLeftDomainBorder() {
        return base.getLeftDomainBorder();
    }

    public double getRightDomainBorder() {
        return base.getRightDomainBorder();
    }

    public double getFunctionValue(double x) {
        return Math.pow(base.getFunctionValue(x), exponent);
    }
}
