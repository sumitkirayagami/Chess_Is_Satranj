
package com.chess.engine.pieces;

import com.chess.engine.Alliance;       //Alliance is enum
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import java.util.Collection;



public abstract class Piece {
  
    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cachedHashCode;
    
    Piece(final PieceType pieceType,
            final int piecePosition,
            final Alliance pieceAlliance,
            final boolean isFirstMove) {
        this.pieceType = pieceType;
        this. pieceAlliance = pieceAlliance;
        this. piecePosition = piecePosition;
        //todo more work here
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }
    
    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1: 0);
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
       if(this == other) {
           return true;
       }
       if( !(other instanceof Piece)) {
        return false;
       }
       final Piece otherPiece = (Piece) other;
       return  piecePosition == otherPiece.getPiecePosition() &&
               pieceType == otherPiece.getPieceType() &&
               pieceAlliance == otherPiece.getPieceAlliance() &&
               isFirstMove == otherPiece.isFirstMove();
    }
    
    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }
    
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }
    
    public int getPiecePosition() {
        return this.piecePosition;
    }
    
    public boolean isFirstMove() {
        return this.isFirstMove;
    }
    
    public PieceType getPieceType() {
        return this.pieceType;
    }
    
    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }
    public abstract Collection<Move> calculateLegalMoves(final Board board);
    
    public abstract Piece movePiece(final Move move);

    
    public enum PieceType {
        
        PAWN(100,"P") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
            
            @Override
            public boolean isPawn(){
                return true;
            }
        },        
        KNIGHT(300,"N") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
            
            @Override
            public boolean isPawn(){
                return false;
            }
        },
        BISHOP(300,"B") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
            
            @Override
            public boolean isPawn(){
                return false;
            }
        },
        ROOK(500,"R") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
            
            @Override
            public boolean isPawn(){
                return false;
            }
        },
        QUEEN(900,"Q") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
            
            @Override
            public boolean isPawn(){
                return false;
            }
        },
        KING(10000,"K") {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
            
            @Override
            public boolean isPawn(){
                return false;
            }
        };
        private final String pieceName;
        private final int pieceValue;
        
        PieceType(final int pieceValue,final String pieceName) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }
        
        @Override
        public String toString() { 
            return this.pieceName;
        }
        
        public int getPieceValue(){
            return this.pieceValue;
        }
        public abstract boolean isKing();
        public abstract boolean isRook();
        public abstract boolean isPawn();
    }
    

    
}
