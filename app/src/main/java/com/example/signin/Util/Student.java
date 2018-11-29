package com.example.signin.Util;

public class Student {
    private String id;
    private String name;

    public Student(){
        super();
    }

    public Student(String id) {
        super();
        this.id = id;
    }

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
