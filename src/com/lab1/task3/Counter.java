package com.lab1.task3;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Клас, який є успадкованим від AtomicInteger.
 * Дозволяє збільшувати та зменшувати значення лічильника, не повертаючи значення.
 */

public class Counter extends AtomicInteger {

    public Counter() {
        this.set(0);
    }


    /**
     * Збільшує значення лічильника на одиницю
     */
    public void increase() {
        this.incrementAndGet();
    }

    /**
     * Зменшує значення лічильника на одиницю
     */
    public void decrease() {
        decrementAndGet();
    }
}
