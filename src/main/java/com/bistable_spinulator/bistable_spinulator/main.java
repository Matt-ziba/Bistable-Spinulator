package com.bistable_spinulator.bistable_spinulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.*;


public class main extends Application {

    private final double angularSpeed = 20;
    private final double width = Screen.getPrimary().getVisualBounds().getWidth(); double height = Screen.getPrimary().getVisualBounds().getHeight();
    private final double[] center = {width/2, height/2};
    private final double offsetY = 120;
    private final double offsetX = 450;
    private final double[] revolveAxis = {center[0] - offsetX, center[1]};
    private final long spawnPeriod = 800;
    private double angle = 0;
    private int score = 0;
    private int errors = 0;

    private boolean rotate = false;

    Circle PC1 = new Circle(40.0f, Color.rgb(224,108,117));
    Circle PC2 = new Circle(40.0f, Color.rgb(80,181,192));
    Text Score = new Text("Score:");
    Text ScoreValue = new Text(String.valueOf(score));
    Text Mistakes = new Text("Mistakes:");
    Text ErrorValue = new Text(String.valueOf(errors));
    Text TitleScreen = new Text("START GAME");

    Rectangle MenuButton = new Rectangle(575,100);

    private List<Circle> movingCircle = new ArrayList<>();
    private List<Circle> toBeRemoved = new ArrayList<>();

    public Group mainGroup = new Group(PC1,PC2, ScoreValue, ErrorValue, Mistakes, Score, TitleScreen, MenuButton);

    @Override
    public void start(Stage stage) {

        ScoreValue.setTranslateX(center[0] - width/4); ScoreValue.setTranslateY(center[1] - 300);
        ScoreValue.setFill(Color.rgb(171,178,191));
        ScoreValue.setScaleX(7); ScoreValue.setScaleY(7);

        Score.setTranslateX(center[0] - width/4); Score.setTranslateY(center[1] - 390);
        Score.setFill(Color.rgb(171,178,191));
        Score.setScaleX(7); Score.setScaleY(7);

        ErrorValue.setTranslateX(center[0] + width/4); ErrorValue.setTranslateY(center[1] - 300);
        ErrorValue.setFill(Color.rgb(224,108,117));
        ErrorValue.setScaleY(7); ErrorValue.setScaleX(7);

        Mistakes.setTranslateX(center[0] + width/4); Mistakes.setTranslateY(center[1] - 390);
        Mistakes.setFill(Color.rgb(224,108,117));
        Mistakes.setScaleY(7); Mistakes.setScaleX(7);

        TitleScreen.setTranslateX(center[0] - 50); TitleScreen.setTranslateY(center[1] - 50);
        TitleScreen.setFill(Color.WHITE);
        TitleScreen.setScaleY(8); TitleScreen.setScaleX(8);

        MenuButton.setX(center[0] - 300); MenuButton.setY(center[1] - 100);
        MenuButton.toBack();
        MenuButton.setFill(Color.rgb(224,108,117));
        MenuButton.setArcHeight(15); MenuButton.setArcWidth(15);

        PC1.setTranslateX(revolveAxis[0]); PC1.setTranslateY(revolveAxis[1] + offsetY);
        PC2.setTranslateX(revolveAxis[0]); PC2.setTranslateY(revolveAxis[1] - offsetY);
        for (Node node : mainGroup.getChildren()) {
            if(!node.equals(TitleScreen) && !node.equals(MenuButton)) {
                node.setOpacity(0);
                System.out.println(node);
            }

        }

        Scene scene = new Scene(mainGroup, width, height);
        scene.setFill(Color.rgb(40,44,52));

        stage.setFullScreen(true);
        stage.setTitle("Bistable Spinulator");
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().toString().equals("SPACE")) {
                removeNode(TitleScreen);
                removeNode(MenuButton);
                run(stage, scene);
            }
        });
    }
    public void run(Stage stage, Scene scene) {
        Timer period = new Timer();

        for (Node node : mainGroup.getChildren()) {
            node.setOpacity(1);
        }
        scene.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().toString().equals("SPACE")) {
                rotate = true;
            }
            if(keyEvent.getCode().toString().equals("S") || keyEvent.getCode().toString().equals("DOWN")) {
                try {
                    for (Circle enemy : movingCircle) {
                        if ((enemy.getTranslateX() > 278 && enemy.getTranslateX() < 358) && enemy.getTranslateY() == revolveAxis[1] + offsetY) {
                            if (angle == 0 && enemy.getFill().equals(Color.rgb(224, 108, 117))) {
                                killEnemy(enemy, PC1);
                                incrementScore();
                            } else if (angle == 200 && enemy.getFill().equals(Color.rgb(80, 181, 192))) {
                                killEnemy(enemy, PC2);
                                incrementScore();
                            }

                        }
                    }
                } catch (ConcurrentModificationException ignored) {

                }
            }
            if(keyEvent.getCode().toString().equals("W") || keyEvent.getCode().toString().equals("UP")) {
                try {
                    for (Circle enemy : movingCircle) {
                        if ((enemy.getTranslateX() > 278 && enemy.getTranslateX() < 358) && enemy.getTranslateY() == revolveAxis[1] - offsetY) {
                            if (angle == 200 && enemy.getFill().equals(Color.rgb(224, 108, 117))) {
                                killEnemy(enemy, PC1);
                                incrementScore();
                            } else if (angle == 0 && enemy.getFill().equals(Color.rgb(80, 181, 192))) {
                                killEnemy(enemy, PC2);
                                incrementScore();
                            }

                        }
                    }
                } catch (ConcurrentModificationException ignored) {

                }
            }
            if(keyEvent.getCode().toString().equals("R")) {
                reset();
            }
        });

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

        Timer timerTask = new Timer();
        timerTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Circle newCircle = circleSpawn();
                    mainGroup.getChildren().add(newCircle);
                    movingCircle.add(newCircle);
                    for(Circle enemy : toBeRemoved) {
                        removeNode(enemy);
                    }
                });
            }
        }, 0, spawnPeriod);
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
    public void removeNode(Node enemy) {
        if(movingCircle.contains(enemy)) {
            movingCircle.remove(enemy);
        }
        mainGroup.getChildren().remove(enemy);
    }
    public void killEnemy(Circle enemy, Circle PC) {
        movingCircle.remove(enemy);
        mainGroup.getChildren().remove(enemy);
        deathAnimation(PC);
    }
    public void incrementScore() {
        score++;
        ScoreValue.setText(String.valueOf(score));
    }
    public void incrementError() {
        errors++;
         ErrorValue.setText(String.valueOf(errors));
    }
    public void deathAnimation(Circle PC) {
        final int[] radius = {40};
        final double[] opacity = {1.00};
        Circle deathAnimation = new Circle(PC.getTranslateX(), PC.getTranslateY(), PC.getRadius(), Color.rgb(171,178,191));
        deathAnimation.toBack();
        mainGroup.getChildren().add(deathAnimation);
        Timer timerTask = new Timer();
        timerTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(deathAnimation.getRadius() < 80) {
                        deathAnimation.setRadius(radius[0]);
                        deathAnimation.setOpacity(opacity[0]);
                    } else {
                        removeNode(deathAnimation);
                        timerTask.cancel();
                    }
                    radius[0]++;
                    opacity[0] = opacity[0] - 0.025;
                });
            }
        }, 0, 6);
    }
    public void reset() {
        score = 0;
        ScoreValue.setText(String.valueOf(score));
        List<Circle> killList = new ArrayList<>();
        killList.addAll(movingCircle);
        for(Circle c : killList) {
            removeNode(c);
        }
        killList.clear();
    }
}