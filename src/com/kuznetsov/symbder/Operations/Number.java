package com.kuznetsov.symbder.Operations;

public class Number extends Node {

    protected int type;
    protected double value;
    public static final int CONSTANT = 0, PI = 1, E = 2, X = 3;
    public static double globalX;

    public static Number NumberX() {
        return new Number(X, 0);
    }

    public static Number NumberConstant(double constant) {
        return new Number(CONSTANT, constant);

    }

    public static Number NumberPI() {
        return new Number(PI, 0);

    }

    public static Number NumberE() {
        return new Number(E, 0);
    }

    public Number(int t, double f) {
        used = true;
        type = t;
        value = f;
    }

    @Override
    public boolean isAConstant() {
        return type == CONSTANT;
    }

    @Override
    String getAlias() {
        switch (type) {
            case CONSTANT:
                return Double.toString(value);
            case PI:
                return "pi";
            case E:
                return "e";
            case X:
                return "x";
        }
        return null;
    }

    @Override
    public double getNumericValue() {
        switch (type) {
            case CONSTANT:
                return value;
            case PI:
                return Math.PI;
            case E:
                // System.out.println("Math.E = " + Math.E);
                return Math.E;
            case X:
                return globalX;
        }
        return 0;
    }

    @Override
    public int getPrecedence() {
        throw new UnsupportedOperationException("Not supported yet.");//numbers have no precidense 
    }

    @Override
    public String toString() {
        return getAlias();
    }

    @Override
    public Node getDerivative() {
        if (type == X) {
            return NumberConstant(1);
        } else {
            return NumberConstant(0);
        }
    }

    @Override
    public Node Optimize() {
        return this;
    }

}
