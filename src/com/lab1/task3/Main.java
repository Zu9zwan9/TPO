package com.lab1.task3;

import java.util.concurrent.CountDownLatch;

/**
 * Створіть клас Counter з методами increment() та decrement(),
 * які збільшують та зменшують значення лічильника відповідно.
 * Створіть два потоки, один з яких збільшує 100000 разів значення лічильника,
 * а інший –зменшує 100000 разів значення лічильника.
 * <p>
 * Використовуючи синхронізований доступ, добийтесь правильної роботи лічильника при одночасній роботі з ним
 * двох і більше потоків.
 */

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Counter counterInstance = new Counter();
        CountDownLatch latch = new CountDownLatch(2);

        for (char value : new char[]{'-', '+'}) {
            new Modifier(latch, value, counterInstance, 100000).start();
        }

        // очікування закінчення усіх потоків
        latch.await();

        // перевірка значення лічильника
        System.out.println((counterInstance.get() == 0) ? "Check succeed" : "Check not succeed");
    }
}
