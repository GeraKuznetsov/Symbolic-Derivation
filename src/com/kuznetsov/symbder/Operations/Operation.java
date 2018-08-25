package com.kuznetsov.symbder.Operations;

public abstract class Operation extends Node {

    private int bracketCount = 0;

    public int getPrecedenceAndBracketCount() {
        return this.bracketCount * 10 + this.getPrecedence();
    }

  public  Operation(int bracketCount) {
        this.bracketCount = bracketCount;
    }
}
