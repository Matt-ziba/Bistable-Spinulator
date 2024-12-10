package com.bistable_spinulator.bistable_spinulator;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

import java.util.*;


public class main extends Application {

    private final double angularSpeed = 20;
    private final double width = Screen.getPrimary().getVisualBounds().getWidth();
    private final double height = Screen.getPrimary().getVisualBounds().getHeight();
    private final double[] center = {width / 2, height / 2};
    private final double offsetY = 120;
    private final double offsetX = 450;
    private final double[] revolveAxis = {center[0] - offsetX, center[1] + 60};
    private double moveSpeed = 7;
    private double angle = 0;
    private int score = 0;
    private int errors = 0;

    private boolean rotate = false;

    Color red = Color.rgb(224, 108, 117);
    Color blue = Color.rgb(80, 181, 192);
    Color gray = Color.rgb(171, 178, 191);

    Rectangle mistake = new Rectangle(width + 500, height + 500);
    Rectangle MenuButton = new Rectangle(1000, 175);

    Circle PC1 = new Circle(40.0f, red);
    Circle PC2 = new Circle(40.0f, blue);

    Text Score = new Text("SCORE:");
    Text ScoreValue = new Text(String.valueOf(score));
    Text Mistakes = new Text("MISTAKES:");
    Text ErrorValue = new Text(String.valueOf(errors));
    Text TitleScreen = new Text("START GAME");
    Text Controls = new Text("                  CONTROLS: \nR: Reset                          W/↑: Top\nSPACE: Rotate                S/↓: Bottom");


    private List<Circle> movingCircle = new ArrayList<>();
    private List<Circle> toBeRemoved = new ArrayList<>();

    public Group mainGroup = new Group(mistake, PC1, PC2, ScoreValue, ErrorValue, Mistakes, Score, TitleScreen, MenuButton, Controls);

    @Override
    public void start(Stage stage) {
        mistakeTimeline.setCycleCount(1);

        mistake.setFill(red);

        ScoreValue.setTranslateX(center[0] - width / 4);
        ScoreValue.setTranslateY(center[1] - 300);
        ScoreValue.setFill(gray);
        ScoreValue.setScaleX(7);
        ScoreValue.setScaleY(7);

        Score.setTranslateX(center[0] - width / 4);
        Score.setTranslateY(center[1] - 390);
        Score.setFill(gray);
        Score.setScaleX(7);
        Score.setScaleY(7);

        ErrorValue.setTranslateX(center[0] + width / 4);
        ErrorValue.setTranslateY(center[1] - 300);
        ErrorValue.setFill(red);
        ErrorValue.setScaleY(7);
        ErrorValue.setScaleX(7);

        Mistakes.setTranslateX(center[0] + width / 4);
        Mistakes.setTranslateY(center[1] - 390);
        Mistakes.setFill(red);
        Mistakes.setScaleY(7);
        Mistakes.setScaleX(7);

        TitleScreen.setTranslateX(center[0] - 40);
        TitleScreen.setTranslateY(center[1] - 90);
        TitleScreen.setFill(Color.WHITE); TitleScreen.toFront();
        TitleScreen.setScaleY(13); TitleScreen.setScaleX(13);

        Controls.setTranslateX(center[0] - 75);
        Controls.setTranslateY(center[1] + 200);
        Controls.setFill(gray); Controls.setOpacity(0.6);
        Controls.setScaleY(5);
        Controls.setScaleX(5);

        MenuButton.setX(center[0] - 500);
        MenuButton.setY(center[1] - 175);
        MenuButton.toBack();
        MenuButton.setFill(red);
        MenuButton.setArcHeight(37.5);
        MenuButton.setArcWidth(37.5);

        PC1.setTranslateX(revolveAxis[0]);
        PC1.setTranslateY(revolveAxis[1] + offsetY);
        PC2.setTranslateX(revolveAxis[0]);
        PC2.setTranslateY(revolveAxis[1] - offsetY);

        for (Node node : mainGroup.getChildren()) {
            if (!node.equals(TitleScreen) && !node.equals(MenuButton) && !node.equals(Controls)) {
                node.setOpacity(0);
            }

        }
        mistake.setOpacity(0);

        Scene scene = new Scene(mainGroup, width, height);
        scene.setFill(Color.rgb(40, 44, 52));

        stage.setFullScreen(true);
        stage.setTitle("Bistable Spinulator");
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().toString().equals("SPACE")) {
                removeNode(TitleScreen);
                removeNode(MenuButton);
                removeNode(Controls);
                run(stage, scene);
            }
        });
        TitleScreen.setOnMouseClicked(event -> {
            removeNode(TitleScreen);
            removeNode(MenuButton);
            removeNode(Controls);
            run(stage, scene);
        });
    }

    public void run(Stage stage, Scene scene) {

        for (Node node : mainGroup.getChildren()) {
            node.setOpacity(1);
        }
        mistake.setOpacity(0);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().toString().equals("SPACE")) {
                rotate = true;
            }
            if (keyEvent.getCode().toString().equals("S") || keyEvent.getCode().toString().equals("DOWN")) {
                try {
                    for (Circle enemy : movingCircle) {
                        if ((enemy.getTranslateX() > 278 && enemy.getTranslateX() < 358) && enemy.getTranslateY() == revolveAxis[1] + offsetY) {
                            if (angle == 0 && enemy.getFill().equals(red)) {
                                killEnemy(enemy, PC1, true);
                                incrementScore();
                            } else if (angle == 200 && enemy.getFill().equals(blue)) {
                                killEnemy(enemy, PC2, true);
                                incrementScore();
                            } else if (angle == 0 && enemy.getFill().equals(blue)) {
                                killEnemy(enemy, PC1, true);
                                mistakeAnimation();
                            } else if (angle == 200 && enemy.getFill().equals(red)) {
                                killEnemy(enemy, PC2, true);
                                mistakeAnimation();
                            }

                        }
                    }
                } catch (ConcurrentModificationException ignored) {

                }
            }
            if (keyEvent.getCode().toString().equals("W") || keyEvent.getCode().toString().equals("UP")) {
                try {
                    for (Circle enemy : movingCircle) {
                        if ((enemy.getTranslateX() > 278 && enemy.getTranslateX() < 358) && enemy.getTranslateY() == revolveAxis[1] - offsetY) {
                            if (angle == 200 && enemy.getFill().equals(red)) {
                                killEnemy(enemy, PC1, true);
                                incrementScore();
                            } else if (angle == 0 && enemy.getFill().equals(blue)) {
                                killEnemy(enemy, PC2, true);
                                incrementScore();
                            }else if (angle == 200 && enemy.getFill().equals(blue)) {
                                killEnemy(enemy, PC1, true);
                                mistakeAnimation();
                            } else if (angle == 0 && enemy.getFill().equals(red)) {
                                killEnemy(enemy, PC2, true);
                                mistakeAnimation();
                            }

                        }
                    }
                } catch (ConcurrentModificationException ignored) {

                }
            }
            if (keyEvent.getCode().toString().equals("R")) {
                reset();
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (rotate) {
                    double rad = Math.toRadians(angle);
                    double x1 = revolveAxis[0] + offsetY * Math.sin(rad);
                    double y1 = revolveAxis[1] + offsetY * Math.cos(rad);
                    double x2 = revolveAxis[0] + offsetY * Math.sin(rad + Math.PI);
                    double y2 = revolveAxis[1] + offsetY * Math.cos(rad + Math.PI);

                    PC1.setTranslateX(x1);
                    PC1.setTranslateY(y1);
                    PC2.setTranslateX(x2);
                    PC2.setTranslateY(y2);

                    angle += angularSpeed;

                    if (angle == 200) {
                        rotate = false;
                    }
                    if (angle == 380) {
                        angle = 0;
                        rotate = false;
                    }
                }

                for (Circle enemy : movingCircle) {
                    double newX = enemy.getTranslateX() - moveSpeed;
                    if (enemy.getTranslateX() < 0) {
                        toBeRemoved.add(enemy);
                    }
                    enemy.setTranslateX(newX);
                }

            }
        };
        timer.start();

        Timer timerTask = new Timer();
        long spawnPeriod = 800;
        timerTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (score >= 20) {
                        moveSpeed += 0.1;
                    }
                    Circle newCircle = circleSpawn();
                    mainGroup.getChildren().add(newCircle);
                    movingCircle.add(newCircle);
                    for (Circle enemy : toBeRemoved) {
                        removeNode(enemy);
                        mistakeTimeline.play();

                    }
                    toBeRemoved.clear();
                });
            }
        }, 0, spawnPeriod);
    }

    public static void main(String[] args) {
        launch();
    }


    public Circle circleSpawn() {
        Color color;
        if (Math.random() > 0.5) {
            color = red;
        } else {
            color = blue;
        }

        Circle enemy = new Circle(40.0f, color);

        if (Math.random() > 0.5) {
            enemy.setTranslateY(revolveAxis[1] + offsetY);
            enemy.setTranslateX(width);
        } else {
            enemy.setTranslateY(revolveAxis[1] - offsetY);
            enemy.setTranslateX(width);
        }
        return enemy;
    }

    public void removeNode(Node enemy) {
        if (movingCircle.contains(enemy)) {
            movingCircle.remove(enemy);
        }
        mainGroup.getChildren().remove(enemy);
    }

    public void killEnemy(Circle enemy, Circle PC, boolean success) {
        movingCircle.remove(enemy);
        mainGroup.getChildren().remove(enemy);
        if(success) {
            deathAnimation(PC);
        }
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
        Circle deathAnimation = new Circle(PC.getTranslateX(), PC.getTranslateY(), PC.getRadius(), gray);
        deathAnimation.toBack();
        mainGroup.getChildren().add(deathAnimation);
        Timer timerTask = new Timer();
        timerTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (deathAnimation.getRadius() < 80) {
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

    public void mistakeAnimation() {
        incrementError();
        final double[] opacity = {1};
        mistake.setOpacity(1);
        Timer timerTask = new Timer();
        timerTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(opacity[0] >= 0.05) {
                        opacity[0] -= 0.05;
                        mistake.setOpacity(opacity[0]);
                    } else if(mistake.getOpacity() <  0.05) {
                        mistake.setOpacity(0);
                        timerTask.cancel();
                    }
                });
            }
            },0, 20 );
        mistakeTimeline.stop();
    }
    public void reset() {
        score = 0;
        errors = 0;
        ScoreValue.setText(String.valueOf(score));
        ErrorValue.setText(String.valueOf(score));
        List<Circle> killList = new ArrayList<>(movingCircle);
        for(Circle c : killList) {
            removeNode(c);
        }
        killList.clear();
    }
    Timeline mistakeTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
        mistakeAnimation();
    }));
}