import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

enum Player { ZERO, ONE, TWO, THREE};

public class GameManager {
    private int playerCount;
    private GoBoard board;
    private Cell[][] grid; // parallel matrix of locations on Canvas GUI
    private Player currentPlayer;
    private boolean isActive;
    private Map<Player, Color> playerColors;
    private ArrayList<Piece> allPieces;
    private int boardSize;
    private int passes;
    private Label mainLabel;
    private Map<Player, String> playerStrings;
    private Button passButton;
    private ArrayList<Cell> cellsInKo;

    public GameManager(GoBoard board, int playerCount, Label mainLabel, Button pass) {
        this.playerCount = playerCount;
        this.passButton = pass;
        this.mainLabel = mainLabel;
        this.board = board;
        board.setManager(this);
        boardSize = board.getBoardSize();
        currentPlayer = null;
        isActive = false;
        this.passes = 0;

        // the ko rule: a player cannot play in a spot if their piece had just be taken there
        cellsInKo = new ArrayList<Cell>();

        playerColors = new HashMap<Player, Color>();
        playerColors.put(Player.ZERO, Color.BLACK);
        playerColors.put(Player.ONE, Color.WHITE);
        playerColors.put(Player.TWO, Color.RED);
        playerColors.put(Player.THREE, Color.BLUE);

        playerStrings = new HashMap<Player, String>();
        playerStrings.put(Player.ZERO, "Black");
        playerStrings.put(Player.ONE, "White");
        playerStrings.put(Player.TWO, "Red");
        playerStrings.put(Player.THREE, "Blue");

        allPieces = new ArrayList<Piece>();

        grid = new Cell[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                grid[i][j] = new Cell(this, i, j);
            }
        }

        // set up listener on pass button
        passButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                sendMove(-1, -1); // send -1 to tell manager this turn is a pass
            }
        });
    }

    public int getBoardSize() { return this.boardSize; }

    public Cell[][] getGrid() { return this.grid; }

    public void sendMove(int x, int y) {
        // place piece of correct color at coord x, y

        // if manager active, try to place piece
        if (isActive) {

            if (x == -1 && y == -1) {// these values sent by goboard when player passes
                // check if enough passes to end game
                passes += 1;
                if (passes == playerCount) {
                    // end the game and count the winner
                    
                    Player winner = getWinner();

                    String update = playerStrings.get(winner) + " has won the game!";
                    System.out.println(update);
                    mainLabel.setText(update);

                    this.isActive = false;
                }
            } // if move pass
            else {

                passes = 0; // reset passes if the player did not pass

                Color color = playerColors.get(currentPlayer);
                Point location = board.getCellLocations()[x][y];
                Piece toPlace = new Piece(location, color, currentPlayer);

                // if space not empty, abort move
                if (grid[x][y].hasPiece()) {
                    return;
                }

                // ko check
                // the ko rule: a player cannot play in a spot if their piece had just be taken there
                // if ko applies, abort move
                if (cellsInKo.contains(grid[x][y])) {
                    return;
                }


                // place piece
                grid[x][y].setPiece(toPlace);

                // check to see if anything was captured
                Cell north = grid[x][y].getNorth();
                Cell east = grid[x][y].getEast();
                Cell south = grid[x][y].getSouth();
                Cell west = grid[x][y].getWest();

                if (north != null && north.hasPiece() && north.getPlayer() != grid[x][y].getPlayer()) {
                    if (north.isCaptured()) { 
                        cellsInKo = north.getShape();
                        removeShape(north); 
                    }
                }
                if (west != null && west.hasPiece() && west.getPlayer() != grid[x][y].getPlayer()) {
                    if (west.isCaptured()) { 
                        cellsInKo = west.getShape();
                        removeShape(west); 
                    }
                }
                if (south != null && south.hasPiece() && south.getPlayer() != grid[x][y].getPlayer()) {
                    if (south.isCaptured()) { 
                        cellsInKo = south.getShape();
                        removeShape(south); 
                    }
                }
                if (east != null && east.hasPiece() && east.getPlayer() != grid[x][y].getPlayer()) {
                    if (east.isCaptured()) {
                        cellsInKo = east.getShape();
                         removeShape(east); 
                        }
                }

                // check to see if suicide
                if (grid[x][y].isCaptured()) {
                    grid[x][y].setPiece();
                    return;
                }

                // add to catalog for quick UI drawing
                allPieces.add(toPlace);

                // redraw board
                board.redraw();

                // update label
                String update = "It's " + playerStrings.get(getNextPlayer()) + "\'s turn.";
                mainLabel.setText(update);

                // clear ko cells, passing should clear this too
                cellsInKo = new ArrayList<Cell>();
            } // end else for if send pass

            // advance player
            currentPlayer = getNextPlayer();
        }
    }

    public ArrayList<Piece> getAllPieces() { return this.allPieces; }

    private void removeShape(Cell rep) {
        ArrayList<Cell> shape = rep.getShape();

        for (Cell cell : shape) {
            allPieces.remove(cell.getPiece());
            cell.setPiece();
        }
    }

    public Player getWinner() {
        Map<Player, Integer> scores = new HashMap<Player, Integer>();
        scores.put(Player.ZERO, 0);
        scores.put(Player.ONE, 0);
        scores.put(Player.TWO, 0);
        scores.put(Player.THREE, 0);

        // populate scores map
        Set<Cell> checkedCells = new HashSet<Cell>();
        for (Cell[] row : this.grid) {
            for (Cell current : row) {

                if (checkedCells.contains(current)) {continue;}

                Player scorer = current.getPlayer();
                if (scorer == null) { scorer = isControlledBy(current); }

                if (scorer != null) {
                    ArrayList<Cell> shape = current.getShape();
                    checkedCells.addAll(shape);
                    int updated = scores.get(scorer) + shape.size();
                    scores.replace(scorer, updated);
                }
            }
        }

        // return maximum
        Map.Entry<Player, Integer> max = null;
        for (Map.Entry<Player, Integer> entry : scores.entrySet()) {
            if (max == null || max.getValue() < entry.getValue()) {
                max = entry;
            }
        }

        System.out.println(playerStrings.get(max.getKey()));

        return max.getKey();
    }


    private Player isControlledBy(Cell rep) {

        if (rep.hasPiece()) { return null; } // return nothing if not open spot
        
        ArrayList<Cell> shape = rep.getShape();
        Set<Player> controller = new HashSet<Player>();

        for (Cell cell : shape) {
            ArrayList<Cell> neighbors = cell.getNeighbors();

            for (Cell neighbor : neighbors) {
                if (neighbor.hasPiece()) {
                    controller.add(neighbor.getPlayer());
                }
            }
        }

        if (controller.size() == 1) { // if only surrounded by one player
            Player toReturn = null;
            for (Player player : controller) {
                toReturn = player;
            }
            return toReturn;
        }
        else {
            return null;
        }
    }


    private Player getNextPlayer() {
        Player next = null;
        if (currentPlayer == Player.THREE) {
            next = Player.ZERO;
        }
        if (currentPlayer == Player.TWO) {
            if (playerCount == 3) {
                next = Player.ZERO;
            }
            else { next = Player.THREE; }
        }
        if (currentPlayer == Player.ONE) {
            if (playerCount == 2) {
                next = Player.ZERO;
            }
            else {
                next = Player.TWO;
            }
        }
        if (currentPlayer == Player.ZERO) {
            next = Player.ONE;
        }
        return next;

    }
    public void begin() {
        isActive = true;
        currentPlayer = Player.ZERO;
    }
}