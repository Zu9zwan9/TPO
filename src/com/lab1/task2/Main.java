package com.lab1.task2;

/**
 * Створити два потоки, один з яких виводить на консоль символ ‘-‘, а інший – символ ‘|’.
 * Запустіть потоки в основній програмі так, щоб вони виводили свої символи в рядок.
 * <p>
 * Використовуючи найпростіші методи управління потоками,
 * добитись почергового виведення на консоль символів.
 */

public class Main {
    public static void main(String[] args) {
        final Printing printObject = new Printing();
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    printObject.printDash();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    printObject.printSpecial();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
    }
}


class Printing {
    private final int counterLimit = 80;
    private boolean timeToPrint = false;
    private int counter = 0;

    public void printDash() throws InterruptedException {
        synchronized (this) {
            while (!timeToPrint) {
                System.out.print('-');
                counter++;
                if (counter > this.counterLimit) {
                    break;
                }
                this.timeToPrint = true;
                wait();
                notify();
            }
        }
    }

    public void printSpecial() throws InterruptedException {
        synchronized (this) {
            while (timeToPrint) {
                System.out.print('|');
                counter++;
                if (counter > this.counterLimit) {
                    break;
                }
                this.timeToPrint = false;
                notify();
                wait();
            }
        }
    }
}
