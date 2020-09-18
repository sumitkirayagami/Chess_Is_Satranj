
package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class King extends Piece{

    private final static int[] CANDIDATE_MOVE_CANDIDATE = {-9,-8,-7,-1,1,7,8,9};
    
    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;
    
    public King(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance,true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }
    
    
     
    public King(final Alliance pieceAlliance,
            final int piecePosition,
            final boolean isFirstMove,
            final boolean isCastled,
            final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING,piecePosition, pieceAlliance,isFirstMove);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }
    
    public boolean isCastled() {
        return this.isCastled;
    }
    
    public boolean isKingSideCastleCapable(){
        
        return this.kingSideCastleCapable;
    }
    public boolean isQueenSideCastleCapable(){
        return this.queenSideCastleCapable;
    }
  
    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        
        final List<Move> legalMoves = new ArrayList<>();
        
        for(final int currentCandidateOffset: CANDIDATE_MOVE_CANDIDATE) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            
            if(isFirstColumnExclusion(this.piecePosition,currentCandidateOffset) ||
               isEighthColumnExclusion(this.piecePosition,currentCandidateOffset)){
                continue;
            }
            
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
               final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if(!candidateDestinationTile.isTileOccupied()){
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                }else{
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance allianceOfPiece = pieceAtDestination.getPieceAlliance();
                    if(this.pieceAlliance != allianceOfPiece){
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate,pieceAtDestination));
                    }
                }
        }
        }
        //Adding moves for Castling like KingSide and QueenSide
        if(this.isCastled==false){
            if(this.kingSideCastleCapable){
                Collection<Piece> opponentPlayerPieces;
                int castleRookStart,castleRookDestination,kingDestination,oppKingDangerPosition;
                if(this.getPieceAlliance().toString().equals("BLACK")){
                    castleRookStart=7 ;
                    castleRookDestination= 5;
                    kingDestination=6;
                    oppKingDangerPosition=55;
                    opponentPlayerPieces = board.getWhitePieces();
                }
                else{
                    castleRookStart=63 ;
                    castleRookDestination=61;
                    kingDestination=62;
                    oppKingDangerPosition=14;
                    opponentPlayerPieces = board.getBlackPieces();
                }
                boolean a=true,b=true;
                if(board.getTile(kingDestination).isTileOccupied() || board.getTile(castleRookDestination).isTileOccupied())
                    a=false;
                
               
                outer:for(Piece oppPiece:opponentPlayerPieces){
                    if(!oppPiece.getPieceType().isKing() ){ //&& !oppPiece.getPieceType().isPawn()
                        Collection<Move> oppMoves=oppPiece.calculateLegalMoves(board);
                        for(Move m:oppMoves){
                            int target=m.getDestinationCoordinate();
                            if(target==castleRookDestination || target==kingDestination){
                                b=false;
                                break outer;
                            }    
                        }
                    }else{
                        if(oppPiece.getPiecePosition()==oppKingDangerPosition){
                            b=false;
                            break;
                        }
                    }
                }
                
                
                if(a&b)  
                    legalMoves.add(new KingSideCastleMove(board,
                                                        this,
                                                        kingDestination,
                                                        new Rook(this.getPieceAlliance(),castleRookStart,false),
                                                        castleRookStart,
                                                        castleRookDestination));
                
            }
            if(this.queenSideCastleCapable){
                Collection<Piece> opponentPlayerPieces;
                int castleRookStart,castleRookDestination,kingDestination,extraTileCoordinate,oppKingDangerPosition;
                if(this.getPieceAlliance().toString().equals("BLACK")){
                    castleRookStart=0;
                    castleRookDestination= 3;
                    kingDestination=2;
                    extraTileCoordinate=1;
                    oppKingDangerPosition=10;
                    opponentPlayerPieces = board.getWhitePieces();
                }
                else{
                    castleRookStart=56 ;
                    castleRookDestination=59;
                    kingDestination=58;
                    extraTileCoordinate=57;
                    oppKingDangerPosition=50;
                    opponentPlayerPieces = board.getBlackPieces();
                }
                boolean a=true,b=true;
                if(board.getTile(kingDestination).isTileOccupied() || board.getTile(castleRookDestination).isTileOccupied() || board.getTile(extraTileCoordinate).isTileOccupied())
                    a=false;
                
                outer:for(Piece oppPiece:opponentPlayerPieces){
                    if(!oppPiece.getPieceType().isKing()){
                        Collection<Move> oppMoves=oppPiece.calculateLegalMoves(board);
                        for(Move m:oppMoves){
                            int target=m.getDestinationCoordinate();
                            if(target==castleRookDestination || target==kingDestination || target==extraTileCoordinate){
                                b=false;
                                break outer;
                            }    
                        }
                    }else{
                        if(oppPiece.getPiecePosition()==oppKingDangerPosition || oppPiece.getPiecePosition()==oppKingDangerPosition-1){
                            b=false;
                            break;
                        }
                    }
                }
                
                if(a&b)
                legalMoves.add(new QueenSideCastleMove(board,
                                                        this,
                                                        kingDestination,
                                                        new Rook(this.getPieceAlliance(),castleRookStart,false),
                                                        castleRookStart,
                                                        castleRookDestination));
            }
        }
        //inbetween work in progress
        return ImmutableList.copyOf(legalMoves);
    }
    @Override
    public King movePiece(final Move move) {
        return new King(move.getMovedPiece().getPieceAlliance(),
                        move.getDestinationCoordinate(),
                        false,
                        move.isCastlingMove(),
                        false,
                        false);
    }
    
    @Override
    public String toString() {
        return PieceType.KING.toString();
    }
    
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){ 
        return BoardUtils.FIRST_COLUMN[currentPosition] && ( candidateOffset == -9 || candidateOffset == -1 || 
                candidateOffset == 7); 
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ( candidateOffset == -7 || candidateOffset == 1 |
                candidateOffset == 9);  
    }

}
