package com.lab1.task1_join;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BallCanvas extends JPanel {

    private final ArrayList<Ball> balls = new ArrayList<>();
    private final BounceFrame frame;

    public BallCanvas(BounceFrame f) {
        this.frame = f;
    }

    public void add(Ball b) {
        this.balls.add(b);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        ArrayList<Ball> inactiveBalls = new ArrayList<>();
        for (Ball b : balls) {
            if (b.isActive) {
                b.draw(g2);
            } else {
                inactiveBalls.add(b);
            }
        }

        for (Ball b : inactiveBalls) {
            balls.remove(b);
            this.frame.upCounter();
        }
    }

}

