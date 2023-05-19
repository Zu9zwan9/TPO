package com.lab2.task3;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Tutor extends Thread {

    private final Journal journal;
    private final ArrayList<Student> students;
    private final String tutorName;
    private final CountDownLatch latch;

    public Tutor(CountDownLatch latch, String name, Journal journal, ArrayList<Student> students) {
        this.journal = journal;
        this.students = students;
        this.tutorName = name;
        this.latch = latch;
    }

    @Override
    public void run() {
        Random random = new Random();

        for (int week = 0; week < 4; week++) {
            for (Student student : this.students) {
                this.journal.addMark(this.tutorName, student, week, random.nextInt(100));
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("%s has finished the work\n", this.tutorName);
        this.latch.countDown();
    }
}
