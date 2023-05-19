package com.lab1.task1;

import javax.swing.*;
import java.awt.*;

public class BounceFrame extends JFrame {

    public static final int WIDTH = 450;
    public static final int HEIGHT = 350;

    private final BallCanvas canvas;

    private final JLabel valueLabel = new JLabel("---");
    private int ballsCounter = 0;

    public BounceFrame() {
        this.setSize(WIDTH, HEIGHT);
        this.setTitle("Bounce program");

        this.canvas = new BallCanvas(this);
        System.out.println("In Frame Thread name = "
                + Thread.currentThread().getName());
        Container content = this.getContentPane();
        content.add(this.canvas, BorderLayout.CENTER);

        JPanel lowPanel = new JPanel();
        lowPanel.setBackground(Color.darkGray);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.lightGray);

        JButton buttonStart = new JButton("Start");
        JButton buttonStop = new JButton("Stop");

        buttonStart.addActionListener(e -> {

            Ball b = new Ball(canvas);
            canvas.add(b);

            BallThread thread = new BallThread(b);
            thread.start();
            System.out.println("Thread name = " + thread.getName());
        });

        buttonStop.addActionListener(e -> System.exit(0));

        buttonPanel.add(buttonStart);
        buttonPanel.add(buttonStop);

        JPanel counterPanel = new JPanel();
        counterPanel.setBackground(Color.lightGray);

        JLabel staticLabel = new JLabel("Balls counter: ");
        staticLabel.setFont(new Font("Verdana", Font.BOLD, 16));

        valueLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        counterPanel.add(staticLabel);
        counterPanel.add(valueLabel);
        staticLabel.setVerticalAlignment(JLabel.CENTER);
        valueLabel.setVerticalAlignment(JLabel.CENTER);

        lowPanel.setLayout(new GridLayout(1, 2));
        lowPanel.add(buttonPanel, BorderLayout.CENTER);
        lowPanel.add(counterPanel, BorderLayout.CENTER);

        content.add(lowPanel, BorderLayout.SOUTH);
    }

    public void upCounter() {
        this.ballsCounter += 1;
        this.valueLabel.setText(Integer.toString(this.ballsCounter));
    }
}

