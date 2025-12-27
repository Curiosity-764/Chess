// ChessAI.java - Pure Alpha-Beta Pruning
import java.util.*;

public class ChessAI {
    private boolean isWhite;
    private int maxDepth;
    
    // Piece values (same as the minimax code you found)
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000;
    
    // Position tables (from the code you found)
    private static final int[][] PAWN_POSITION = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {10, 10, 20, 30, 30, 20, 10, 10},
        {5, 5, 10, 25, 25, 10, 5, 5},
        {0, 0, 0, 20, 20, 0, 0, 0},
        {5, -5, -10, 0, 0, -10, -5, 5},
        {5, 10, 10, -20, -20, 10, 10, 5},
        {0, 0, 0, 0, 0, 0, 0, 0}
    };
    
    private static final int[][] KNIGHT_POSITION = {
        {-50, -40, -30, -30, -30, -30, -40, -50},
        {-40, -20, 0, 0, 0, 0, -20, -40},
        {-30, 0, 10, 15, 15, 10, 0, -30},
        {-30, 5, 15, 20, 20, 15, 5, -30},
        {-30, 0, 15, 20, 20, 15, 0, -30},
        {-30, 5, 10, 15, 15, 10, 5, -30},
        {-40, -20, 0, 5, 5, 0, -20, -40},
        {-50, -40, -30, -30, -30, -30, -40, -50}
    };
    
    public ChessAI(boolean isWhite) {
        this.isWhite = isWhite;
        this.maxDepth = 3; 
    }
    
    public Move getBestMove(ChessBoard board) {
        return alphaBetaRoot(board, maxDepth);
    }
    
    private Move alphaBetaRoot(ChessBoard board, int depth) {
        List<Move> allMoves = getAllPossibleMoves(board, isWhite);
        
        if (allMoves.isEmpty()) return null;
        
        Move bestMove = allMoves.get(0);
        int bestValue = Integer.MIN_VALUE;
        
        for (Move move : allMoves) {
            ChessPiece[][] newBoard = simulateMove(board.getBoardState(), move);
            
            int moveValue = alphaBeta(newBoard, depth - 1, Integer.MIN_VALUE, 
                                      Integer.MAX_VALUE, false, board);
            
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }
        
        return bestMove;
    }
    
    private int alphaBeta(ChessPiece[][] board, int depth, int alpha, int beta, 
                          boolean maximizingPlayer, ChessBoard originalBoard) {
        if (depth == 0) {
            return evaluateBoard(board);
        }
        
        boolean currentPlayer = maximizingPlayer ? isWhite : !isWhite;
        List<Move> moves = getAllPossibleMoves(board, currentPlayer, originalBoard);
        
        if (moves.isEmpty()) {
            if (isKingInCheck(board, currentPlayer, originalBoard)) {
                return maximizingPlayer ? Integer.MIN_VALUE + 1000 : Integer.MAX_VALUE - 1000;
            }
            return 0; 
        }
        
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            
            for (Move move : moves) {
                ChessPiece[][] newBoard = simulateMove(board, move);
                int eval = alphaBeta(newBoard, depth - 1, alpha, beta, false, originalBoard);
                
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                
                if (beta <= alpha) {
                    break; 
                }
            }
            
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            
            for (Move move : moves) {
                ChessPiece[][] newBoard = simulateMove(board, move);
                int eval = alphaBeta(newBoard, depth - 1, alpha, beta, true, originalBoard);
                
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                
                if (beta <= alpha) {
                    break;
                }
            }
            
            return minEval;
        }
    }
    
    private int evaluateBoard(ChessPiece[][] board) {
        int score = 0;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null) {
                    int pieceValue = getPieceValue(piece);
                    int positionBonus = getPositionBonus(piece, row, col);
                    int totalValue = pieceValue + positionBonus;
                    
                    if (piece.isWhite() == isWhite) {
                        score += totalValue;
                    } else {
                        score -= totalValue;
                    }
                }
            }
        }
        
        return score;
    }
    
    private int getPieceValue(ChessPiece piece) {
        switch (piece.getType()) {
            case PAWN: return PAWN_VALUE;
            case KNIGHT: return KNIGHT_VALUE;
            case BISHOP: return BISHOP_VALUE;
            case ROOK: return ROOK_VALUE;
            case QUEEN: return QUEEN_VALUE;
            case KING: return KING_VALUE;
            default: return 0;
        }
    }
    
    private int getPositionBonus(ChessPiece piece, int row, int col) {
        int adjustedRow = piece.isWhite() ? 7 - row : row;
        
        switch (piece.getType()) {
            case PAWN: return PAWN_POSITION[adjustedRow][col];
            case KNIGHT: return KNIGHT_POSITION[adjustedRow][col];
            // Can add more position tables here...
            default: return 0;
        }
    }
    
    private boolean isKingInCheck(ChessPiece[][] board, boolean forWhite, ChessBoard originalBoard) {
        Position kingPos = null;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null && piece.getType() == PieceType.KING && 
                    piece.isWhite() == forWhite) {
                    kingPos = new Position(row, col);
                    break;
                }
            }
            if (kingPos != null) break;
        }
        
        if (kingPos == null) return false;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null && piece.isWhite() != forWhite) {
                    List<Position> attacks = piece.getPossibleMoves(new Position(row, col), board);
                    for (Position attack : attacks) {
                        if (attack.equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    private ChessPiece[][] simulateMove(ChessPiece[][] board, Move move) {
        ChessPiece[][] newBoard = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, 8);
        }
        
        newBoard[move.to.row][move.to.col] = newBoard[move.from.row][move.from.col];
        newBoard[move.from.row][move.from.col] = null;
        return newBoard;
    }
    
    private List<Move> getAllPossibleMoves(ChessPiece[][] board, boolean forWhite, 
                                           ChessBoard originalBoard) {
        List<Move> moves = new ArrayList<>();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null && piece.isWhite() == forWhite) {
                    Position from = new Position(row, col);
                    List<Position> possibleMoves = piece.getPossibleMoves(from, board);
                    
                    for (Position to : possibleMoves) {
                        // Simplified - in real alpha-beta, need proper check checking
                        moves.add(new Move(from, to));
                    }
                }
            }
        }
        
        return moves;
    }
    
    private List<Move> getAllPossibleMoves(ChessBoard board, boolean forWhite) {
        List<Move> allMoves = new ArrayList<>();
        ChessPiece[][] boardState = board.getBoardState();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = boardState[row][col];
                if (piece != null && piece.isWhite() == forWhite) {
                    Position from = new Position(row, col);
                    List<Position> possibleMoves = piece.getPossibleMoves(from, boardState);
                    
                    for (Position to : possibleMoves) {
                        if (!board.leavesKingInCheck(from, to, forWhite)) {
                            allMoves.add(new Move(from, to));
                        }
                    }
                }
            }
        }
        return allMoves;
    }
    
    public static class Move {
        public Position from;
        public Position to;
        
        public Move(Position from, Position to) {
            this.from = from;
            this.to = to;
        }
    }
}