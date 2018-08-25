package com.kuznetsov.symbder.Operations;

public class Unary extends Operation {

    public static final int SIN = 0, COS = 1, NEG = 2, TAN = 3, SEC = 4, LN = 5;
    protected int Function;
    protected Node u;

    public Unary(int function, Node u) {
        super(-1);
        this.u = u;
        Function = function;
    }

    public Unary(int function, int bracketCount) {
        super(bracketCount);
        Function = function;
    }

    @Override
    public String toString() {
        return this.getAlias() + "(" + u.toString() + ")";
    }

    @Override
    public int getPrecedence() {
        if (Function == NEG) {
            return 3;
        } else {
            return 5;
        }
    }

    @Override
    public double getNumericValue() {
        switch (Function) {
            case NEG:
                return -u.getNumericValue();
            case SIN:
                return Math.sin(u.getNumericValue());
            case COS:
                return Math.cos(u.getNumericValue());
            case TAN:
                return Math.tan(u.getNumericValue());
            case SEC:
                return 1.d / Math.cos(u.getNumericValue());
            case LN:
                return Math.log(u.getNumericValue());
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    String getAlias() {
        switch (this.Function) {
            case NEG:
                return "-";
            case SIN:
                return "sin";
            case COS:
                return "cos";
            case TAN:
                return "tan";
            case SEC:
                return "sec";
            case LN:
                return "ln";
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Node getDerivative() {
        switch (Function) {
            case NEG:
                // -u'
                return new Unary(NEG, u.getDerivative());
            case SIN:
                //cos(u)*u'
                return new Binary('*', new Unary(COS, u), u.getDerivative());
            case COS:
                //-sin(u)*u'
                return new Binary('*', new Unary(NEG, new Unary(SIN, u)), u.getDerivative());
            case TAN:
                //sec(u)^2*u'	
                return new Binary('*', new Binary('^', new Unary(SEC, u), Number.NumberConstant(2)), u.getDerivative());
            case SEC:
                //sec(u)*tan(u)*u'
                return new Binary('*', new Binary('*', new Unary(SEC, u), new Unary(TAN, u)), u.getDerivative());
            case LN:
                // 1/u
                return new Binary('/', Number.NumberConstant(1), u);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Node Optimize() {
        u = u.Optimize();
        if (u.isAConstant() && Function == NEG) {
            return Number.NumberConstant(-u.getNumericValue());
        } else if (Function == LN && u instanceof Number && ((Number) u).type == Number.E) {
            return Number.NumberConstant(1);
        }
        return this;
    }

}
