package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;


public class Table extends Observable{

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    
    
    private  Board chessBoard;
    
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    
    private  Move computerMove;
    
    private boolean highlightLegalMoves;
    
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private final static String defaultPieceImagesPath = "src\\art/";
    
    private static final Table INSTANCE = new Table();
    
    private Table() {
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = true;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel,BorderLayout.EAST);
        this.gameFrame.setVisible(true);
        
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static Table get() {
        return INSTANCE;
    }
    
    public void show() {
        System.out.println("\n\n");
        System.out.println("%%%%    inside show method of Table.java");
        System.out.println("Table.get().getMoveLog().clear()\t\t will be called");
        Table.get().getMoveLog().clear();
        System.out.println("Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog())           will be called");
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        System.out.println("Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog())                       will be called");
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        System.out.println("Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard())                       will be called");
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
        System.out.println("%%%%    Exiting from show method of Table.java");
        System.out.println("\n\n");
    }
    
    private GameSetup getGameSetup() {
        return this.gameSetup;
    }
    
    private Board getGameBoard() {
        return this.chessBoard;
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN file");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open up that pgn file");
            }
        });
        fileMenu.add(openPGN);
        
        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        
        return fileMenu;
    }
    
    private JMenu createPreferencesMenu() {
        
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        
        preferencesMenu.addSeparator();
        
        final JCheckBoxMenuItem legalMoveHighlighterCheckbox =  new JCheckBoxMenuItem("Highlight legal moves", false);
        
        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
            }
        });
        
        preferencesMenu.add(legalMoveHighlighterCheckbox);
        return preferencesMenu;
    }
    
    private JMenu createOptionsMenu() {
        
        final JMenu optionsMenu = new JMenu("Options");
        
        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setupGameMenuItem);
        return optionsMenu; 
    }
    
    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }
    
    private static class TableGameAIWatcher implements Observer {

        @Override
        public void update(final Observable o,final Object arg) {
            
            System.out.println("\n\n Inside update of TableGameAI watcher of table.java");
            
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard()
                    .currentPlayer()) &&
                    !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                    !Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                //Create an AI thread
                //execute ai wrok
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
            
            if(Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
                System.out.println("game over, "+Table.get().getGameBoard()
                    .currentPlayer() + " is in checkmate!");
            }
            if(Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                System.out.println("game over, "+Table.get().getGameBoard()
                    .currentPlayer() + " is in stalemate!");
            }
        }
        
    }
    
    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }
    
    public void updateComputerMove(final Move move) {
        this.computerMove = move;
    }
    
    public MoveLog getMoveLog() {
        return this.moveLog;
    }
    
    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }
    
    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }
    
    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }
    
    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }
    
    private static class AIThinkTank extends SwingWorker<Move, String> {
        
        private AIThinkTank() {
            
        }
        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy miniMax = new MiniMax(4);
            final Move bestMove = miniMax.execute(Table.get().getGameBoard());
            return bestMove;
        }
        @Override
        public void done() {
            try{
                final Move bestMove = get();
                
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().
                        currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(),
                                            Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER); 
            
            } catch(InterruptedException e) {
                System.out.println("hello in line 274 of Table.java");
                e.printStackTrace();
            } catch(ExecutionException e) {
                System.out.println("pareshan in line 277 of Table.java");
                e.printStackTrace();
            }
        }
    }
    
    public enum BoardDirection {
        
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }
            
            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }
            
            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    
    }
    
    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;
        
        BoardPanel() {
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i=0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }
        
        public void drawBoard(final Board board) {
            
            System.out.println("==  inside drawBoard() method of BoardPanel in Table.java");
            System.out.println("Firstly it will remove all previous components and then call drawTile( board) 64 tiles");
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
            System.out.println("==  Exiting from drawBoard() method of BoardPanel in Table.java");
        }
    }
    
    public static class MoveLog {
        
        private final List<Move> moves;
        
        MoveLog() {
            this.moves = new ArrayList<>();
        }
        
        public List<Move> getMoves() {
            return this.moves;
        }
        
        public void addMove(final Move move) {
            this.moves.add(move);
        }
        
        public int size() {
            return this.moves.size();
        }
        
        public void clear() {
            this.moves.clear();
        }
        
        public Move removeMove(int index) {
            return this.moves.remove(index);
        }
        
        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }
    
    enum PlayerType{
        HUMAN,
        COMPUTER
    }
    
    private class TilePanel extends JPanel {
        private final int tileId;
        
        TilePanel(final BoardPanel boardPanel, 
                  final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            
            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(final MouseEvent e) {
                    System.out.println("++  inside mouseClicked() method of TilePanel in Table.java");
                    
                    if(javax.swing.SwingUtilities.isRightMouseButton(e)) {
                        
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;  
                    }else if(javax.swing.SwingUtilities.isLeftMouseButton(e)){
                            
                            if(sourceTile == null) {
                                sourceTile = chessBoard.getTile(tileId);
                                humanMovedPiece = sourceTile.getPiece();
                                if(humanMovedPiece == null) {
                                    sourceTile = null;
                                }
                            } else {
                                destinationTile = chessBoard.getTile(tileId);
                                final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(),destinationTile.getTileCoordinate());
                                final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                                if(transition.getMoveStatus().isDone()) {
                                    chessBoard = transition.getTransitionBoard();
                                    //TODO add the move that was made to the move log
                                    moveLog.addMove(move);
                                }
                                sourceTile = null;
                                destinationTile = null;
                                humanMovedPiece = null;
                            }
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("//////////////////line 426 of tile in Table.java is here. you were wondering about\\\\\\\\\\\\\\\\\\\\");
                                    gameHistoryPanel.redo(chessBoard,moveLog);
                                    takenPiecesPanel.redo(moveLog);
                                    
                                    if(gameSetup.isAIPlayer(chessBoard.currentPlayer())){ 
                                        Table.get().moveMadeUpdate(PlayerType.COMPUTER);
                                    }
                                    
                                    boardPanel.drawBoard(chessBoard);
                                }
                            });
                    }
                    System.out.println("++  Exiting from mouseClicked() method of TilePanel in Table.java");
                }

                @Override
                public void mousePressed(MouseEvent e) { }

                @Override
                public void mouseReleased(MouseEvent e) { }

                @Override
                public void mouseEntered(MouseEvent e) { }

                @Override
                public void mouseExited(MouseEvent e) { }
                
            });
            
            validate();
        }
        
        public void drawTile(final Board board) {
           //System.out.println("++  inside drawTile() method of TilePanel in Table.java");
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
            //System.out.println("++  Exiting from drawtile() method of TilePanel in Table.java");
        }
        
        private void assignTilePieceIcon(final Board board) {
            //System.out.println("++  inside assignTilePieceIcon() method of TilePanel in Table.java");
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()) {
                try{
                    final BufferedImage image =
                        ImageIO.read(new File(defaultPieceImagesPath +
                        board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1)+
                        board.getTile(this.tileId).getPiece().toString() + ".png"));
                    add(new JLabel(new ImageIcon(image)));
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("++  Exiting from assignTilePieceIcon() method of TilePanel in Table.java");
        }
        
        private void highlightLegals(final Board board) {
            if(highlightLegalMoves) {
                for(final Move move : pieceLegalMoves(board)) {
                    
                    if(move.getDestinationCoordinate() == this.tileId) {
                        System.out.println("++  inside highlightLegals() method of TilePanel in Table.java"
                            + "now green dot will pasted");
                    
                        try{
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("src/art/green_dot.png")))));
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            
        }
        
        private Collection<Move> pieceLegalMoves(final Board board) {
            
            if(humanMovedPiece != null &&
                    humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance())
            {
                System.out.println("++ it is called by highlightlegals,,,,, inside pieceLegalMoves() method of TilePanel in Table.java"
                        + " and will be returned after calculateLegalMoves(board)****");
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTileColor() {
            //System.out.println("++  inside assignTileColor() method of TilePanel in Table.java");
            Color lightTileColor,darkTileColor;
            lightTileColor = Color.decode("#FFFACD");
            darkTileColor = Color.GREEN;
            if(BoardUtils.EIGHT_RANK[this.tileId] ||
                    BoardUtils.SIXTH_RANK[this.tileId] ||
                    BoardUtils.FOURTH_RANK[this.tileId] ||
                    BoardUtils.SECOND_RANK[this.tileId] ) {
                setBackground(this.tileId %2 == 0 ? lightTileColor : darkTileColor);
            }else if(BoardUtils.SEVENTH_RANK[this.tileId] ||
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] ||
                    BoardUtils.FIRST_RANK[this.tileId] ) {
                setBackground(this.tileId %2 == 0 ? darkTileColor : lightTileColor);
            }
            //System.out.println("++  Exiting from assignTileColor() method of TilePanel in Table.java");       
        }
        
    }
}
