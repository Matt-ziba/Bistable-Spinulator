package com.bistable_spinulator.bistable_spinulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;


public class main extends Application {

    public static Date now;
    private final double angularSpeed = 10;
    private final double width = Screen.getPrimary().getVisualBounds().getWidth(); double height = Screen.getPrimary().getVisualBounds().getHeight();
    private final double[] center = {width/2, height/2};
    private final double offsetY = 120;
    private final double offsetX = 450;
    private final double[] revolveAxis = {center[0] - offsetX, center[1]};
    private final long spawnPeriod = 1000;
    private double lastSpawnTime = 1000;
    private double startTime = 0;
    private double angle = 0;

    private boolean rotate = false;

    Circle PC1 = new Circle(40.0f, Color.rgb(255,0,0));
    Circle PC2 = new Circle(40.0f, Color.rgb(0,0,255));

    private List<Circle> movingCircle = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        Timer period = new Timer();
        PC1.setTranslateX(revolveAxis[0]); PC1.setTranslateY(revolveAxis[1] + offsetY);
        PC2.setTranslateX(revolveAxis[0]); PC2.setTranslateY(revolveAxis[1] - offsetY);

        Group mainGroup = new Group(PC1,PC2);
        Scene scene = new Scene(mainGroup, width, height);

        scene.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().toString().equals("SPACE")) {
                rotate = true;
            }
        });

        startTime = System.currentTimeMillis();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if(rotate) {
                    double rad = Math.toRadians(angle);
                    double x1 = revolveAxis[0] + offsetY * Math.sin(rad);
                    double y1 = revolveAxis[1] + offsetY * Math.cos(rad);
                    double x2 = revolveAxis[0] + offsetY * Math.sin(rad + Math.PI);
                    double y2 = revolveAxis[1] + offsetY * Math.cos(rad + Math.PI);
                    if(angle >= 190) {
                        PC2.setTranslateX(x1); PC1.setTranslateY(y1);
                        PC1.setTranslateX(x2); PC2.setTranslateY(y2);
                    }
                    PC1.setTranslateX(x1); PC1.setTranslateY(y1);
                    PC2.setTranslateX(x2); PC2.setTranslateY(y2);

                    angle += angularSpeed;

                    if(angle == 190) {
                        angle = 190;
                        rotate = false;
                    }
                    if(angle % 370 == 0) {
                        angle = 0;
                        rotate = false;
                    }
                }
                for(Circle enemy : movingCircle) {
                    double newX = enemy.getTranslateX() - 15;
                    enemy.setTranslateX(newX);
                }

            }
        };
        timer.start();
        lastSpawnTime = System.nanoTime();



        stage.setTitle("Bistable Spinulator");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

    public Circle circleSpawn() {

        Color color;
        double pos;

        if(Math.random() > 0.5) {
            color = Color.RED;
        } else {
            color = Color.BLUE;
        }

        if(Math.random() > 0.5) {
            pos = center[1] + offsetY;
        } else {
            pos = center[1] - offsetY;
        }
        System.out.println("new circle");
        return new Circle(width, pos, 40, color);

    }

}