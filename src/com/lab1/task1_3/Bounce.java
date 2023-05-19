package com.lab1.task1_3;

/**
 * Реалізувати програму імітації руху більярдних кульок (код у додатку),
 * в якій рух кожної кульки відтворюється в окремому потоці.
 * <p>
 * Модифікувати програму так, щоб при потраплянні в «лузу» кульки зникали,
 * а відповідний потік завершував свою роботу.  Кількість кульок, яка потрапила в «лузу»,
 * має динамічно відображатись у текстовому полі інтерфейсу програми.
 * <p>
 * Модифікувати програму «Більярдна кулька» так,
 * щоб кульки червоного кольору створювались з вищим пріоритетом потоку, в якому вони виконують рух,
 * ніж кульки синього кольору.
 */

import javax.swing.*;

public class Bounce {

    public static void main(String[] args) {
        BounceFrame frame = new BounceFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
        System.out.println("Thread name = " + Thread.currentThread().getName());

    }
}

