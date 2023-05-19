package com.lab1.task3;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Клас, екземпляри якого міняють значення лічильника
 */

public class Modifier extends Thread {

    private final Counter counter;
    private final int iter;
    private final Consumer<Counter> method;
    private final CountDownLatch latch;

    public Modifier(CountDownLatch latch, char sign, Counter counter, int iterationsNumber) {
        this.latch = latch;
        this.counter = counter;
        this.iter = iterationsNumber;
        // визначення консьюмера дозволяє уникнути використання switch - case на кожній ітерації
        this.method = (sign == '+') ? Modifier::increase : Modifier::decrease;
    }

    public static void increase(Counter localCounter) {
        localCounter.increase();
    }

    public static void decrease(Counter localCounter) {
        localCounter.decrease();
    }

    @Override
    public void run() {
        for (int i = 0; i < this.iter; i++) {
            this.method.accept(this.counter);
        }
        this.latch.countDown();
    }
}
