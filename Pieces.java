public class Pieces {
    public static ChessPiece createPiece(PieceType type, boolean white) {
        switch (type) {
            case PAWN: return new Pawn(white);
            case KNIGHT: return new Knight(white);
            case BISHOP: return new Bishop(white);
            case ROOK: return new Rook(white);
            case QUEEN: return new Queen(white);
            case KING: return new King(white);
            default: throw new IllegalArgumentException("Unknown piece type: " + type);
        }
    }
}