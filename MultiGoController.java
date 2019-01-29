import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.canvas.Canvas;

public class MultiGoController {
    @FXML private Canvas gameCanvas;
    @FXML private ChoiceBox playerCountChooser;
    @FXML private ChoiceBox boardSizeChooser;
    @FXML private Label mainLabel;
    @FXML private Button startButton;
    @FXML private Button passButton;

    private GoBoard board;
    private GameManager manager;

    public void initialize() {


        // initialize listeners/objects

        playerCountChooser.getItems().add("2");
        playerCountChooser.getItems().add("3");
        playerCountChooser.getItems().add("4");

        playerCountChooser.setValue("2");

        boardSizeChooser.getItems().add("23");
        boardSizeChooser.getItems().add("21");
        boardSizeChooser.getItems().add("19");
        boardSizeChooser.getItems().add("17");
        boardSizeChooser.getItems().add("15");
        boardSizeChooser.getItems().add("13");
        boardSizeChooser.getItems().add("11");
        boardSizeChooser.getItems().add("9");

        boardSizeChooser.setValue("19");

        // initialize a quick empty game board so starting screen isn't empty
        // will be over-written by a new board when start is pressed
        int boardSize = 19;
        board = new GoBoard(gameCanvas, boardSize);
    }

    @FXML
    public void start(ActionEvent event) {
        board.clear();


        // find a board
        String str = (String) boardSizeChooser.getValue();
        int boardSize = Integer.parseInt(str);
        board = new GoBoard(gameCanvas, boardSize);
        // hire a referee
        String str1 = (String) playerCountChooser.getValue();
        int playerCount = Integer.parseInt(str1);
        manager = new GameManager(board, playerCount, mainLabel, passButton);
        // start the game
        manager.begin();
    }
}