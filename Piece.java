import javafx.scene.paint.Color;

import javafx.scene.canvas.GraphicsContext;

public class Piece {
    private Point location;
    private Color color;
    private Player myPlayer;

    public Piece(Point location, Color color, Player myPlayer) {
        this.location = location;
        this.color = color;
        this.myPlayer = myPlayer;
    }

    public Player getPlayer() {
        return this.myPlayer;
    }

    public void draw(GraphicsContext context) {
        context.setFill(color);
        int radius = 32;
        context.fillOval(location.getX() - 16, location.getY() - 16, radius, radius);
    }
}