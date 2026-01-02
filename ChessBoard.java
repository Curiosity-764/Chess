import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private Image[][] pieceImages;

	private Position enPassantTarget = null;
	private boolean whiteKingsideCastle = true;
	private boolean whiteQueensideCastle = true;
	private boolean blackKingsideCastle = true;
	private boolean blackQueensideCastle = true;
	private ChessAI chessAI;

	
	private Position whiteKingPos = new Position(7, 4);
	private Position blackKingPos = new Position(0, 4);
	public ChessBoard(ChessGame parent) {
		this.parent = parent;
		this.board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
		this.possibleMoves = new ArrayList<>();
		this.whiteTurn = true;
		this.gameActive = true;
		this.pieceImages = new Image[2][6];
		this.chessAI = new ChessAI(false); 
		setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE));
		loadPieceImages();
		initializeBoard();
		setupMouseListener();
	}

	private void loadPieceImages() {
		try {
			pieceImages[0][0] = new ImageIcon(getClass().getResource("/pieces/wp.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[0][1] = new ImageIcon(getClass().getResource("/pieces/wn.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[0][2] = new ImageIcon(getClass().getResource("/pieces/wb.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[0][3] = new ImageIcon(getClass().getResource("/pieces/wr.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[0][4] = new ImageIcon(getClass().getResource("/pieces/wq.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[0][5] = new ImageIcon(getClass().getResource("/pieces/wk.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);

			pieceImages[1][0] = new ImageIcon(getClass().getResource("/pieces/bp.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[1][1] = new ImageIcon(getClass().getResource("/pieces/bn.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[1][2] = new ImageIcon(getClass().getResource("/pieces/bb.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[1][3] = new ImageIcon(getClass().getResource("/pieces/br.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[1][4] = new ImageIcon(getClass().getResource("/pieces/bq.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
			pieceImages[1][5] = new ImageIcon(getClass().getResource("/pieces/bk.png")).getImage()
					.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
		} catch (Exception e) {
			System.err.println("Error loading piece images: " + e.getMessage());
			System.err.println("Make sure the pieces folder is in your classpath");
		}
	}

	private Image getPieceImage(ChessPiece piece) {
		if (piece == null)
			return null;

		int colorIndex = piece.isWhite() ? 0 : 1;
		int pieceIndex = -1;

		switch (piece.getType()) {
		case PAWN:
			pieceIndex = 0;
			break;
		case KNIGHT:
			pieceIndex = 1;
			break;
		case BISHOP:
			pieceIndex = 2;
			break;
		case ROOK:
			pieceIndex = 3;
			break;
		case QUEEN:
			pieceIndex = 4;
			break;
		case KING:
			pieceIndex = 5;
			break;
		}

		return pieceImages[colorIndex][pieceIndex];
	}

	private void setupMouseListener() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!gameActive)
					return;

				int col = e.getX() / TILE_SIZE;
				int row = e.getY() / TILE_SIZE;

				if (col >= 0 && col < BOARD_SIZE && row >= 0 && row < BOARD_SIZE) {
					selectPiece(new Position(row, col));
				}
			}
		});
	}
	
	private void selectPiece(Position pos) {
	    if (!parent.isPlayerVsPlayer() && !whiteTurn) {
	        return; 
	    }
	    
	    ChessPiece clickedPiece = board[pos.row][pos.col];
	    
	    if (selectedPosition != null) {
	        if (isPossibleMove(pos)) {
	            movePiece(selectedPosition, pos);
	            selectedPosition = null;
	            possibleMoves.clear();
	            
	            if (!parent.isPlayerVsPlayer() && !whiteTurn) {
	                Timer timer = new Timer(1000, new ActionListener() {
	                    @Override
	                    public void actionPerformed(ActionEvent e) {
	                        makeAIMove();
	                    }
	                });
	                timer.setRepeats(false); 
	                timer.start();
	            }
	            
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

	    // Update king position cache BEFORE moving
	    if (movingPiece.getType() == PieceType.KING) {
	        if (movingPiece.isWhite()) {
	            whiteKingPos = to;
	        } else {
	            blackKingPos = to;
	        }
	    }
	    
	  
	    if (movingPiece.getType() == PieceType.KING && Math.abs(from.col - to.col) == 2) {
	       
	        int row = from.row;
	        if (to.col == 6) {
	            board[row][5] = board[row][7];
	            board[row][7] = null;
	        } else if (to.col == 2) { 
	            board[row][3] = board[row][0];
	            board[row][0] = null;
	        }
	    }

	    if (movingPiece.getType() == PieceType.PAWN && to.equals(enPassantTarget)) {
	        int capturedRow = movingPiece.isWhite() ? to.row + 1 : to.row - 1;
	        board[capturedRow][to.col] = null;
	    }

	    updateCastlingRights(from, movingPiece);

	    board[to.row][to.col] = movingPiece;
	    board[from.row][from.col] = null;

	    if (movingPiece.getType() == PieceType.PAWN && (to.row == 0 || to.row == BOARD_SIZE - 1)) {
	        showPromotionDialog(to, movingPiece.isWhite());
	    }

	    if (movingPiece.getType() == PieceType.PAWN && Math.abs(to.row - from.row) == 2) {
	        int direction = movingPiece.isWhite() ? -1 : 1;
	        int enemyPawnRow = movingPiece.isWhite() ? to.row + 1 : to.row - 1;
	        boolean hasEnemyPawn = false;
	        
	        if (from.col > 0) {
	            ChessPiece leftPiece = board[enemyPawnRow][from.col - 1];
	            if (leftPiece != null && leftPiece.getType() == PieceType.PAWN && 
	                leftPiece.isWhite() != movingPiece.isWhite()) {
	                hasEnemyPawn = true;
	            }
	        }
	        
	        if (from.col < 7) {
	            ChessPiece rightPiece = board[enemyPawnRow][from.col + 1];
	            if (rightPiece != null && rightPiece.getType() == PieceType.PAWN && 
	                rightPiece.isWhite() != movingPiece.isWhite()) {
	                hasEnemyPawn = true;
	            }
	        }
	        
	        if (hasEnemyPawn) {
	            int enPassantRow = from.row + (to.row - from.row) / 2;
	            enPassantTarget = new Position(enPassantRow, from.col);
	        } else {
	            enPassantTarget = null;
	        }
	    } else {
	        enPassantTarget = null;
	    }

	    whiteTurn = !whiteTurn;
	    parent.updateStatus(whiteTurn ? "White's turn" : "Black's turn");

	    checkGameState();
	}
	public void makeAIMove() {
	    if (!gameActive || whiteTurn || parent.isPlayerVsPlayer()) {
	        return;
	    }
	    
	    ChessAI.Move aiMove = chessAI.getBestMove(this);
	    
	    if (aiMove != null) {
	        ChessPiece movingPiece = board[aiMove.from.row][aiMove.from.col];
	        if (movingPiece != null) {
	            board[aiMove.to.row][aiMove.to.col] = movingPiece;
	            board[aiMove.from.row][aiMove.from.col] = null;
	            
	            if (movingPiece.getType() == PieceType.PAWN && 
	                (aiMove.to.row == 0 || aiMove.to.row == 7)) {
	                board[aiMove.to.row][aiMove.to.col] = Pieces.createPiece(PieceType.QUEEN, false);
	            }
	            
	            whiteTurn = true;
	            parent.updateStatus("White's turn");
	            repaint();
	            checkGameState();
	        }
	    }
	}

	private void showPromotionDialog(Position pos, boolean isWhite) {
		Promotion dialog = new Promotion((Frame) SwingUtilities.getWindowAncestor(this), isWhite);
		dialog.setVisible(true);

		PieceType selectedType = dialog.getSelectedPiece();

		board[pos.row][pos.col] = Pieces.createPiece(selectedType, isWhite);

		repaint();
	}

	private void updateCastlingRights(Position from, ChessPiece piece) {
		boolean isWhite = piece.isWhite();

		if (piece.getType() == PieceType.KING) {
			if (isWhite) {
				whiteKingsideCastle = false;
				whiteQueensideCastle = false;
			} else {
				blackKingsideCastle = false;
				blackQueensideCastle = false;
			}
		} else if (piece.getType() == PieceType.ROOK) {
			if (from.col == 0) { 
				if (isWhite)
					whiteQueensideCastle = false;
				else
					blackQueensideCastle = false;
			} else if (from.col == 7) {
				if (isWhite)
					whiteKingsideCastle = false;
				else
					blackKingsideCastle = false;
			}
		}
	}

	private void handleCastling(Position from, Position to) {
		int row = from.row;

		if (to.col == 6) { 
			board[row][5] = board[row][7]; 
			board[row][7] = null;
		} else if (to.col == 2) {
			board[row][3] = board[row][0]; 
			board[row][0] = null;
		}
	}

	private void checkGameState() {
	    boolean inCheck = isInCheck(whiteTurn);
	    
	    if (inCheck) {
	        boolean hasLegalMoves = false;
	        
	        for (int row = 0; row < BOARD_SIZE && !hasLegalMoves; row++) {
	            for (int col = 0; col < BOARD_SIZE && !hasLegalMoves; col++) {
	                ChessPiece piece = board[row][col];
	                if (piece != null && piece.isWhite() == whiteTurn) {
	                    List<Position> moves = getPossibleMoves(new Position(row, col));
	                    if (!moves.isEmpty()) {
	                        hasLegalMoves = true;
	                    }
	                }
	            }
	        }
	        
	        if (!hasLegalMoves) {
	            gameActive = false;
	            parent.updateStatus("Checkmate! " + (whiteTurn ? "Black" : "White") + " wins!");
	            return;
	        } else {
	            parent.updateStatus("Check! " + (whiteTurn ? "White" : "Black") + " is in check!");
	            return;
	        }
	    }
	    
	    parent.updateStatus(whiteTurn ? "White's turn" : "Black's turn");
	}

	private List<Position> getPossibleMoves(Position pos) {
		List<Position> moves = new ArrayList<>();
		
		ChessPiece piece = board[pos.row][pos.col];

		if (piece == null)
			return moves;

		moves = piece.getPossibleMoves(pos, board);

		addSpecialMoves(pos, moves, piece);

		moves.removeIf(move -> leavesKingInCheck(pos, move, piece.isWhite()));

		return moves;
	}

	private void addSpecialMoves(Position pos, List<Position> moves, ChessPiece piece) {
		if (piece.getType() == PieceType.PAWN && enPassantTarget != null) {
			int direction = piece.isWhite() ? -1 : 1;
			if (pos.row == enPassantTarget.row - direction && Math.abs(pos.col - enPassantTarget.col) == 1) {
				int capturedRow = piece.isWhite() ? enPassantTarget.row + 1 : enPassantTarget.row - 1;
				ChessPiece targetPawn = board[capturedRow][enPassantTarget.col];
				if (targetPawn != null && targetPawn.getType() == PieceType.PAWN && 
					targetPawn.isWhite() != piece.isWhite()) {
					moves.add(enPassantTarget);
				}
			}
		}

		if (piece.getType() == PieceType.KING) {
			addCastlingMoves(pos, moves, piece.isWhite());
		}
	}

	private void addCastlingMoves(Position pos, List<Position> moves, boolean isWhite) {
		if (isInCheck(isWhite))
			return;

		int row = isWhite ? 7 : 0;
		if (pos.row != row)
			return;

		if ((isWhite && whiteKingsideCastle) || (!isWhite && blackKingsideCastle)) {
			if (board[row][5] == null && board[row][6] == null && !isSquareAttacked(new Position(row, 4), !isWhite)
					&& !isSquareAttacked(new Position(row, 5), !isWhite)) {
				moves.add(new Position(row, 6));
			}
		}

		if ((isWhite && whiteQueensideCastle) || (!isWhite && blackQueensideCastle)) {
			if (board[row][1] == null && board[row][2] == null && board[row][3] == null
					&& !isSquareAttacked(new Position(row, 4), !isWhite)
					&& !isSquareAttacked(new Position(row, 3), !isWhite)) {
				moves.add(new Position(row, 2));
			}
		}
	}

	private boolean isSquareAttacked(Position square, boolean byWhite) {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				ChessPiece piece = board[row][col];
				if (piece != null && piece.isWhite() == byWhite) {
					List<Position> attacks = piece.getPossibleMoves(new Position(row, col), board);
					for (Position attack : attacks) {
						if (attack.equals(square)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean leavesKingInCheck(Position from, Position to, boolean movingPieceIsWhite) {
		ChessPiece temp = board[to.row][to.col];
		board[to.row][to.col] = board[from.row][from.col];
		board[from.row][from.col] = null;

		boolean inCheck = isInCheck(movingPieceIsWhite);

		board[from.row][from.col] = board[to.row][to.col];
		board[to.row][to.col] = temp;

		return inCheck;
	}

	private boolean isInCheck(boolean forWhite) {
		Position kingPos = findKing(forWhite);
		if (kingPos == null)
			return false;

		return isSquareAttacked(kingPos, !forWhite);
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

	private boolean isPossibleMove(Position pos) {
		for (Position p : possibleMoves) {
			if (p.equals(pos)) {
				return true;
			}
		}
		return false;
	}

	private void initializeBoard() {
	    for (int i = 0; i < BOARD_SIZE; i++) {
	        for (int j = 0; j < BOARD_SIZE; j++) {
	            board[i][j] = null;
	        }
	    }

	    for (int i = 0; i < BOARD_SIZE; i++) {
	        board[1][i] = Pieces.createPiece(PieceType.PAWN, false);
	    }

	    board[0][0] = Pieces.createPiece(PieceType.ROOK, false);
	    board[0][1] = Pieces.createPiece(PieceType.KNIGHT, false);
	    board[0][2] = Pieces.createPiece(PieceType.BISHOP, false);
	    board[0][3] = Pieces.createPiece(PieceType.QUEEN, false);
	    board[0][4] = Pieces.createPiece(PieceType.KING, false);
	    board[0][5] = Pieces.createPiece(PieceType.BISHOP, false);
	    board[0][6] = Pieces.createPiece(PieceType.KNIGHT, false);
	    board[0][7] = Pieces.createPiece(PieceType.ROOK, false);

	    for (int i = 0; i < BOARD_SIZE; i++) {
	        board[6][i] = Pieces.createPiece(PieceType.PAWN, true);
	    }

	    board[7][0] = Pieces.createPiece(PieceType.ROOK, true);
	    board[7][1] = Pieces.createPiece(PieceType.KNIGHT, true);
	    board[7][2] = Pieces.createPiece(PieceType.BISHOP, true);
	    board[7][3] = Pieces.createPiece(PieceType.QUEEN, true);
	    board[7][4] = Pieces.createPiece(PieceType.KING, true);
	    board[7][5] = Pieces.createPiece(PieceType.BISHOP, true);
	    board[7][6] = Pieces.createPiece(PieceType.KNIGHT, true);
	    board[7][7] = Pieces.createPiece(PieceType.ROOK, true);

	    whiteKingsideCastle = true;
	    whiteQueensideCastle = true;
	    blackKingsideCastle = true;
	    blackQueensideCastle = true;
	    enPassantTarget = null;
	    
	    // Initialize king positions
	    whiteKingPos = new Position(7, 4);
	    blackKingPos = new Position(0, 4);
	}

	public void newGame() {
	    initializeBoard();
	    selectedPosition = null;
	    possibleMoves.clear();
	    whiteTurn = true;
	    gameActive = true;
	    
	    chessAI = new ChessAI(false);
	    
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
