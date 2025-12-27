import java.util.List;

public abstract class ChessPiece {
    protected boolean white;
    protected PieceType type;
    
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
    
    public abstract List<Position> getPossibleMoves(Position pos, ChessPiece[][] board);
    
    protected boolean isValidPosition(Position pos, int boardSize) {
        return pos.row >= 0 && pos.row < boardSize && 
               pos.col >= 0 && pos.col < boardSize;
    }
// movement cua cac quan    
    protected void addLinearMoves(Position pos, List<Position> moves, ChessPiece[][] board, 
                                 int[][] directions, int maxSteps) {
        for (int[] dir : directions) {
            for (int i = 1; i <= maxSteps; i++) {
                Position newPos = new Position(pos.row + i * dir[0], pos.col + i * dir[1]);
                if (!isValidPosition(newPos, board.length)) break;
                
                ChessPiece target = board[newPos.row][newPos.col];
                if (target == null) {
                    moves.add(newPos);
                } else {
                    if (target.isWhite() != this.white) {
                        moves.add(newPos);
                    }
                    break;
                }
            }
        }
    }
}