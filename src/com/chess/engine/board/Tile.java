package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;



public abstract class Tile {
    
    protected final int tileCoordinate; //number of tile to be identify the tile
    //final means once tileCoordinate has some value it can not be change again
    //protected means only child class can access
    
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();
    
    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles(){
         System.out.println("------inside createAllPossibleEmptyTiles() method of Tile.java");
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        
        for(int i=0;i<BoardUtils.NUM_TILES;i++){
            emptyTileMap.put(i,new EmptyTile(i));
        }
        System.out.println("------Exiting from createAllPossibleEmptyTiles() method of Tile.java");
        // here used Guava dependency
        return ImmutableMap.copyOf(emptyTileMap);   //so that no one can change or delete it
        //inbuilt 
        // return Collections.unmodifiableMap(emptyTileMap);
    }
    
    public static Tile createTile(final int tileCoordinate, final Piece piece) {
        //System.out.println("------inside createTile method of Tile.java and returning****");
        return piece != null ? new OccupiedTile(tileCoordinate, piece): EMPTY_TILES_CACHE.get(tileCoordinate);
    }
    
    private Tile(final int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }
    
    public abstract boolean isTileOccupied();
    
    public abstract Piece getPiece();
    
    public int getTileCoordinate() {
        return this.tileCoordinate;
    }
    
    public static final class EmptyTile extends Tile {
        private EmptyTile(final int coordinate){
            super(coordinate);
        }
        
        @Override
        public String toString() {
            return "-";
        }
        
        @Override
        public boolean isTileOccupied(){
           return false; 
        }
        
        @Override
        public Piece getPiece() {
            return null;
        }
    }
    
    public static final class OccupiedTile extends Tile {
    
        private final Piece pieceOnTile;
        
        private OccupiedTile(int tileCoordinate,final Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }
        
        @Override
        public String toString() {
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                    getPiece().toString();
        }
        
        @Override
        public boolean isTileOccupied(){
            return true;
        }
        
        @Override
        public Piece getPiece(){
            return this.pieceOnTile;
        }
}
    
}
