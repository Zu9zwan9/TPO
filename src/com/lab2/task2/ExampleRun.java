package com.lab2.task2;

/**
 * Реалізувати приклад Producer-Consumer application
 * (див. https://docs.oracle.com/javase/tutorial/essential/concurrency/guardmeth.html ).
 * Модифікувати масив даних цієї програми, які читаються, у масив чисел заданого розміру (100, 1000 або 5000)
 * та протестуйте програму.
 */

public class ExampleRun {
    public static void main(String[] args) {
        Drop drop = new Drop();
        (new Thread(new Producer(drop, 100))).start();
        (new Thread(new TaskConsumer(drop))).start();
    }
}
