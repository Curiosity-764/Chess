import java.util.ArrayList;
import java.util.List;

public class Bishop extends ChessPiece {
    public Bishop(boolean white) {
        super(PieceType.BISHOP, white);
    }
    
    @Override
    public List<Position> getPossibleMoves(Position pos, ChessPiece[][] board) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        addLinearMoves(pos, moves, board, directions, board.length - 1);
        return moves;
    }
}