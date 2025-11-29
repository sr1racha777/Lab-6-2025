package functions.basic;

import functions.Function;

public class Exp implements Function {

    public double getLeftDomainBorder() {
        return -Double.MAX_VALUE;
    }

    public double getRightDomainBorder() {
        return Double.MAX_VALUE;
    }

    public double getFunctionValue(double x) {
        return Math.exp(x);
    }
}
