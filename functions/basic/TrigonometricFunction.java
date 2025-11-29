package functions.basic;

import functions.Function;

public abstract class TrigonometricFunction implements Function {

    public double getLeftDomainBorder() {
        return -Double.MAX_VALUE;
    }

    public double getRightDomainBorder() {
        return Double.MAX_VALUE;
    }
}
