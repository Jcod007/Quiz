package com.example.superquiz;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class
AnimatedBackgroundView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private final List<Shape> shapes = new ArrayList<>();
    private final ValueAnimator animator;

    public AnimatedBackgroundView(Context context) {
        this(context, null);
    }

    public AnimatedBackgroundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Créer des formes aléatoires
        for (int i = 0; i < 15; i++) {
            shapes.add(createRandomShape());
        }

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(10000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> invalidate());
        animator.start();
    }

    private static class Shape {
        float x, y, size, speed, angle;
        int color, type;

        Shape(float x, float y, float size, int color, int type, float speed, float angle) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.type = type;
            this.speed = speed;
            this.angle = angle;
        }
    }

    private Shape createRandomShape() {
        return new Shape(
                random.nextFloat() * getWidth(),
                random.nextFloat() * getHeight(),
                20f + random.nextFloat() * 40f,
                Color.argb(50, random.nextInt(256), random.nextInt(256), 255),
                random.nextInt(3),
                1f + random.nextFloat() * 2f,
                random.nextFloat() * 360f
        );
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (Shape shape : shapes) {
            // Mettre à jour la position
            shape.x += (float) (Math.cos(Math.toRadians(shape.angle)) * shape.speed);
            shape.y += (float) (Math.sin(Math.toRadians(shape.angle)) * shape.speed);

            // Vérifier les limites
            if (shape.x < -shape.size) shape.x = getWidth();
            if (shape.x > getWidth() + shape.size) shape.x = -shape.size;
            if (shape.y < -shape.size) shape.y = getHeight();
            if (shape.y > getHeight() + shape.size) shape.y = -shape.size;

            // Dessiner la forme
            paint.setColor(shape.color);
            switch (shape.type) {
                case 0: // Cercle
                    canvas.drawCircle(shape.x, shape.y, shape.size / 2, paint);
                    break;
                case 1: // Carré
                    canvas.drawRect(
                            shape.x - shape.size / 2,
                            shape.y - shape.size / 2,
                            shape.x + shape.size / 2,
                            shape.y + shape.size / 2,
                            paint
                    );
                    break;
                case 2: // Triangle
                    @SuppressLint("DrawAllocation") Path path = new Path();
                    path.moveTo(shape.x, shape.y - shape.size / 2);
                    path.lineTo(shape.x - shape.size / 2, shape.y + shape.size / 2);
                    path.lineTo(shape.x + shape.size / 2, shape.y + shape.size / 2);
                    path.close();
                    canvas.drawPath(path, paint);
                    break;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        animator.cancel();
        super.onDetachedFromWindow();
    }
}

