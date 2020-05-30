package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;


public class StartDialog extends Dialog<ButtonType> {
    int size = 7;
    int bombs = 7;

    {
        setTitle("Options");
        setHeaderText("Choose size of the board and number of bombs");
        GridPane content = new GridPane();
        content.setAlignment(Pos.CENTER);
        content.setHgap(30);
        content.setVgap(10);
        content.setPadding(new Insets(25, 25, 25, 25));

        ObservableList<String> s = FXCollections.observableArrayList("7X7", "11X11", "15X15");
        Map<String, ObservableList<Integer>> map = new HashMap<>();
        map.put(s.get(0), FXCollections.observableArrayList(7, 10, 12));
        map.put(s.get(1), FXCollections.observableArrayList(18, 24, 30));
        map.put(s.get(2), FXCollections.observableArrayList(33, 45, 56));

        ComboBox<String> sizeBox = new ComboBox<>(s);
        ComboBox<Integer> difficultyBox = new ComboBox<>(FXCollections.observableArrayList(map.get(s.get(0))));
        sizeBox.getSelectionModel().selectFirst();
        difficultyBox.getSelectionModel().selectFirst();

        sizeBox.setOnAction(e -> {
            String ss = sizeBox.getValue();
            size = Integer.parseInt(ss.split("X")[0]);
            difficultyBox.getItems().clear();
            difficultyBox.getItems().addAll(map.get(ss));
            difficultyBox.getSelectionModel().selectFirst();

        });
        difficultyBox.setOnAction(e -> {
            if (!difficultyBox.getItems().isEmpty())
                bombs = difficultyBox.getValue();
        });


        Text text1 = new Text("Size of the board:");
        text1.setFont(new Font(15));
        Text text2 = new Text("Amount of bombs:");
        text2.setFont(new Font(15));

        ButtonType ok = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().add(ok);
        getDialogPane().getButtonTypes().add(cancel);

        content.add(text1, 0, 1);
        content.add(text2, 1, 1);
        content.add(sizeBox, 0, 2);
        content.add(difficultyBox, 1, 2);
        getDialogPane().setContent(content);
    }
}
