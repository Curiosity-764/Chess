import java.util.*;

public class Rook extends ChessPiece {
    public Rook(boolean white) {
        super(PieceType.ROOK, white);
    }
    
    @Override
    public List<Position> getPossibleMoves(Position pos, ChessPiece[][] board) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        addLinearMoves(pos, moves, board, directions, board.length - 1);
        return moves;
    }
}