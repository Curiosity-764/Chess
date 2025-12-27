import java.util.ArrayList;
import java.util.List;

public class Knight extends ChessPiece {
    public Knight(boolean white) {
        super(PieceType.KNIGHT, white);
    }
    
    @Override
    public List<Position> getPossibleMoves(Position pos, ChessPiece[][] board) {
        List<Position> moves = new ArrayList<>();
        int[][] knightMoves = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        
        for (int[] move : knightMoves) {
            Position newPos = new Position(pos.row + move[0], pos.col + move[1]);
            if (isValidPosition(newPos, board.length)) {
                ChessPiece target = board[newPos.row][newPos.col];
                if (target == null || target.isWhite() != white) {
                    moves.add(newPos);
                }
            }
        }
        
        return moves;
    }
}