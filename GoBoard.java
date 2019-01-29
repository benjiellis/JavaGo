import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import java.util.ArrayList;

public class GoBoard {
    private Point[][] cellLocations;
    private int boardSize;
    private Canvas gameCanvas;
    private GameManager manager;
    private EventHandler<MouseEvent> listener;

    public GoBoard(Canvas gameCanvas, int boardSize) {
        this.gameCanvas = gameCanvas;
        this.boardSize = boardSize;
        this.manager = null; // setManager needs to be called later
        this.listener = null;
        // initialize objects

        drawBoard();

        initializeGrid();
        // remove old listener
        if (this.listener != null) {
            gameCanvas.removeEventFilter(MouseEvent.MOUSE_CLICKED, this.listener);
        }
        // setup listener for mouse clicks on game board
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (manager != null) {

                    double width = gameCanvas.getWidth();
                    double height = gameCanvas.getHeight();

                    double adjustedX = (e.getX() / width) * boardSize;
                    double adjustedY = (e.getY() / height) * boardSize;

                    int xpos = (int) adjustedX; // needs to be correct cell locations
                    int ypos = (int) adjustedY; // needs to be correct cell locations
                    manager.sendMove(xpos, ypos);
                }
            }
        };
        gameCanvas.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
        this.listener = eventHandler;
        
    }

    public int getBoardSize() { return this.boardSize; }

    public void setManager(GameManager manager) { this.manager = manager; }

    private void drawBoard() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        double width = gameCanvas.getWidth();
        double height = gameCanvas.getHeight();

        gc.setLineWidth(2);

        // draw vertical lines
        for (int i = 0; i < boardSize; i++) {
            gc.strokeLine(width * (i / (double) (boardSize-1)), 0, width * (i / (double) (boardSize-1)), height);
        }
        // draw horizontal lines
        for (int i = 0; i < boardSize; i++) {
            gc.strokeLine(0, height * (i / (double) (boardSize-1)), width, height * (i / (double) (boardSize-1)));
        }
    }

    private void initializeGrid() {
        double width = gameCanvas.getWidth();
        double height = gameCanvas.getHeight();

        cellLocations = new Point[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Double xLoc = width * (i / (double) (boardSize-1));
                Double yLoc = height * (j / (double) (boardSize-1));
                cellLocations[i][j] = new Point(xLoc, yLoc);
                System.out.println(cellLocations[i][j].toString());
            }
        }
    }

    public void redraw() {
        // clear board
        clear();
        // drawlines
        drawBoard();
        // redraw pieces
        ArrayList<Piece> pieces = manager.getAllPieces();
        for (Piece piece : pieces) {
            piece.draw(gameCanvas.getGraphicsContext2D());
        }
    }

    public Point[][] getCellLocations() { return this.cellLocations; }

    public void clear() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }
}