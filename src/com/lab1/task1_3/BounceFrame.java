package com.lab1.task1_3;

import javax.swing.*;
import java.awt.*;

public class BounceFrame extends JFrame {
    private int ballsCounter = 0;

    public static final int WIDTH = 450;
    public static final int HEIGHT = 350;

    private final BallCanvas canvas;

    private final JLabel valueLabel = new JLabel("---");
    private int redBallsCounter = 0;
    private int blueBallsCounter = 0;

    private Color currentColor; // змінна для зберігання поточного кольору

    private Ball redBall = null; // додали змінну для червоної кульки
    private boolean isRedBallCreated = false; // прапорець для перевірки, чи червона кулька вже створена

    public BounceFrame() {
        this.setSize(WIDTH, HEIGHT);
        this.setTitle("Bounce program");

        this.canvas = new BallCanvas(this);
        Container content = this.getContentPane();
        content.add(this.canvas, BorderLayout.CENTER);

        JPanel lowPanel = new JPanel();
        lowPanel.setBackground(Color.darkGray);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.lightGray);

        JButton buttonStart = new JButton("Start");
        JButton buttonStop = new JButton("Stop");
        JButton buttonCreateRed = new JButton("Create Red Ball");
        JButton buttonCreateBlue = new JButton("Create Blue Ball");
        JButton buttonReset = new JButton("Reset");
        buttonReset.addActionListener(e -> {
            this.canvas.removeAll();
            this.ballsCounter = 0;
            this.valueLabel.setText("---");
        });

        buttonPanel.add(buttonStart);
        buttonPanel.add(buttonStop);
        buttonPanel.add(buttonReset); // додали кнопку Reset

        buttonCreateRed.addActionListener(e -> {
            currentColor = Color.RED;
            Ball br = new Ball(this.canvas, Color.RED);
            this.canvas.add(br);
            BallThread thread = new BallThread(br);
            thread.start();
            System.out.println("Thread name = " + thread.getName());
            this.redBallsCounter++;
            upCounter();
        });

        buttonCreateBlue.addActionListener(e -> {
            currentColor = Color.BLUE;
            Ball bb = new Ball(this.canvas, Color.BLUE);
            this.canvas.add(bb);
            BallThread thread = new BallThread(bb);
            thread.start();
            System.out.println("Thread name = " + thread.getName());
            this.blueBallsCounter++;
            upCounter();
        });

        buttonStart.addActionListener(e -> {
            if (!isRedBallCreated) { // якщо червона кулька ще не створена
                this.redBall = new Ball(canvas, Color.RED);
                canvas.add(redBall);
                BallThread redThread = new BallThread(redBall);
                redThread.setPriority(Thread.MAX_PRIORITY);
                redThread.start();
                System.out.println("Red thread name = " + redThread.getName()
                        + ", Priority = " + redThread.getPriority());
                isRedBallCreated = true; // встановлюємо прапорець на true
            } else {
                Ball b;
                if (currentColor == Color.RED) { // створюємо червону кульку, якщо поточний колір червоний
                    b = new Ball(canvas, Color.RED);
                } else { // якщо колір синій
                    b = new Ball(canvas, Color.BLUE);
                }
                canvas.add(b);
                BallThread thread = new BallThread(b);
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
                System.out.println("Thread name = " + thread.getName());
            }
        });
        buttonStop.addActionListener(e -> System.exit(0));

        buttonPanel.add(buttonStart);
        buttonPanel.add(buttonCreateRed);
        buttonPanel.add(buttonCreateBlue);
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
        this.valueLabel.setText(Integer.toString(this.ballsCounter) + " (Red: " + this.redBallsCounter + ", Blue: " + this.blueBallsCounter + ")");
    }
}
