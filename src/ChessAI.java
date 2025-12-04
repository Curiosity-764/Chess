// ChessAI.java
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChessAI {
    private Random random;
    
    public ChessAI() {
        this.random = new Random();
    }
    
    public void makeMove(ChessPiece[][] board, boolean isWhite) {
        List<Move> possibleMoves = getAllPossibleMoves(board, isWhite);
        
        if (possibleMoves.isEmpty()) {
            return; // No moves available (checkmate or stalemate)
        }
        
        // Simple AI: prioritize captures, then random moves
        List<Move> capturingMoves = new ArrayList<>();
        List<Move> nonCapturingMoves = new ArrayList<>();
        
        for (Move move : possibleMoves) {
            if (board[move.to.row][move.to.col] != null) {
                capturingMoves.add(move);
            } else {
                nonCapturingMoves.add(move);
            }
        }
        
        Move selectedMove;
        if (!capturingMoves.isEmpty()) {
            // Prefer capturing moves
            selectedMove = capturingMoves.get(random.nextInt(capturingMoves.size()));
        } else {
            // Use random move
            selectedMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
        }
        
        // Execute the move
        board[selectedMove.to.row][selectedMove.to.col] = board[selectedMove.from.row][selectedMove.from.col];
        board[selectedMove.from.row][selectedMove.from.col] = null;
        
        // Handle pawn promotion for AI
        if (board[selectedMove.to.row][selectedMove.to.col].getType() == PieceType.PAWN && 
            (selectedMove.to.row == 0 || selectedMove.to.row == 7)) {
            board[selectedMove.to.row][selectedMove.to.col] = new ChessPiece(PieceType.QUEEN, isWhite);
        }
    }
    
    private List<Move> getAllPossibleMoves(ChessPiece[][] board, boolean isWhite) {
        List<Move> allMoves = new ArrayList<>();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Position> moves = getPseudoLegalMovesForPiece(board, new Position(row, col));
                    for (Position move : moves) {
                        allMoves.add(new Move(new Position(row, col), move));
                    }
                }
            }
        }
        
        return allMoves;
    }
    
    private List<Position> getPseudoLegalMovesForPiece(ChessPiece[][] board, Position pos) {
        List<Position> moves = new ArrayList<>();
        ChessPiece piece = board[pos.row][pos.col];
        
        if (piece == null) return moves;

        switch (piece.getType()) {
            case PAWN:
                addPawnMoves(board, pos, moves, piece.isWhite());
                break;
            case ROOK:
                addRookMoves(board, pos, moves);
                break;
            case KNIGHT:
                addKnightMoves(board, pos, moves);
                break;
            case BISHOP:
                addBishopMoves(board, pos, moves);
                break;
            case QUEEN:
                addRookMoves(board, pos, moves);
                addBishopMoves(board, pos, moves);
                break;
            case KING:
                addKingMoves(board, pos, moves);
                break;
        }
        
        return moves;
    }
    
    private void addPawnMoves(ChessPiece[][] board, Position pos, List<Position> moves, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;
        
        // Move forward one square
        Position forwardOne = new Position(pos.row + direction, pos.col);
        if (isValidPosition(forwardOne) && board[forwardOne.row][forwardOne.col] == null) {
            moves.add(forwardOne);
            
            // Move forward two squares from starting position
            if (pos.row == startRow) {
                Position forwardTwo = new Position(pos.row + 2 * direction, pos.col);
                if (isValidPosition(forwardTwo) && board[forwardTwo.row][forwardTwo.col] == null) {
                    moves.add(forwardTwo);
                }
            }
        }
        
        // Capture diagonally
        int[] captureCols = {pos.col - 1, pos.col + 1};
        for (int captureCol : captureCols) {
            Position capturePos = new Position(pos.row + direction, captureCol);
            if (isValidPosition(capturePos)) {
                ChessPiece target = board[capturePos.row][capturePos.col];
                if (target != null && target.isWhite() != isWhite) {
                    moves.add(capturePos);
                }
            }
        }
    }
    
    private void addRookMoves(ChessPiece[][] board, Position pos, List<Position> moves) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                Position newPos = new Position(pos.row + i * dir[0], pos.col + i * dir[1]);
                if (!isValidPosition(newPos)) break;
                
                ChessPiece target = board[newPos.row][newPos.col];
                if (target == null) {
                    moves.add(newPos);
                } else {
                    if (target.isWhite() != board[pos.row][pos.col].isWhite()) {
                        moves.add(newPos);
                    }
                    break;
                }
            }
        }
    }
    
    private void addKnightMoves(ChessPiece[][] board, Position pos, List<Position> moves) {
        int[][] knightMoves = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        
        for (int[] move : knightMoves) {
            Position newPos = new Position(pos.row + move[0], pos.col + move[1]);
            if (isValidPosition(newPos)) {
                ChessPiece target = board[newPos.row][newPos.col];
                if (target == null || target.isWhite() != board[pos.row][pos.col].isWhite()) {
                    moves.add(newPos);
                }
            }
        }
    }
    
    private void addBishopMoves(ChessPiece[][] board, Position pos, List<Position> moves) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        
        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                Position newPos = new Position(pos.row + i * dir[0], pos.col + i * dir[1]);
                if (!isValidPosition(newPos)) break;
                
                ChessPiece target = board[newPos.row][newPos.col];
                if (target == null) {
                    moves.add(newPos);
                } else {
                    if (target.isWhite() != board[pos.row][pos.col].isWhite()) {
                        moves.add(newPos);
                    }
                    break;
                }
            }
        }
    }
    
    private void addKingMoves(ChessPiece[][] board, Position pos, List<Position> moves) {
        int[][] kingMoves = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        for (int[] move : kingMoves) {
            Position newPos = new Position(pos.row + move[0], pos.col + move[1]);
            if (isValidPosition(newPos)) {
                ChessPiece target = board[newPos.row][newPos.col];
                if (target == null || target.isWhite() != board[pos.row][pos.col].isWhite()) {
                    moves.add(newPos);
                }
            }
        }
    }
    
    private boolean isValidPosition(Position pos) {
        return pos.row >= 0 && pos.row < 8 && pos.col >= 0 && pos.col < 8;
    }
    
    private class Move {
        Position from;
        Position to;
        
        Move(Position from, Position to) {
            this.from = from;
            this.to = to;
        }
    }
}