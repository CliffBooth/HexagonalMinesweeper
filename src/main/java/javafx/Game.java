package javafx;

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

    private Double[] coords = new Double[]{  //coordinates of a single hexagon
            W * 0.25, 0.0,
            W * 0.75, 0.0,
            W, H * 0.5,
            W * 0.75, H,
            W * 0.25, H,
            0.0, H * 0.5
    };

    private Text status;
    private int flags;
    private Text flagsText;

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
        flags = bombs;
        flagsText = new Text(Integer.toString(flags));
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

    public void restart() {
        Stage stage = new Stage();
        start(stage);
    }


    class HexView extends StackPane {
        Hex hex;
        Polygon border;
        Polygon lit;
        Text text = new Text();

        HexView(Hex hex, double dx, double dy) {
            this.hex = hex;

            allCells.add(this);
            border = new Polygon();

            border.getPoints().addAll(coords);
            border.setStroke(Color.BLACK);
            border.setStrokeWidth(2);

            lit = new Polygon();
            lit.getPoints().addAll(border.getPoints());
            lit.setFill(Color.GREEN);
            lit.setStroke(Color.BLACK);
            lit.setStrokeWidth(3);

            getChildren().addAll(border, text, lit);
            setTranslateX(dx);
            setTranslateY(dy);

            lit.setOnMouseClicked(e -> {
                if (gameOver)
                    return;
                if (e.getButton() == MouseButton.PRIMARY) {
                    if (board.isItFirstTurn()) {
                        board.open(hex);
                        for (HexView cell : allCells) {
                            if (cell.hex.isBomb()) {
                                cell.border.setFill(Color.RED);
                                cell.text.setVisible(false);
                            } else {
                                cell.text.setVisible(true);
                                cell.border.setFill(null);
                            }
                            String text = cell.hex.getBombs() == 0 ? "" : Integer.toString(cell.hex.getBombs());
                            cell.text.setText(text);
                            cell.text.setFont(new Font(H / 2));
                        }
                    }
                    board.open(hex);
                    if (board.getLose()) {
                        gameOver = true;
                        status.setText("YOU LOSE!");
                        status.setFill(Color.RED);
                    }
                    if (board.getVictory()) {
                        gameOver = true;
                        status.setText("YOU WIN!");
                        status.setFill(Color.GREEN);
                    }
                    update();
                } else {
                    if (!hex.isFlagged() && flags != 0) {
                        hex.flag();
                        flags--;
                        lit.setFill(Color.YELLOW);
                    } else if (hex.isFlagged()) {
                        hex.flag();
                        flags++;
                        lit.setFill(Color.GREEN);
                    }
                    flagsText.setText(Integer.toString(flags));
                }
            });
        }

        public void update() {
            for (HexView cell : allCells) {
                if (cell.hex.isOpened() & cell.getChildren().size() == 3)
                    cell.getChildren().remove(2);
            }
        }

    }
}