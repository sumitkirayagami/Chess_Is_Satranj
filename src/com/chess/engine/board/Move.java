package com.chess.engine.board;

import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;


public abstract class Move {

    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;
    
    public static final Move NULL_MOVE = new NullMove();
    
    
    private Move(final Board board, 
                 final Piece pieceMoved,
                 final int destinationCoordinate){
        this.board = board;
        this.movedPiece = pieceMoved;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = pieceMoved.isFirstMove();
    }
    
    private Move(final Board board,
            final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }
    
    @Override
    public int hashCode() {
        System.out.println("## inside hashCode method of Move.java");
        final int prime = 31;
        int result =1;
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        System.out.println("## Exiting from hashCode method of Move.java");
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        System.out.println("## inside equals method of Move.java");
        if(this == other){
            return true;
        }
        if( ! (other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        System.out.println("## Exiting from equals method of Move.java");
        return  getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getMovedPiece().equals(otherMove.getMovedPiece());
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public int getCurrentCoordinate(){return this.getMovedPiece().getPiecePosition();}
    
    public int getDestinationCoordinate() { return this.destinationCoordinate; }
    
    public Piece getMovedPiece() { return this.movedPiece; }
    
    public boolean isAttack() { return false; }
    
    public boolean isCastlingMove() { return false; }
    
    public Piece getAttackedPiece() { return null;}
    
    public Board execute() {    
        System.out.println("##  execute method of Move which is the parent of different type of moves");
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            //move the moving piece using next line
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            System.out.println("## Exiting from execute method of Move.java");
            //builder.setMoveTransition(this); from github
            return builder.build();
    }
        
    public static class PawnPromotion extends Move{

        final Move decoratedMove;
        final Pawn promotedPawn;
        
        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(),decoratedMove.getMovedPiece(),decoratedMove.getDestinationCoordinate());
            System.out.println("PawnPromotion constructor");
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }
        
        @Override
        public int hashCode(){
            System.out.println("####pp inside hashCode method of PawnPromotion of Move.java and returning****");
            return decoratedMove.hashCode() + (31* promotedPawn.hashCode());
        }
        
        @Override
        public boolean equals(final Object other) {
            System.out.println("####pp inside equals method of PawnPromotion of Move.java and returning****");
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }
        
        @Override
        public Board execute(){
            System.out.println("####pp inside execute of PawnPromtion of Move.java");
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();
            for(final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()) {
                if(!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            System.out.println("####pp Exiting from Execute method of PawnPromotion of Move.java");
            return builder.build();
        }
        
        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }
        
        @Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }
        
        @Override
        public String toString() {
            return "";
        }
        
    }
    
    public static final class MajorMove extends Move{

        public MajorMove(final Board board,final Piece movePiece,final int destinationCoordinate) {
            super(board, movePiece, destinationCoordinate);
            System.out.println("MajorMove constructor");
        }
        
        @Override
        public boolean equals(final Object other) {
            System.out.println("####mm  inside equals method MajorMove class of Move.java and returning****");
            return this == other || other instanceof MajorMove && super.equals(other);
        }
        
        @Override 
        public String toString() {
            System.out.println("####mm inside toString method MajorMove class of Move.java and returning****");
            return movedPiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    
    public static class MajorAttackMove extends AttackMove {
        
        public MajorAttackMove(final Board board,
                                final Piece pieceMoved,
                                final int destinationCoordinate,
                                final Piece pieceAttacked) {
            super(board,pieceMoved, destinationCoordinate, pieceAttacked);
            System.out.println("MajorAttackMove constructor");
        }
        
        @Override
        public boolean equals(final Object other) {
            System.out.println("######mam  inside equals method MajorAttackMove class of Move.java and returning****");
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }
        
        @Override
        public String toString() {
            System.out.println("######mam  inside toString method MajorAttackMove class of Move.java and returning****");
            return movedPiece.getPieceType() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    
    public static class PawnMove extends Move {

        public PawnMove(final Board board,
                        final Piece movePiece,
                        final int destinationCoordinate) {
            super(board, movePiece, destinationCoordinate);
            System.out.println("PawnMove constructor");
        }
        
        @Override
        public boolean equals(final Object other) {
            System.out.println("####pm  inside equals method of PawnMove class of Move.java and returning");
            return this == other || other instanceof PawnMove && super.equals(other);
        }
        
        @Override
        public String toString() {
            System.out.println("####pm  inside toString method of PawnMove class of Move.java and returning");
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }
    
    //This class has overriden execute method
    public static final class PawnJump extends Move {

        public PawnJump(final Board board,
                        final Piece movePiece,
                        final int destinationCoordinate) {
            super(board, movePiece, destinationCoordinate);
            System.out.println("PawnJump constructor");
        }
        
        @Override
        public Board execute() {
            System.out.println("####pj inside the execute method of Pawn Jump of Move.java");
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            System.out.println("####pj Exiting from the execute method of Pawn Jump of Move.java");
            return builder.build();
        }
        
        @Override
        public String toString() {
            System.out.println("####pj inside toString method of Pawn Jump of Move.java and returning****");
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate); 
        }
    }
    
    public static  class AttackMove extends Move{

        final Piece attackedPiece;
        public AttackMove(final Board board,final Piece movePiece,
                final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movePiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
            System.out.println("AttackMove constructor");
        }
        
        @Override
        public int hashCode() {
            System.out.println("####am  inside hashCode method of AttackMove class of Move.java and returning****");
            return this.attackedPiece.hashCode() + super.hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            System.out.println("####am  inside equals method of AttackMove class of Move.java");
            if(this == other) {
                return true;
            }
            if(!(other instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            System.out.println("####am Exitng form  equals method of AttackMove class of Move.java");
            return super.equals(otherAttackMove) &&
                    getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }
        
        @Override
        public boolean isAttack() {
            return true;
        }
        
        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }
    
    public static class PawnAttackMove extends AttackMove {

        public PawnAttackMove(final Board board,
                              final Piece movePiece,
                              final int destinationCoordinate,
                              final Piece attackedPiece) {
            super(board, movePiece, destinationCoordinate, attackedPiece);
            System.out.println("PawnAttackMove constructor");
        }
        
        @Override
        public boolean equals(final Object other) {
            System.out.println("######pam  inside equals method of PawnAttackMove class of Move.java and returning****");
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }
        
        @Override
        public String toString() {  
            System.out.println("######pam  inside toString method of PawnAttackMove class of Move.java and returning****");
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0,1) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
        
    }
    
    //This class has overriden execute method 
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(final Board board,
                                       final Piece movePiece,
                                       final int destinationCoordinate,
                                       final Piece attackedPiece) {
            super(board, movePiece, destinationCoordinate, attackedPiece);
            System.out.println("PawnEnPassantAttackMove constructor");
        }
        
        @Override
        public boolean equals(final Object other) {
            System.out.println("######## inside equals method of PawnEnPassantAttackMove class of Move.java and returning****");
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }
        
        @Override
        public Board execute() {
            System.out.println("######## inside execute method of PawnEnPassantAttackMove of Move.java");
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                if(!piece.equals(this.getAttackedPiece())){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            System.out.println("######## Exiting from execute method of PawnEnPassantAttackMove of Move.java");
            return builder.build();
        }
        
    }
      
    //This class has overriden execute method
    public abstract  class CastleMove extends Move{
        
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;
  
        public CastleMove(final Board board,
                          final Piece movePiece,
                          final int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination) {
            super(board, movePiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
            System.out.println("CastleMove constructor");
        }
        public Rook getCastleRook() { return this.castleRook; }
        
        @Override
        public boolean isCastlingMove() { return true; }
        
        @Override
        public Board execute() {
            
            System.out.println("####c inside the execute method of CastleMove of Move.java");
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece) && !(piece.getPiecePosition()==this.castleRook.getPiecePosition())) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            //Todo look into the first move on normal pieces
            builder.setPiece(new Rook(this.castleRook.getPieceAlliance(),this.castleRookDestination));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            System.out.println("####c Exiting from the execute method of CastleMove of Move.java");
            return builder.build();
        }
        
        @Override 
        public int hashCode() {
            System.out.println("####c inside the hashCode method of CastleMove of Move.java");
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            System.out.println("####c Exiting from the hashCode method of CastleMove of Move.java");
            return result;
        }
        
        @Override
        public boolean equals(final Object other) {
            System.out.println("####c inside the equals method of CastleMove of Move.java");
            if(this==other){
                return true;
            }
            if(!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            System.out.println("####c Exiting from the equals method of CastleMove of Move.java");
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }
    
    public static class KingSideCastleMove extends CastleMove {
        
        
        public KingSideCastleMove(final Board board,
                                final Piece movePiece,
                                final int destinationCoordinate,
                                final Rook castleRook,
                                final int castleRookStart,
                                final int castleRookDestination) {
            super(board, movePiece, destinationCoordinate,castleRook, castleRookStart, castleRookDestination);
            System.out.println("KingSideCastleMove constructor");
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }
        
        @Override
        public String toString() {  return "0-0"; }
        
    }
    
    public static class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(final Board board,
                                final Piece movePiece,
                                final int destinationCoordinate,
                                final Rook castleRook,
                                final int castleRookStart,
                                final int castleRookDestination){
            super(board, movePiece, destinationCoordinate,castleRook, castleRookStart, castleRookDestination);
            System.out.println("QueenSideCastleMove constructor");
        }
        
         @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }
        
        @Override
        public String toString() { return "0-0-0"; }
    }
    
    public static class NullMove extends Move {

        public NullMove() {
            super(null, 65);
        }
         
        @Override
        public Board execute() {
            throw new RuntimeException("cannot execute the null move!");
        }
        
        @Override
        public int getCurrentCoordinate() {
            return -1;
        }
    }
    
    public static class MoveFactory {
        
        private MoveFactory() {
            throw new RuntimeException("NOt instantiable!");
        }
        
        public static Move createMove(final Board board,
                                    final int currentCoordinate,
                                    final int destinationCoordinate) {
            
            System.out.println("## inside createMove method of MoveFactory of move.java");
            for(final Move move : board.getAllLegalMoves()) {
                if(move.getCurrentCoordinate() == currentCoordinate && 
                        move.getDestinationCoordinate() == destinationCoordinate) {
                         return move;
                }
            }
            System.out.println("## Exiting from createMove method of MoveFactory of move.java");
            return NULL_MOVE;
        }
    }
    
} 
