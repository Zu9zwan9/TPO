package com.lab1.task1_3;


import java.awt.*;

public class BallThread extends Thread {
    private final Ball b;

    public BallThread(Ball ball) {
        b = ball;
        // Модифікувати програму «Більярдна кулька» так, щоб кульки червоного кольору створювались
        //з вищим пріоритетом потоку, в якому вони виконують рух, ніж кульки синього кольору.
        if (b.color == Color.RED) {
            this.setPriority(MAX_PRIORITY);
        } else {
            this.setPriority(MIN_PRIORITY);
        }
    }

    @Override
    public void run() {
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


