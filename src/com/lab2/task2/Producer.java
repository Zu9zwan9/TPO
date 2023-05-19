package com.lab2.task2;

import java.util.Random;

public class Producer implements Runnable {
    private final Drop drop;
    private final int[] messageNumbers;

    public Producer(Drop drop, int size) {
        this.drop = drop;
        this.messageNumbers = new Random().ints(size, 1, 100).toArray();
    }

    public void run() {

        Random random = new Random();

        for (int i : messageNumbers) {
            drop.put(Integer.toString(i));
            try {
                Thread.sleep(random.nextInt(50));
            } catch (InterruptedException e) {
            }
        }
        drop.put("DONE");
    }
}
