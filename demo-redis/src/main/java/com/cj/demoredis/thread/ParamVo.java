package com.cj.demoredis.thread;

public class ParamVo {
    private int i;

    public ParamVo(int i) {
        this.i = i;
    }

    public int getI() {

        return i;
    }

    @Override
    public String toString() {

        return String.valueOf(i) + " " + hashCode();
    }
}
