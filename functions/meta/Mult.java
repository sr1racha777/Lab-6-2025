package functions.meta;

import functions.Function;

public class Mult implements Function {
    private final Function f1;
    private final Function f2;

    public Mult(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    public double getLeftDomainBorder() {
        return Math.max(f1.getLeftDomainBorder(), f2.getLeftDomainBorder());
    }

    public double getRightDomainBorder() {
        return Math.min(f1.getRightDomainBorder(), f2.getRightDomainBorder());
    }

    public double getFunctionValue(double x) {
        return f1.getFunctionValue(x) * f2.getFunctionValue(x);
    }
}
