package com.kuznetsov.symbder.Operations;

import java.util.ArrayList;

public class Binary extends Operation {

    protected Node u, v;
    private char type; //'l' = log

    public Binary(char t, int bracketCount) {
        super(bracketCount);
        type = t;
    }

    public Binary(char t, Node u, Node v) {
        super(-1);
        this.u = u;
        this.v = v;
        type = t;
    }

    @Override
    public String toString() {
        return "(" + u.toString() + ")" + this.getAlias() + "(" + v.toString() + ")";
    }

    @Override
    public double getNumericValue() {
        switch (type) {
            case '+':
                return u.getNumericValue() + v.getNumericValue();
            case '*':
                return u.getNumericValue() * v.getNumericValue();
            case '/':
                return u.getNumericValue() / v.getNumericValue();
            case '^':
                return Math.pow(u.getNumericValue(), v.getNumericValue());

        }
        return 0;
    }

    @Override
    String getAlias() {
        return type + "";
    }

    @Override
    public int getPrecedence() {
        switch (type) {
            case '+':
                return 1;
            case '*':
                return 2;
            case '/':
                return 2;
            case '^':
                return 4;
        }
        return 0;
    }

    private double identity() {
        return (type == '+') ? 0 : 1;
    }

    @Override
    public Node getDerivative() {

        switch (type) {
            case '+':
                // u' + v'
                return new Binary('+', u.getDerivative(), v.getDerivative());
            case '*':
                //return u * v' + v * u'
                return new Binary('+', new Binary('*', u, v.getDerivative()), new Binary('*', v, u.getDerivative()));
            case '/':
                // (u * v' - v * u') / (v ^ 2)
                return new Binary('/', new Binary('+', new Binary('*', v, u.getDerivative()), new Unary(Unary.NEG, new Binary('*', u, v.getDerivative()))), new Binary('^', v, Number.NumberConstant(2)));
            case '^':
                //v * u^(v-1) * u' + u^v * ln(u)*v'
                return new Binary('+', new Binary('*', new Binary('*', v, new Binary('^', u, new Binary('+', v, Number.NumberConstant(-1)))), u.getDerivative()), new Binary('*', new Binary('^', u, v), new Binary('*', new Unary(Unary.LN, u), v.getDerivative())));
                // u^v * (u' * (v / u) + ln(u)*v')
                //return new Binary('*', new Binary('^', u, v), new Binary('+', new Binary('*', v.getDerivative(), new Unary(Unary.LN, u)), new Binary('*', u.getDerivative(), new Binary('/', v, u))));

        }
        return null;
    }

    private static boolean isANumberEqualtTo(Node n, double d) {
        return n.isAConstant() && n.getNumericValue() == d;
    }

    private void forceOrder() {
        if (v.isAConstant()) {
            Node n = v;
            v = u;
            u = n;
        }
    }

    @Override
    public Node Optimize() {
        u = u.Optimize();
        v = v.Optimize();
        switch (type) {
            case '+':
                if (isANumberEqualtTo(v, 0)) {
                    return u; //if, v in u+v is 0, return u
                }
                if (isANumberEqualtTo(u, 0)) {
                    return v; //if, u in u*v is 0, return v
                }
                break;
            case '*':
                if (isANumberEqualtTo(u, 0) || isANumberEqualtTo(v, 1)) {
                    return u; //return u if it is zero or if it is being multiplied by 1
                }
                if (isANumberEqualtTo(v, 0) || isANumberEqualtTo(u, 1)) {
                    return v; //return v if it is zero or if it is being multiplied by 1
                }
                break;
            case '/':
                if (isANumberEqualtTo(v, 1) || isANumberEqualtTo(u, 0)) {
                    return u; //return u if it is zero or if it is being devided by 1
                }
                break;
            case '^':
                if (isANumberEqualtTo(u, 1) || isANumberEqualtTo(v, 0)) {
                    return Number.NumberConstant(1); //if it is 1^v or u^0, return 1
                }
                if (isANumberEqualtTo(u, 0)) {
                    return Number.NumberConstant(0); //if it is 0^v return 0
                }
                if (isANumberEqualtTo(v, 1)) {
                    return u; //if it is u^1 return u
                }
                if ((u instanceof Binary) && ((Binary) u).type == '^') {
                    Node uv = ((Binary) u).v;
                    ((Binary) u).v = new Binary('*', uv, v);
                    return u.Optimize();
                } //(A^B)^C == A^(B*C)

                break;
        }

        if (u.isAConstant() && v.isAConstant()) {
            return Number.NumberConstant(this.getNumericValue()); //if both are numbers, just find the result
        }

        if (type != '^' && type != '/' && true) { //restructure tree to be more simplifable
            ArrayList<Node> elements = new ArrayList<>(), finalElements = new ArrayList<>();
            Number cumTotal = Number.NumberConstant(this.identity());
            elements.add(u);
            elements.add(v);
            boolean reduced = false;
            //  System.out.println("Type: " + type);
            foundOne:
            while (!reduced) {
                for (Node n : elements) {
                    if ((n instanceof Binary) && ((Binary) n).type == this.type) {
                        elements.add(((Binary) n).u);
                        elements.add(((Binary) n).v);
                        elements.remove(n);
                        continue foundOne;
                    }
                }
                reduced = true;
            }

            boolean haveConstant = false;
            for (Node n : elements) {
                // System.out.println("Iter:");
                if (n.isAConstant()) {
                    //      System.out.println("Current constant: " + cumTotal.toString());
                    cumTotal = Number.NumberConstant(new Binary(this.type, cumTotal, n).getNumericValue());
                    //     System.out.println("new: " + cumTotal.toString());
                    haveConstant = true;
                } else {
                    finalElements.add(n);
                }
                // System.out.println("Constant= " + cumTotal.getNumericValue());
            }

            //  System.out.println("Contant: " + haveConstant);
            //   System.out.println("Nodes:");
            for (Node n : finalElements) {
                //      System.out.println("Node: " + n.toString());
            }

            if (!haveConstant) {
                //System.out.println("Returned original becouse no constants");
                return this;
            } else if (finalElements.isEmpty()) {
                // System.out.println("Returned constant becouse no non-constants");
                return cumTotal;
            }

            finalElements.add(cumTotal);

            Binary cumBinary = new Binary(this.type, null, null);

            int lowest = 1;
            for (Node n : finalElements) {
                if (lowest == 1) {
                    cumBinary.u = n;
                    lowest++;
                } else if (lowest == 2) {
                    cumBinary.v = n;
                    lowest++;
                } else {
                    cumBinary = new Binary(this.type, cumBinary, n);
                }
            }
            cumBinary.forceOrder();
            //System.out.println("Returning: " + cumBinary.toString());
            return cumBinary;
        }

        return this;
    }

}
