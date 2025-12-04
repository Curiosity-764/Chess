// ChessPiece.java
public class ChessPiece {
    private PieceType type;
    private boolean white;

    public ChessPiece(PieceType type, boolean white) {
        this.type = type;
        this.white = white;
    }

    public PieceType getType() {
        return type;
    }

    public boolean isWhite() {
        return white;
    }
}
