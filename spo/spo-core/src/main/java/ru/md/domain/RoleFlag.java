package ru.md.domain;

/**
 * Created by Admin on 09.02.2017.
 */
public class RoleFlag {
    private String name;
    private boolean flag;
    public String getName() {
        return name;
    }
    public boolean isFlag() {
        return flag;
    }
    public RoleFlag(String name, boolean flag) {
        this.name = name;
        this.flag = flag;
    }
}
