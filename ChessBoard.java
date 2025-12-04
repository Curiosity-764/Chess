// ChessBoard.java (COMPLETE VERSION for PNG images)
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ChessBoard extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 80;
    
    private ChessPiece[][] board;
    private ChessGame parent;
    private Position selectedPosition;
    private List<Position> possibleMoves;
    private boolean whiteTurn;
    private boolean gameActive;
    
    // Image cache for pieces
    private Image[][] pieceImages;

    public ChessBoard(ChessGame parent) {
        this.parent = parent;
        this.board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
        this.possibleMoves = new ArrayList<>();
        this.whiteTurn = true;
        this.gameActive = true;
        this.pieceImages = new Image[2][6]; // [color][pieceType]
        
        setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE));
        loadPieceImages();
        initializeBoard();
        setupMouseListener();
    }

    private void loadPieceImages() {
        try {
            // Use getResource for proper path handling in Eclipse
            pieceImages[0][0] = new ImageIcon(getClass().getResource("/pieces/wp.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[0][1] = new ImageIcon(getClass().getResource("/pieces/wn.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[0][2] = new ImageIcon(getClass().getResource("/pieces/wb.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[0][3] = new ImageIcon(getClass().getResource("/pieces/wr.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[0][4] = new ImageIcon(getClass().getResource("/pieces/wq.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[0][5] = new ImageIcon(getClass().getResource("/pieces/wk.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            
            pieceImages[1][0] = new ImageIcon(getClass().getResource("/pieces/bp.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[1][1] = new ImageIcon(getClass().getResource("/pieces/bn.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[1][2] = new ImageIcon(getClass().getResource("/pieces/bb.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[1][3] = new ImageIcon(getClass().getResource("/pieces/br.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[1][4] = new ImageIcon(getClass().getResource("/pieces/bq.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
            pieceImages[1][5] = new ImageIcon(getClass().getResource("/pieces/bk.png")).getImage().getScaledInstance(TILE_SIZE-10, TILE_SIZE-10, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Error loading piece images: " + e.getMessage());
            System.err.println("Make sure the pieces folder is in your classpath");
        }
    }
    private Image getPieceImage(ChessPiece piece) {
        if (piece == null) return null;
        
        int colorIndex = piece.isWhite() ? 0 : 1;
        int pieceIndex = -1;
        
        switch (piece.getType()) {
            case PAWN: pieceIndex = 0; break;
            case KNIGHT: pieceIndex = 1; break;
            case BISHOP: pieceIndex = 2; break;
            case ROOK: pieceIndex = 3; break;
            case QUEEN: pieceIndex = 4; break;
            case KING: pieceIndex = 5; break;
        }
        
        return pieceImages[colorIndex][pieceIndex];
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameActive) return;
                
                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;
                
                if (col >= 0 && col < BOARD_SIZE && row >= 0 && row < BOARD_SIZE) {
                    handleSquareClick(new Position(row, col));
                }
            }
        });
    }

    private void handleSquareClick(Position pos) {
        if (!parent.isPlayerVsPlayer() && !whiteTurn) {
            return;
        }

        ChessPiece clickedPiece = board[pos.row][pos.col];
        
        if (selectedPosition != null) {
            if (isPossibleMove(pos)) {
                movePiece(selectedPosition, pos);
                selectedPosition = null;
                possibleMoves.clear();
                
    
            } else if (clickedPiece != null && clickedPiece.isWhite() == whiteTurn) {
                selectedPosition = pos;
                possibleMoves = getPossibleMoves(pos);
            } else {
                selectedPosition = null;
                possibleMoves.clear();
            }
        } else if (clickedPiece != null && clickedPiece.isWhite() == whiteTurn) {
            selectedPosition = pos;
            possibleMoves = getPossibleMoves(pos);
        }
        
        repaint();
    }

    private void movePiece(Position from, Position to) {
        ChessPiece movingPiece = board[from.row][from.col];
        board[to.row][to.col] = movingPiece;
        board[from.row][from.col] = null;
        
        if (movingPiece.getType() == PieceType.PAWN && 
            (to.row == 0 || to.row == BOARD_SIZE - 1)) {
            board[to.row][to.col] = new ChessPiece(PieceType.QUEEN, movingPiece.isWhite());
        }
        
        whiteTurn = !whiteTurn;
        parent.updateStatus(whiteTurn ? "White's turn" : "Black's turn");
        
        checkGameState();
    }

    private void checkGameState() {
        boolean hasLegalMoves = false;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null && piece.isWhite() == whiteTurn) {
                    List<Position> moves = getPossibleMoves(new Position(row, col));
                    if (!moves.isEmpty()) {
                        hasLegalMoves = true;
                        break;
                    }
                }
            }
            if (hasLegalMoves) break;
        }
        
        if (!hasLegalMoves) {
            gameActive = false;
            if (isInCheck(whiteTurn)) {
                parent.updateStatus("Checkmate! " + (whiteTurn ? "Black" : "White") + " wins!");
            } else {
                parent.updateStatus("Stalemate!");
            }
        } else if (isInCheck(whiteTurn)) {
            parent.updateStatus("Check! " + (whiteTurn ? "White" : "Black") + " is in check!");
        }
    }

    private List<Position> getPossibleMoves(Position pos) {
        List<Position> moves = new ArrayList<>();
        ChessPiece piece = board[pos.row][pos.col];
        
        if (piece == null) return moves;

        switch (piece.getType()) {
            case PAWN:
                addPawnMoves(pos, moves, piece.isWhite());
                break;
            case ROOK:
                addRookMoves(pos, moves);
                break;
            case KNIGHT:
                addKnightMoves(pos, moves);
                break;
            case BISHOP:
                addBishopMoves(pos, moves);
                break;
            case QUEEN:
                addRookMoves(pos, moves);
                addBishopMoves(pos, moves);
                break;
            case KING:
                addKingMoves(pos, moves);
                break;
        }

        moves.removeIf(move -> wouldBeInCheckAfterMove(pos, move));
        
        return moves;
    }

    private boolean wouldBeInCheckAfterMove(Position from, Position to) {
        ChessPiece temp = board[to.row][to.col];
        board[to.row][to.col] = board[from.row][from.col];
        board[from.row][from.col] = null;
        
        boolean inCheck = isInCheck(whiteTurn);
        
        board[from.row][from.col] = board[to.row][to.col];
        board[to.row][to.col] = temp;
        
        return inCheck;
    }

    private boolean isInCheck(boolean forWhite) {
        Position kingPos = findKing(forWhite);
        if (kingPos == null) return false;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null && piece.isWhite() != forWhite) {
                    List<Position> moves = getPseudoLegalMoves(new Position(row, col));
                    for (Position move : moves) {
                        if (move.equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private List<Position> getPseudoLegalMoves(Position pos) {
        List<Position> moves = new ArrayList<>();
        ChessPiece piece = board[pos.row][pos.col];
        
        if (piece == null) return moves;

        switch (piece.getType()) {
            case PAWN:
                addPawnMoves(pos, moves, piece.isWhite());
                break;
            case ROOK:
                addRookMoves(pos, moves);
                break;
            case KNIGHT:
                addKnightMoves(pos, moves);
                break;
            case BISHOP:
                addBishopMoves(pos, moves);
                break;
            case QUEEN:
                addRookMoves(pos, moves);
                addBishopMoves(pos, moves);
                break;
            case KING:
                addKingMoves(pos, moves);
                break;
        }
        
        return moves;
    }

    private Position findKing(boolean white) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null && piece.getType() == PieceType.KING && piece.isWhite() == white) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    private void addPawnMoves(Position pos, List<Position> moves, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;
        
        Position forwardOne = new Position(pos.row + direction, pos.col);
        if (isValidPosition(forwardOne) && board[forwardOne.row][forwardOne.col] == null) {
            moves.add(forwardOne);
            
            if (pos.row == startRow) {
                Position forwardTwo = new Position(pos.row + 2 * direction, pos.col);
                if (isValidPosition(forwardTwo) && board[forwardTwo.row][forwardTwo.col] == null) {
                    moves.add(forwardTwo);
                }
            }
        }
        
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

    private void addRookMoves(Position pos, List<Position> moves) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int[] dir : directions) {
            for (int i = 1; i < BOARD_SIZE; i++) {
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

    private void addKnightMoves(Position pos, List<Position> moves) {
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

    private void addBishopMoves(Position pos, List<Position> moves) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        
        for (int[] dir : directions) {
            for (int i = 1; i < BOARD_SIZE; i++) {
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

    private void addKingMoves(Position pos, List<Position> moves) {
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
        return pos.row >= 0 && pos.row < BOARD_SIZE && pos.col >= 0 && pos.col < BOARD_SIZE;
    }

    private boolean isPossibleMove(Position pos) {
        return possibleMoves.stream().anyMatch(p -> p.equals(pos));
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = null;
            }
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            board[1][i] = new ChessPiece(PieceType.PAWN, false);
            board[6][i] = new ChessPiece(PieceType.PAWN, true);
        }

        board[0][0] = new ChessPiece(PieceType.ROOK, false);
        board[0][1] = new ChessPiece(PieceType.KNIGHT, false);
        board[0][2] = new ChessPiece(PieceType.BISHOP, false);
        board[0][3] = new ChessPiece(PieceType.QUEEN, false);
        board[0][4] = new ChessPiece(PieceType.KING, false);
        board[0][5] = new ChessPiece(PieceType.BISHOP, false);
        board[0][6] = new ChessPiece(PieceType.KNIGHT, false);
        board[0][7] = new ChessPiece(PieceType.ROOK, false);

        board[7][0] = new ChessPiece(PieceType.ROOK, true);
        board[7][1] = new ChessPiece(PieceType.KNIGHT, true);
        board[7][2] = new ChessPiece(PieceType.BISHOP, true);
        board[7][3] = new ChessPiece(PieceType.QUEEN, true);
        board[7][4] = new ChessPiece(PieceType.KING, true);
        board[7][5] = new ChessPiece(PieceType.BISHOP, true);
        board[7][6] = new ChessPiece(PieceType.KNIGHT, true);
        board[7][7] = new ChessPiece(PieceType.ROOK, true);
    }

    public void newGame() {
        initializeBoard();
        selectedPosition = null;
        possibleMoves.clear();
        whiteTurn = true;
        gameActive = true;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(new Color(240, 217, 181));
                } else {
                    g.setColor(new Color(181, 136, 99));
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                
                if (selectedPosition != null && selectedPosition.row == row && selectedPosition.col == col) {
                    g.setColor(new Color(255, 255, 0, 100));
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
                
                for (Position move : possibleMoves) {
                    if (move.row == row && move.col == col) {
                        g.setColor(new Color(0, 255, 0, 100));
                        g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
                
                ChessPiece piece = board[row][col];
                if (piece != null) {
                    drawPiece(g, piece, col * TILE_SIZE, row * TILE_SIZE);
                }
            }
        }
    }

    private void drawPiece(Graphics g, ChessPiece piece, int x, int y) {
        Image pieceImage = getPieceImage(piece);
        if (pieceImage != null) {
            int imgX = x + (TILE_SIZE - pieceImage.getWidth(null)) / 2;
            int imgY = y + (TILE_SIZE - pieceImage.getHeight(null)) / 2;
            g.drawImage(pieceImage, imgX, imgY, this);
        } else {
            String pieceSymbol = getPieceSymbol(piece);
            g.setColor(piece.isWhite() ? Color.WHITE : Color.BLACK);
            g.setFont(new Font("Serif", Font.BOLD, 48));
            
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (TILE_SIZE - fm.stringWidth(pieceSymbol)) / 2;
            int textY = y + ((TILE_SIZE - fm.getHeight()) / 2) + fm.getAscent();
            
            g.drawString(pieceSymbol, textX, textY);
        }
    }

    private String getPieceSymbol(ChessPiece piece) {
        switch (piece.getType()) {
            case KING: return piece.isWhite() ? "♔" : "♚";
            case QUEEN: return piece.isWhite() ? "♕" : "♛";
            case ROOK: return piece.isWhite() ? "♖" : "♜";
            case BISHOP: return piece.isWhite() ? "♗" : "♝";
            case KNIGHT: return piece.isWhite() ? "♘" : "♞";
            case PAWN: return piece.isWhite() ? "♙" : "♟";
            default: return "";
        }
    }

    public ChessPiece[][] getBoardState() {
        return board;
    }

    public void setBoardState(ChessPiece[][] newBoard) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(newBoard[i], 0, board[i], 0, BOARD_SIZE);
        }
        repaint();
    }
}