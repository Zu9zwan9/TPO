package com.lab3.task2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 2. Реалізувати один з алгоритмів комп’ютерного практикуму 2 або 3 з використанням ForkJoinFramework
 * та визначити прискорення, яке отримане за рахунок використання ForkJoinFramework.
 */


public class Main {

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Student> studentsList = makeStudentsList();
        Journal groupsJournal = new Journal();
        CountDownLatch latch = new CountDownLatch(4);

        for (String name : new String[]{"Main tutor", "Assistant 1", "Assistant 2", "Assistant 3"}) {
            new Tutor(latch, name, groupsJournal, studentsList).start();
        }

        latch.await();
        groupsJournal.listJournal();
    }

    private static ArrayList<Student> makeStudentsList() {
        String[] groups = {"Group_1", "Group_2", "Group_3"};
        String[] studentNames = {"Anabelle Jimenez", "Gerardo Owen", "Darren Carpenter", "Payten Raymond",
                "Charlee Rush", "Kash Jimenez", "Ali Heath", "Rihanna Carson",
                "Emanuel Andersen", "Daisy Bolton", "Alia Valencia", "Manuel Washington"};
        List<String> studentsList = Arrays.asList(studentNames);
        Collections.shuffle(studentsList);
        ArrayList<Student> students = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 4; i++) {
                students.add(new Student(studentsList.get(3 * i + j), groups[j]));
            }
        }

        return students;
    }
}
