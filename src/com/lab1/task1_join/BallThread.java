package com.lab1.task1_join;


import java.awt.*;

public class BallThread extends Thread {
    private final Ball b;
    private final Thread previousThread;

    public BallThread(Ball ball, Thread previousThread) {
        b = ball;
        // Модифікувати програму «Більярдна кулька» так, щоб кульки червоного кольору створювались
        //з вищим пріоритетом потоку, в якому вони виконують рух, ніж кульки синього кольору.
        if (b.color == Color.RED) {
            this.setPriority(MIN_PRIORITY);
        } else {
            this.setPriority(MAX_PRIORITY);
        }
        this.previousThread = previousThread;
    }

    @Override
    public void run() {
        try {
            if (previousThread != null) {
                previousThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            for (int i = 1; i < 10000; i++) {
                if (b.move()) {
                    Thread.currentThread().interrupt();
                } else {
                    Thread.sleep(5);
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}


