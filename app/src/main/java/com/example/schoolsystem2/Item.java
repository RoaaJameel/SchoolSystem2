package com.example.schoolsystem2;

public class Item {
    int id;
    String name;

    Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
