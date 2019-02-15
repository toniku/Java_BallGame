import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Ball_Game extends Application {

    // possible actions
    private enum UserAction{
        NONE, LEFT, RIGHT
    }
    
    // APP Width and Height
    private static final int APP_W = 1600;
    private static final int APP_H = 800;
    
    private static final int BALL_RADIUS = 30; // ball size
    private static final int BAR_W = 100; // bar size
    private static final int BAR_H = 30;
    private static int scorePoints = 0;
    private static int prevScorePoints = 0;
    
    private final Circle ball = new Circle(BALL_RADIUS);
    private final Rectangle bar = new Rectangle(BAR_W, BAR_H);   
    private final Text score = new Text("Score: " + scorePoints + " Previous: " + prevScorePoints);
    
    private boolean ballUp, ballLeft = true;
    private UserAction action = UserAction.NONE; 
    
    private final Timeline timeline = new Timeline();
    private boolean running = true;
    
    
    private Parent createContent(){ 
        ballUp = true;
        Pane root = new Pane();
        root.setPrefSize(APP_W, BAR_H);
        score.setFill(Color.BLUE);
        score.setFont(new Font(30));
        score.setTranslateX(5);
        score.setTranslateY(30);
        
        bar.setTranslateX(APP_W / 2);
        bar.setTranslateY(APP_H - BAR_H);
        bar.setFill(Color.CADETBLUE);

        // create new frame and define a speed
        KeyFrame frame = new KeyFrame(Duration.seconds(0.008), (ActionEvent event) -> {
            if (!running)
                return;
            
            // bar moving coming from actions
            switch (action) {
                case LEFT:
                    if (bar.getTranslateX() - 6 > 0)
                        bar.setTranslateX(bar.getTranslateX() - 6);
                    break;
                case RIGHT:
                    if (bar.getTranslateX() + BAR_W + 6 < APP_W)
                        bar.setTranslateX(bar.getTranslateX() + 6);
                    break;
                case NONE:
                    break;
            }
            
            // if ball moves to the left, we will reduce 5 from it's X location, if not then add 5...
            ball.setTranslateX(ball.getTranslateX() + (ballLeft ? -5 : 5));
            ball.setTranslateY(ball.getTranslateY() + (ballUp ? -5 : 5));
            
            
            if (ball.getTranslateX() - BALL_RADIUS == 0)
                ballLeft = false;
            else if (ball.getTranslateX() + BALL_RADIUS == APP_W)
                ballLeft = true;
            
            if (ball.getTranslateY() - BALL_RADIUS == 0)
                ballUp = false;
            
            // check to see if ball hits the bar
            //when ball hits the bar we will put it's state up and add score
            else if (ball.getTranslateY() + BALL_RADIUS == APP_H - BAR_H
                    && ball.getTranslateX() + BALL_RADIUS > bar.getTranslateX() - BAR_W
                    && ball.getTranslateX() - BALL_RADIUS < bar.getTranslateX() + BAR_W)
                    
            {
                scorePoints++;
                score.setText("Score: " + scorePoints + " Previous: " + prevScorePoints);
                ballUp = true;
            }
            
            // if the ball doesn't hit the bar, the game will restart after 3s
            if (ball.getTranslateY() + BALL_RADIUS > APP_H)
            {
               try {
                    prevScorePoints = scorePoints;
                    scorePoints = 0;
                    score.setText("Score: " + scorePoints + " Previous: " + prevScorePoints);
                    Thread.sleep(3000); // delay before new game
                    restartGame();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Ball_Game.class.getName()).log(Level.SEVERE, null, ex);
                    
                    }
            }
        });
        // timeline animation
        timeline.getKeyFrames().add(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        
        // adding the objects to the childgroup, (screen)
        root.getChildren().addAll(ball, bar, score);
        return root;
    }
    
    private void restartGame() {
        stopGame();
        startGame();
    }
    
    private void stopGame() {
        running = false;
        timeline.stop();
    }
    
    private void startGame() {
        
        ballUp = true;
        // ball X and Y cordinates at the start
        ball.setTranslateX(50);
        ball.setTranslateY(100);    
        timeline.play();
        running = true;
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {       
        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed(event -> { // when key is pressed
            switch (event.getCode()) {
                case LEFT:
                    action = UserAction.LEFT;
                    break;
                case RIGHT:
                    action = UserAction.RIGHT;
                    break;
            }
        });
                scene.setOnKeyReleased(event -> { // when key is released
            switch (event.getCode()) {
                case LEFT:
                    action = UserAction.NONE;
                    break;
                case RIGHT:
                    action = UserAction.NONE;
                    break;
            }
        });
                
        primaryStage.setWidth(APP_W);
        primaryStage.setHeight(APP_H + BALL_RADIUS);
        primaryStage.setTitle("Ball_Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        startGame();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}