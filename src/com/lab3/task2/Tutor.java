package com.lab3.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

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
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        for (int week = 0; week < 4; week++) {
            commonPool.invoke(new CustomRecursiveAction(tutorName, students, journal, week));
        }

        System.out.printf("%s has finished the work\n", this.tutorName);
        this.latch.countDown();
    }
}

class CustomRecursiveAction extends RecursiveAction {

    private static final int THRESHOLD = 2;
    private final ArrayList<Student> workload;
    private final Journal journalLink;
    private final String tutorName;
    private final int week;
    private final Random random;

    public CustomRecursiveAction(String tutorName,
                                 ArrayList<Student> studentsToEvaluate,
                                 Journal journalToPutMarksIn,
                                 int week) {
        this.workload = studentsToEvaluate;
        this.journalLink = journalToPutMarksIn;
        this.tutorName = tutorName;
        this.week = week;
        this.random = new Random();
    }

    @Override
    protected void compute() {
        if (workload.size() > THRESHOLD) {
            ForkJoinTask.invokeAll(createSubtasks());
        } else {
            processing(this.tutorName, workload, this.journalLink, this.week);
        }
    }

    private List<CustomRecursiveAction> createSubtasks() {
        List<CustomRecursiveAction> subtasks = new ArrayList<>();

        ArrayList<Student> partOne = new ArrayList<>(workload.subList(0, workload.size() / 2));
        ArrayList<Student> partTwo = new ArrayList<>(workload.subList(workload.size() / 2, workload.size()));

        subtasks.add(new CustomRecursiveAction(this.tutorName, partOne, this.journalLink, this.week));
        subtasks.add(new CustomRecursiveAction(this.tutorName, partTwo, this.journalLink, this.week));

        return subtasks;
    }

    private void processing(String tutorName, ArrayList<Student> work, Journal journalLink, int week) {
        for (Student student : work
        ) {
            journalLink.addMark(tutorName, student, week, this.random.nextInt(100));
        }
    }
}
