package com.example.student_managemnt;

public class Student {
    private final String name;
    private final String id;
    private final int age;
    private final String course;
    private final int module1;
    private final int module2;
    private final int module3;

    public Student(String name, String id, int age, String course, int module1, int module2, int module3) {
        this.name = name;
        this.id = id;
        this.age = age;
        this.course = course;
        this.module1 = module1;
        this.module2 = module2;
        this.module3 = module3;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getCourse() {
        return course;
    }

    public int getModule1() {
        return module1;
    }

    public int getModule2() {
        return module2;
    }

    public int getModule3() {
        return module3;
    }
}
