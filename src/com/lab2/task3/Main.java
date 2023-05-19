package com.lab2.task3;

/**
 * Реалізувати роботу електронного журналу групи, в якому зберігаються оцінки з однієї дисципліни трьох груп студентів.
 * Кожного тижня лектор і його 3 асистенти виставляють оцінки з дисципліни за 100-бальною шкалою.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
        List<String> studentsList = new ArrayList<>(List.of(studentNames));
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
