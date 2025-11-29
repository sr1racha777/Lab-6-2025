package functions.meta;

import functions.Function;

public class Shift implements Function {
    private final Function base;
    private final double shiftX;
    private final double shiftY;

    public Shift(Function base, double shiftX, double shiftY) {
        this.base = base;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    public double getLeftDomainBorder() {
        return base.getLeftDomainBorder() - shiftX;
    }

    public double getRightDomainBorder() {
        return base.getRightDomainBorder() - shiftX;
    }

    public double getFunctionValue(double x) {
        return base.getFunctionValue(x + shiftX) + shiftY;}

}
