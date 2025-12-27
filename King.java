import java.util.ArrayList;
import java.util.List;

public class King extends ChessPiece {
    public King(boolean white) {
        super(PieceType.KING, white);
    }
    
    @Override
    public List<Position> getPossibleMoves(Position pos, ChessPiece[][] board) {
        List<Position> moves = new ArrayList<>();
        int[][] kingMoves = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},          {0, 1},
            {1, -1}, {1, 0}, {1, 1}
        };
        
        for (int[] move : kingMoves) {
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