package com.lab1.task1_join;

/**
 * Побудувати ілюстрацію для методу join() класу Thread з використанням руху більярдних кульок різного кольору.
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

