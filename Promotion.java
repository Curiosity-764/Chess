import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Promotion extends JDialog {
    private PieceType selectedPiece;
    
    public Promotion(Frame parent, boolean isWhite) {
        super(parent, "Pawn Promotion", true);
        this.selectedPiece = PieceType.QUEEN; 
        
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton queenBtn = createPieceButton(PieceType.QUEEN, isWhite);
        JButton rookBtn = createPieceButton(PieceType.ROOK, isWhite);
        JButton bishopBtn = createPieceButton(PieceType.BISHOP, isWhite);
        JButton knightBtn = createPieceButton(PieceType.KNIGHT, isWhite);
        
        panel.add(queenBtn);
        panel.add(rookBtn);
        panel.add(bishopBtn);
        panel.add(knightBtn);
        
        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }
    
    private JButton createPieceButton(PieceType pieceType, boolean isWhite) {
        String colorPrefix = isWhite ? "w" : "b";
        String imagePath = "";
        
        switch (pieceType) {
            case QUEEN: imagePath = "/pieces/" + colorPrefix + "q.png"; break;
            case ROOK: imagePath = "/pieces/" + colorPrefix + "r.png"; break;
            case BISHOP: imagePath = "/pieces/" + colorPrefix + "b.png"; break;
            case KNIGHT: imagePath = "/pieces/" + colorPrefix + "n.png"; break;
        }
        
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPiece = pieceType;
                dispose();
            }
        });
        
        button.setToolTipText(pieceType.toString());
        return button;
    }
    
    public PieceType getSelectedPiece() {
        return selectedPiece;
    }
}