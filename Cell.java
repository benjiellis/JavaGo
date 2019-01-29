import java.util.ArrayList;

public class Cell {
    private int x;
    private int y;
    private GameManager myGame;
    private Piece myPiece;

    public Cell(GameManager myGame, int x, int y) {
        this.myGame = myGame;
        this.x = x;
        this.y = y;
        myPiece = null;
    }

    public ArrayList<Cell> getNeighbors() {
        ArrayList<Cell> neighbors = new ArrayList<Cell>();

        if (getNorth() != null) { neighbors.add(getNorth()); }
        if (getWest() != null) { neighbors.add(getWest()); }
        if (getSouth() != null) { neighbors.add(getSouth()); }
        if (getEast() != null) { neighbors.add(getEast()); }

        return neighbors;
    }

    public Cell getNorth() {
        if (this.y == 0) {
            return null;
        }
        return myGame.getGrid()[x][y-1];
    }

    public Cell getEast() {
        if (this.x == myGame.getBoardSize()-1) {
            return null;
        }
        return myGame.getGrid()[x+1][y];
    }

    public Cell getSouth() {
        if (this.y == myGame.getBoardSize()-1) {
            return null;
        }
        return myGame.getGrid()[x][y+1];
    }

    public Cell getWest() {
        if (this.x == 0) {
            return null;
        }
        return myGame.getGrid()[x-1][y];
    }

    public void setPiece(Piece piece) {
        myPiece = piece;
    }

    public Player getPlayer() {
        if (myPiece == null) {
            return null;
        }
        return myPiece.getPlayer();
    }

    public void setPiece() {
        setPiece(null);
    }

    public Piece getPiece() { return this.myPiece; }

    public boolean hasPiece() {
        if (myPiece == null) {
            return false;
        }
        return true;
    }

    public ArrayList<Cell> getShape() {
        ArrayList<Cell> inShape = new ArrayList<Cell>();
        ArrayList<Cell> visited = new ArrayList<Cell>();
        
        visited.add(this);
       
        while (!visited.isEmpty()) {

            Cell current = visited.remove(0);
            inShape.add(current);

            Cell north = current.getNorth();
            Cell east = current.getEast();
            Cell south = current.getSouth();
            Cell west = current.getWest();

            if (north != null && north.getPlayer() == this.getPlayer() && !inShape.contains(north)) {
                visited.add(north);
            }
            if (east != null && east.getPlayer() == this.getPlayer() && !inShape.contains(east)) {
                visited.add(east);
            }
            if (south != null && south.getPlayer() == this.getPlayer() && !inShape.contains(south)) {
                visited.add(south);
            }
            if (west != null && west.getPlayer() == this.getPlayer() && !inShape.contains(west)) {
                visited.add(west);
            }
        }
        
        return inShape;
    }

    public boolean isCaptured() {
        if (myPiece == null) {
            return false;
        }

        boolean hasLiberty = false;

        ArrayList<Cell> toCheck = this.getShape();

        for (Cell piece : toCheck) {
            Cell north = piece.getNorth();
            Cell east = piece.getEast();
            Cell south = piece.getSouth();
            Cell west = piece.getWest();

            if (north != null && north.getPlayer() == null) {
                hasLiberty = true;
            }
            if (west != null && west.getPlayer() == null) {
                hasLiberty = true;
            }
            if (east != null && east.getPlayer() == null) {
                hasLiberty = true;
            }
            if (south != null && south.getPlayer() == null) {
                hasLiberty = true;
            }
        }

        return !hasLiberty;
    }



    
}