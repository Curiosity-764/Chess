import java.util.ArrayList;
import java.util.List;

public class Pawn extends ChessPiece {
    
    public Pawn(boolean white) {
        super(PieceType.PAWN, white);
    }
    
    @Override
    public List<Position> getPossibleMoves(Position pos, ChessPiece[][] board) {
        List<Position> moves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int startRow = isWhite() ? 6 : 1;   
        
        
        Position forwardOne = new Position(pos.row + direction, pos.col);
        if (isValidPosition(forwardOne, board.length) && 
            board[forwardOne.row][forwardOne.col] == null) {
            moves.add(forwardOne);
            
            if (pos.row == startRow) {
                Position forwardTwo = new Position(pos.row + (2 * direction), pos.col);
                if (board[forwardTwo.row][forwardTwo.col] == null) {
                    moves.add(forwardTwo);
                }
            }
        }
        

        if (pos.col > 0) {
            Position diagLeft = new Position(pos.row + direction, pos.col - 1);
            if (isValidPosition(diagLeft, board.length)) {
                ChessPiece target = board[diagLeft.row][diagLeft.col];
                if (target != null && target.isWhite() != isWhite()) {
                    moves.add(diagLeft);
                }
            }
        }
    
        if (pos.col < board.length - 1) {
            Position diagRight = new Position(pos.row + direction, pos.col + 1);
            if (isValidPosition(diagRight, board.length)) {
                ChessPiece target = board[diagRight.row][diagRight.col];
                if (target != null && target.isWhite() != isWhite()) {
                    moves.add(diagRight);
                }
            }
        }
        return moves;
    }
    
    protected boolean isValidPosition(Position pos, int boardSize) {
        return pos.row >= 0 && pos.row < boardSize && pos.col >= 0 && pos.col < boardSize;
    }
}