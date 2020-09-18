
package com.chess.gui;

import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.gui.Table.MoveLog;
import com.google.common.primitives.Ints;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;


public class TakenPiecesPanel extends JPanel {
    
    private final JPanel northPanel;
    private final JPanel southPanel;
    
    private static final Color PANEL_COLOR = Color.decode("0xFDFE6");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40,80);
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    
    public TakenPiecesPanel() {
        super(new BorderLayout() );
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8,2));
        this.southPanel = new JPanel(new GridLayout(8,2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
        
    }
    
    public void redo(final MoveLog moveLog) {
        System.out.println("@@@      inside redo() method of takenPiecesPanel.java");
        this.southPanel.removeAll();
        this.northPanel.removeAll();
        
        List<Piece> whiteTakenPieces = new ArrayList<>();
        List<Piece> blackTakenPieces = new ArrayList<>();
        
        for(final Move move : moveLog.getMoves()) {
            if(move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                }else if(takenPiece.getPieceAlliance().isBlack()) {
                    blackTakenPieces.add(takenPiece);
                }
                else{
                    throw new RuntimeException("should not reach here!");
                } 
            }
        }
        
        Collections.sort(whiteTakenPieces, new Comparator<Piece>(){
           @Override
           public int compare(Piece o1, Piece o2) {
               return Ints.compare(o1.getPieceValue(),o2.getPieceValue());
           }
        });
        
        Collections.sort(blackTakenPieces, new Comparator<Piece>(){
           @Override
           public int compare(Piece o1, Piece o2) {
               return Ints.compare(o1.getPieceValue(),o2.getPieceValue());
           }
        });
        
        for(final Piece takenPiece : whiteTakenPieces) {
            try {
                
                final ImageIcon ic=new ImageIcon("src/art/W"+takenPiece.toString() + ".png");
                final Image img = ic.getImage().getScaledInstance(20,20,Image.SCALE_DEFAULT);
                final ImageIcon icon=new ImageIcon(img);
                final JLabel imageLabel = new JLabel(icon);
                this.northPanel.add(imageLabel);
            }catch(Exception e) {
            }
        }
        for(final Piece takenPiece : blackTakenPieces) {
            try {
                final ImageIcon ic=new ImageIcon("src/art/B"+takenPiece.toString() + ".png");
                final Image img = ic.getImage().getScaledInstance(20,20,Image.SCALE_DEFAULT);
                final ImageIcon icon=new ImageIcon(img);
                final JLabel imageLabel = new JLabel(icon);
                this.southPanel.add(imageLabel);
            }catch(Exception e) {
            }
        }
        validate();
        System.out.println("@@@      Exiting from redo() method of takenPiecesPanel.java");
    }
}
