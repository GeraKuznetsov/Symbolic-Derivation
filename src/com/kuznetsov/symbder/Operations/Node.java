package com.kuznetsov.symbder.Operations;

import java.util.ArrayList;

public abstract class Node {

    protected boolean used = false;

    abstract String getAlias();

    @Override
    abstract public String toString();

    abstract public Node Optimize();

    public boolean isAConstant() {
        return false;
    }

    public abstract int getPrecedence();
    //1:+ 
    //2: */
    //3: -
    //4: ^ 
    //5: function

    public abstract double getNumericValue();

    public abstract Node getDerivative();

    private static ArrayList<Node> parsedInput;
    private static String parse;
    private static int bracketCount;

    public static Node parseFunction(String function) throws Exception {
                parsedInput = new ArrayList<>();
        System.out.println("Parsing: " + function);
        parse = "";
        bracketCount = 0;
        int lastParsedType = 0, newParseType;

        for (int i = 0; i < function.length(); i++) {
            char c = function.charAt(i);
            if (c != ' ') {
                if ((c >= '0' && c <= '9') || c == '.') {
                    if (lastParsedType == 2) {
                        parse(2);
                    }
                    newParseType = 1;
                    parse += c;
                } else if (c >= 'a' && c <= 'z') {
                    if (lastParsedType == 1) {
                        parse(1);
                    }
                    newParseType = 2;
                    parse += c;
                } else {
                    if (lastParsedType == 1 || lastParsedType == 2) {
                        parse(lastParsedType);
                    }
                    newParseType = 0;
                    switch (c) {
                        case '-':
                            if (lastParsedType != 0) {
                                parsedInput.add(new Binary('+', bracketCount));
                            }
                            parsedInput.add(new Unary(Unary.NEG, bracketCount));
                            break;
                        case '(':
                            if (!parsedInput.isEmpty() && parsedInput.get(parsedInput.size() - 1) instanceof Number) {
                                parsedInput.add(new Binary('*', bracketCount));
                            }
                            newParseType = 0;
                            bracketCount++;
                            break;
                        case ')':
                            if (lastParsedType == 0) {
                                throw new Exception("invalid bracket placement");
                            }
                            newParseType = -1;
                            bracketCount--;
                            if (bracketCount < 0) {
                                throw new Exception("bracket mismatch");
                            }
                            break;
                        default:
                            if (lastParsedType == 0) {
                                System.out.println(function.substring(i));
                                throw new Exception("invalid operator placement");
                            }
                            switch (c) {
                                case '+':
                                    parsedInput.add(new Binary('+', bracketCount));
                                    break;
                                case '*':
                                    parsedInput.add(new Binary('*', bracketCount));
                                    break;
                                case '/':
                                    parsedInput.add(new Binary('/', bracketCount));
                                    break;
                                case '^':
                                    parsedInput.add(new Binary('^', bracketCount));
                                    break;
                                default:
                                    throw new Exception("unkown symbol: " + c);
                            }
                    }
                }
                lastParsedType = newParseType;
            }
        }

        if (lastParsedType != 0) {
            parse(lastParsedType);
        }
        if (bracketCount > 0) {
            throw new Exception("unclosed brackets");
        }

        
        while (parsedInput.size() > 1) {
            boolean useFrirst = true;
            int highestPriorityIndex = 0;
            Operation highestPriority = null, currentOperation;
            for (int i = 0; i < parsedInput.size(); i++) {
                if (parsedInput.get(i) instanceof Operation) {
                    currentOperation = (Operation) parsedInput.get(i);
                    if ((!currentOperation.used) && (useFrirst || (currentOperation.getPrecedenceAndBracketCount() > highestPriority.getPrecedenceAndBracketCount()))) {
                        highestPriorityIndex = i;
                        useFrirst = false;
                        highestPriority = currentOperation;
                    }
                }
            }
            if(highestPriority == null){
                    throw new Exception("something went wrong");
            }
            highestPriority.used = true;
            if (highestPriority instanceof Unary) {
                Node right = parsedInput.remove(highestPriorityIndex + 1);
                ((Unary) highestPriority).u = right;
            } else if (highestPriority instanceof Binary) {
                Node left = parsedInput.remove(highestPriorityIndex - 1);
                Node right = parsedInput.remove(highestPriorityIndex);
                ((Binary) highestPriority).u = left;
                ((Binary) highestPriority).v = right;
            }
        }
        System.out.println("unoptimized tree:" + parsedInput.get(0));
        return parsedInput.get(0).Optimize();
//</editor-fold>
    }

    static boolean testParse(String s) {
        if (parse.startsWith(s)) {
            parse = parse.substring(s.length());
            return true;
        }
        return false;
    }

    static private void parse(int type) throws Exception {
        if (type == 1) {
            try {
                parsedInput.add(Number.NumberConstant(Double.parseDouble(parse)));
                parse = "";
            } catch (NumberFormatException e) {
                throw new Exception("cant parse number" + e.getMessage());
            }
        } else {
            while (!parse.isEmpty()) {
                if (testParse("x")) {
                    parsedInput.add(Number.NumberX());
                } else if (testParse("e")) {
                    parsedInput.add(Number.NumberE());
                } else if (testParse("pi")) {
                    parsedInput.add(Number.NumberPI());
                } else if (testParse("sin")) {
                    parsedInput.add(new Unary(Unary.SIN, bracketCount));
                } else if (testParse("cos")) {
                    parsedInput.add(new Unary(Unary.COS, bracketCount));
                } else if (testParse("tan")) {
                    parsedInput.add(new Unary(Unary.TAN, bracketCount));
                } else if (testParse("sec")) {
                    parsedInput.add(new Unary(Unary.SEC, bracketCount));
                } else if (testParse("ln")) {
                    parsedInput.add(new Unary(Unary.LN, bracketCount));
                } else {
                    throw new Exception("unkown symbol: " + parse);
                }
            }
        }
    }

}
