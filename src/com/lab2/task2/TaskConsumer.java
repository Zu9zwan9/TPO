package com.lab2.task2;

import java.util.Random;

/**
 * Зміна назви через використання інтерфейса Consumer в коді
 */

public class TaskConsumer implements Runnable {
    private final Drop drop;

    public TaskConsumer(Drop drop) {
        this.drop = drop;
    }

    public void run() {
        Random random = new Random();
        for (String message = drop.take();
             !message.equals("DONE");
             message = drop.take()) {
            System.out.format("MESSAGE RECEIVED: %s%n", message);
            try {
                Thread.sleep(random.nextInt(5));
            } catch (InterruptedException e) {
            }
        }
    }
}
