package view;

import core.Board;
import core.Hex;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.StrictMath.sqrt;

public class Game extends Application {

    private int size;

    private final double W = 47.0;         // width of a single hex on the screen.
    private final double H = W / 2 * sqrt(3);  //height of a hex

    private Board board;
    private boolean gameOver = false;

    private final Double[] coords = new Double[]{  //coordinates of a single hexagon
            W * 0.25, 0.0,
            W * 0.75, 0.0,
            W, H * 0.5,
            W * 0.75, H,
            W * 0.25, H,
            0.0, H * 0.5
    };

    private Text status;
    private Text flagsText;
    private final String loseMessage = "YOU LOSE!";
    private final String winMessage = "YOU WIN!";

    private List<HexView> allCells;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        allCells = new ArrayList<>();

        StartDialog dialog = new StartDialog();

        Optional<ButtonType> result = dialog.showAndWait();
        int bombs;
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            size = dialog.size;
            bombs = dialog.bombs;
        } else
            return;

        double width = W * (size * 0.75 + 0.25);
        double height = H * (size + 0.5);

        board = new Board(size, size, bombs);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 0, 5, 10));
        hbox.setSpacing(20);
        hbox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Button restart = new Button("restart");
        status = new Text("");
        status.setFont(Font.font("", FontWeight.BOLD, 20));
        flagsText = new Text(Integer.toString(board.getFlags()));
        flagsText.setFont(new Font(20));
        hbox.getChildren().addAll(restart, flagsText, status);
        restart.setOnAction(e -> {
            gameOver = false;
            primaryStage.close();
            restart();
        });
        BorderPane root = new BorderPane();
        root.setTop(hbox);
        BorderPane.setMargin(hbox, new Insets(0, 0, 10, 0));
        Parent boardView = drawBoard(board);
        root.setCenter(boardView);

        Scene scene = new Scene(root, width, height);

        primaryStage.setScene(scene);
        primaryStage.setTitle("MineSweeper");
        primaryStage.setHeight(height + 90);
        primaryStage.setWidth(width + 20);
        primaryStage.setMaxHeight(height + 90);
        primaryStage.setMaxWidth(width + 20);
        primaryStage.show();
    }

    private Parent drawBoard(Board board) {
        Pane root = new Pane();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double dx;
                double dy;
                if (x % 2 == 0) {
                    dx = 1.5 * x * 0.5 * W;
                    dy = y * H;
                } else {
                    dx = 0.75 * W + (x - 1) * 0.5 * W * 1.5;
                    dy = 0.5 * H + y * H;
                }
                Hex hex = board.getGrid()[x][y];
                HexView cell = new HexView(hex, dx, dy);
                root.getChildren().add(cell);
            }
        }
        return root;
    }

    private void restart() {
        Stage stage = new Stage();
        start(stage);
    }


    private class HexView extends StackPane {
        private final Hex hex;
        private final Polygon border = new Polygon();
        private final Text text = new Text();

        HexView(Hex hex, double dx, double dy) {
            this.hex = hex;
            text.setFont(new Font(H / 2));

            allCells.add(this);

            border.getPoints().addAll(coords);
            border.setStroke(Color.BLACK);
            border.setStrokeWidth(3);
            border.setFill(Color.GREEN);

            getChildren().addAll(border, text);
            setTranslateX(dx);
            setTranslateY(dy);

            border.setOnMouseClicked(e -> {
                if (gameOver)
                    return;
                if (e.getButton() == MouseButton.PRIMARY) {
                    board.open(hex);
                    if (board.getLose()) {
                        gameOver = true;
                        status.setText(loseMessage);
                        status.setFill(Color.RED);
                    }
                    if (board.getVictory()) {
                        gameOver = true;
                        status.setText(winMessage);
                        status.setFill(Color.GREEN);
                    }
                } else {
                    board.flag(hex);
                }
                update();
            });
        }

        private void update() {
            flagsText.setText(Integer.toString(board.getFlags()));
            for (HexView cell : allCells) {
                if (cell.hex.isFlagged())
                    cell.border.setFill(Color.YELLOW);
                else
                    cell.border.setFill(Color.GREEN);
                if (cell.hex.isOpened() && cell.hex.isBomb())
                    cell.border.setFill(Color.RED);
                else if (cell.hex.isOpened()) {
                    cell.border.setStrokeWidth(2);
                    String text = cell.hex.getBombs() == 0 ? "" : Integer.toString(cell.hex.getBombs());
                    cell.text.setText(text);
                    cell.border.setFill(null);
                }
            }
        }
    }
}