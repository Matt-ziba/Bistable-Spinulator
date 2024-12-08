package com.bistable_spinulator.bistable_spinulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.*;


public class main extends Application {

    private final double angularSpeed = 20;
    private final double width = Screen.getPrimary().getVisualBounds().getWidth(); double height = Screen.getPrimary().getVisualBounds().getHeight();
    private final double[] center = {width/2, height/2};
    private final double offsetY = 120;
    private final double offsetX = 450;
    private final double[] revolveAxis = {center[0] - offsetX, center[1]};
    private final long spawnPeriod = 800;
    private double lastSpawnTime = 1000;
    private double startTime = 0;
    private double angle = 0;
    private int score = 0;

    private boolean rotate = false;

    Circle PC1 = new Circle(40.0f, Color.rgb(224,108,117));
    Circle PC2 = new Circle(40.0f, Color.rgb(80,181,192));
    Text ScoreBoard = new Text(String.valueOf(score));

    private List<Circle> movingCircle = new ArrayList<>();
    private List<Circle> toBeRemoved = new ArrayList<>();

    public Group mainGroup = new Group(PC1,PC2, ScoreBoard);

    @Override
    public void start(Stage stage) {
        Timer period = new Timer();
        ScoreBoard.setTranslateX(center[0]); ScoreBoard.setTranslateY(center[1] - 300);
        ScoreBoard.setFill(Color.rgb(171,178,191));
        ScoreBoard.setScaleX(5); ScoreBoard.setScaleY(5);
        PC1.setTranslateX(revolveAxis[0]); PC1.setTranslateY(revolveAxis[1] + offsetY);
        PC2.setTranslateX(revolveAxis[0]); PC2.setTranslateY(revolveAxis[1] - offsetY);

        Scene scene = new Scene(mainGroup, width, height);

        scene.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().toString().equals("SPACE")) {
                rotate = true;
            }
            if(keyEvent.getCode().toString().equals("S") || keyEvent.getCode().toString().equals("DOWN")) {
                for(Circle enemy : movingCircle) {
                    if((enemy.getTranslateX() > 278 && enemy.getTranslateX() < 358) && enemy.getTranslateY() == revolveAxis[1] + offsetY) {
                        if(angle == 0 && enemy.getFill().equals(Color.rgb(224,108,117))) {
                            killEnemy(enemy);
                            incrementScore();
                        } else if (angle == 200 && enemy.getFill().equals(Color.rgb(80, 181, 192))) {
                            killEnemy(enemy);
                            incrementScore();
                        }

                    }
                }
            }
            if(keyEvent.getCode().toString().equals("W") || keyEvent.getCode().toString().equals("UP")) {
                for(Circle enemy : movingCircle) {
                    if((enemy.getTranslateX() > 278 && enemy.getTranslateX() < 358) && enemy.getTranslateY() == revolveAxis[1] - offsetY) {
                        if(angle == 200 && enemy.getFill().equals(Color.rgb(224,108,117))) {
                            killEnemy(enemy);
                            incrementScore();
                        } else if (angle == 0 && enemy.getFill().equals(Color.rgb(80, 181, 192))) {
                            killEnemy(enemy);
                            incrementScore();
                        }

                    }
                }
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

                    PC1.setTranslateX(x1); PC1.setTranslateY(y1);
                    PC2.setTranslateX(x2); PC2.setTranslateY(y2);

                    angle += angularSpeed;

                    if(angle == 200) {
                        angle = 200;
                        rotate = false;
                    }
                    if(angle == 380) {
                        angle = 0;
                        rotate = false;
                    }
                }
                for(Circle enemy : movingCircle) {
                    double newX = enemy.getTranslateX() - 10;
                    if(enemy.getTranslateX() < 0) {
                        toBeRemoved.add(enemy);
                    }
                    enemy.setTranslateX(newX);
                }

            }
        };
        timer.start();
        lastSpawnTime = System.nanoTime();

        Timer timerTask = new Timer();
        timerTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Circle newCircle = circleSpawn();
                    mainGroup.getChildren().add(newCircle);
                    movingCircle.add(newCircle);
                    for(Circle enemy : toBeRemoved) {
                        removeEnemy(enemy);
                    }
                });
            }
        }, 0, spawnPeriod);

        scene.setFill(Color.rgb(40,44,52));
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
            color = Color.rgb(224,108,117);
        } else {
            color = Color.rgb(80,181,192);
        }

        Circle enemy = new Circle(40.0f, color);

        if(Math.random() > 0.5) {
            enemy.setTranslateY(revolveAxis[1] + offsetY); enemy.setTranslateX(width);
        }
        else {
            enemy.setTranslateY(revolveAxis[1] - offsetY); enemy.setTranslateX(width);
        }
        return  enemy;
    }
    public void removeEnemy(Circle enemy) {
        movingCircle.remove(enemy);
        mainGroup.getChildren().remove(enemy);
    }
    public void killEnemy(Circle enemy) {
        movingCircle.remove(enemy);
        mainGroup.getChildren().remove(enemy);
    }
    public void incrementScore() {
        score++;
        ScoreBoard.setText(String.valueOf(score));
    }

}