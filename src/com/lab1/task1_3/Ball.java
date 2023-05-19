package com.lab1.task1_3;


import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

class Ball {
    private static final int xSize = 20;
    private static final int ySize = 20;
    private static final int pocketShift = 20;
    public final Color color;
    private final BallCanvas canvas;
    public boolean isActive = true;
    private int x;
    private int y;
    private int dx = 2;
    private int dy = 2;

    public Ball(BallCanvas c, Color currentColor) {
        this.canvas = c;


        this.color = currentColor;
        if (Math.random() < 0.5) {
            x = new Random().nextInt(this.canvas.getWidth());
            y = 0;
        } else {
            x = 0;
            y = new Random().nextInt(this.canvas.getHeight());
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(this.color);
        g2.fill(new Ellipse2D.Double(x, y, xSize, ySize));

    }

    public boolean move() {
        x += dx;
        y += dy;
        if (x < 0) {
            x = 0;
            dx = -dx;
        }
        if (x + xSize >= this.canvas.getWidth()) {
            x = this.canvas.getWidth() - xSize;
            dx = -dx;
        }
        if (y < 0) {
            y = 0;
            dy = -dy;
        }
        if (y + ySize >= this.canvas.getHeight()) {
            y = this.canvas.getHeight() - ySize;
            dy = -dy;
        }

        if (touchDown()) {
            this.isActive = false;
            return true;
        } else {
            this.canvas.repaint();
            return false;
        }
    }

    private boolean touchDown() {
        int xCenter = (int) Math.round(this.canvas.getWidth() / 2.0);
        int yCenter = (int) Math.round(this.canvas.getHeight() / 2.0);

        if ((x == 0) | (x == this.canvas.getWidth())) {
            return (yCenter - pocketShift < y) && (y < yCenter + pocketShift);
        } else if ((y == 0) | (y == this.canvas.getHeight())) {
            return (xCenter - pocketShift < x) && (x < xCenter + pocketShift);
        }
        return false;
    }
}

