package functions.meta;

import functions.Function;

public class Scale implements Function {
    private final Function base;
    private final double scaleX;
    private final double scaleY;

    public Scale(Function base, double scaleX, double scaleY) {
        this.base = base;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public double getLeftDomainBorder() {
        return base.getLeftDomainBorder() / scaleX;
    }

    public double getRightDomainBorder() {
        return base.getRightDomainBorder() / scaleX;
    }

    public double getFunctionValue(double x) {
        return base.getFunctionValue(x * scaleX) * scaleY;
    }

}
