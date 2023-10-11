package com.example.pruebaenviogmap;

public class Direction {
    private String name;
    private boolean selected;

    public Direction(String name) {
        this.name = name;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}


