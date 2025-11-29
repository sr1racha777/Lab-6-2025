package functions.threads;

import functions.Function;

public class Task {
    private Function function;
    private double leftBorder;
    private double rightBorder;
    private double discretizationStep;
    private int tasksCount;
    private boolean dataProcessed = true; // true - данные обработаны, можно генерировать новые

    public Task() {
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public double getLeftBorder() {
        return leftBorder;
    }

    public void setLeftBorder(double leftBorder) {
        this.leftBorder = leftBorder;
    }

    public double getRightBorder() {
        return rightBorder;
    }

    public void setRightBorder(double rightBorder) {
        this.rightBorder = rightBorder;
    }

    public double getDiscretizationStep() {
        return discretizationStep;
    }

    public void setDiscretizationStep(double discretizationStep) {
        this.discretizationStep = discretizationStep;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(int tasksCount) {
        this.tasksCount = tasksCount;
    }

    private volatile boolean newData = false;

    public synchronized void markDataAsNew() {
        newData = true;
    }

    public synchronized void markDataAsProcessed() {
        newData = false;
    }

    public synchronized boolean isNewData() {
        return newData;
    }
    public synchronized boolean isDataProcessed() {
        return dataProcessed;
    }

    public synchronized void setDataProcessed(boolean processed) {
        this.dataProcessed = processed;
    }
}