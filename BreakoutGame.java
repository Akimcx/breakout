package breakout;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BreakoutGame extends Application {
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;
	private static final int SCREEN_LEFT_RIGHT_MARGIN = 25;
	private static final int SCREEN_TOP_MARGIN = 250;
	private static final int BRICK_X_GAP = 5;
	private static final int BRICK_Y_GAP = 5;
	private static final int MAX_BRICKS = 10;
	private static final int LINES_COUNT = 5;
	private static final long frameTimeNanos = 1_000_000_000L / 60L; // 60 fps
	private double ballVelocityX = 350;
	private double ballVelocityY = 350;
	private boolean running = false;
	private int ballWidth = 15;

	private Pane root;
	private Rectangle ball; // = new Circle(10, Color.RED);;
	private Rectangle paddle; // = new Rectangle(100, 15, Color.BLUE);;
	private List<Rectangle> bricks;
	private Label score;
	int step = 10;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		root = new Pane();
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

		primaryStage.setTitle("Breakout Game");
		primaryStage.setScene(scene);
		primaryStage.show();

		scene.setOnKeyPressed(event -> {
			switch (event.getCode()) {
			case SPACE:
				running = !running;
				break;
			case LEFT: 
				if(paddle.getLayoutX() - step >= 0) {
					paddle.setLayoutX(paddle.getLayoutX() - step);
				}

				break;
			case RIGHT:
				if (paddle.getLayoutX() + paddle.getWidth() + step <= WINDOW_WIDTH) {
					paddle.setLayoutX(paddle.getLayoutX() + step);
				}
				break;
			default:
				break;
			}
		});

		intialize();
		drawScene();

		running = false;

		new AnimationTimer() {
			long lastUpdate = 0;

			@Override
			public void handle(long now) {
				if (now - lastUpdate >= frameTimeNanos && running) {
					updateGameState();

					drawScene();

					lastUpdate = now;
				}
			}
		}.start();
	}

	private void intialize() {
		score = new Label("Score: ");
		score.setLayoutX(SCREEN_LEFT_RIGHT_MARGIN);
		paddle = new Rectangle(100, 15, Color.BLUE);
		//		paddle.setArcWidth(10);
		//		paddle.setArcHeight(10);
		paddle.setLayoutX(WINDOW_WIDTH / 2 - paddle.getWidth() / 2);
		paddle.setLayoutY(WINDOW_HEIGHT - paddle.getHeight() - 20);

		ball = new Rectangle(ballWidth,ballWidth, Color.RED);
		ball.setArcHeight(500);
		ball.setArcWidth(500);

		ball.setLayoutX(paddle.getLayoutX());
		ball.setLayoutY(paddle.getLayoutY()-ball.getWidth());
		bricks = new ArrayList<>();
		int brickWidth = (WINDOW_WIDTH - SCREEN_LEFT_RIGHT_MARGIN * 2) / MAX_BRICKS - BRICK_X_GAP;
		double offsetHeight = SCREEN_TOP_MARGIN;
		for (int i = 0; i < LINES_COUNT; i++) {
			double offsetWidth = SCREEN_LEFT_RIGHT_MARGIN;
			for (int j = 0; j < MAX_BRICKS; j++) {
				Rectangle brick = new Rectangle(brickWidth, 10, Color.GREEN);
				brick.setLayoutX(offsetWidth);
				offsetWidth += brick.getWidth() + BRICK_X_GAP;
				brick.setLayoutY(offsetHeight);
				bricks.add(brick);
			}
			offsetHeight -= 20 + BRICK_Y_GAP;
		}
	}

	private int score_number = 0;
	private void drawScene() {
		score.setText("Score: "+score_number);
		root.getChildren().removeAll(root.getChildren());
		root.getChildren().addAll(bricks);
		root.getChildren().add(ball);
		root.getChildren().add(paddle);
		root.getChildren().add(score);
	}

	private void updateGameState() {

		ball.setLayoutX(ball.getLayoutX() + ballVelocityX * frameTimeNanos / 1_000_000_000L);
		ball.setLayoutY(ball.getLayoutY() + ballVelocityY * frameTimeNanos / 1_000_000_000L);

		if(ball.getLayoutX() + ball.getWidth() >= WINDOW_WIDTH || ball.getLayoutX() - ball.getWidth() <= 0) {
			ballVelocityX = -ballVelocityX;
		}

		if(ball.getLayoutY() - ball.getWidth() <= 0) {
			ballVelocityY = -ballVelocityY;
		}

		if (ball.getLayoutY() > paddle.getLayoutY() - ball.getWidth() ) {
			if(ball.getLayoutX() >= paddle.getLayoutX() && ball.getLayoutX() <= paddle.getLayoutX()+paddle.getWidth()) {
				ballVelocityY = -ballVelocityY;
			}
		}

		if(ball.getLayoutY() + ball.getHeight() >= WINDOW_HEIGHT) {
			running = false;
		}

		for (int i = 0; i < bricks.size(); i++) {
			Rectangle brick = bricks.get(i);
			if(ball.getLayoutX() >= brick.getLayoutX() && ball.getLayoutX() <= brick.getLayoutX()+brick.getWidth())
				if(ball.getLayoutY() <= brick.getLayoutY() + brick.getHeight()) {
 					bricks.remove(brick);
 					score_number += 10;
					ballVelocityY = -ballVelocityY;
				}
		}
	}
}
