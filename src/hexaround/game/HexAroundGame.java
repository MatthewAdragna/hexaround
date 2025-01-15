/*
 * ******************************************************************************
 *  This files was developed for CS4233: Object-Oriented Analysis & Design.
 *  The course was taken at Worcester Polytechnic Institute.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  * Copyright ©2016-2017 Gary F. Pollice
 *  ******************************************************************************
 *
 */

package hexaround.game;

import hexaround.config.CreatureDefinition;
import hexaround.config.GameConfiguration;
import hexaround.config.PlayerConfiguration;
import hexaround.game.BoardChecks.CheckingHandlerInstance;
import hexaround.required.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class HexAroundGame implements IHexAroundGameManager{
    public static HashMap<CreatureName,CreatureDefinition> creatureNameDefinitionMap;
    private final static HexCoordinate origin = new HexCoordinate(0,0);
    private HashMap<HexCoordinate,Creature> board = new HashMap<>();
    private PlayerName currentPlayer = PlayerName.BLUE;
    private HashMap<CreatureName, Integer>[] inventories= new HashMap[2];
    private boolean butterflyPlaced[] = {false,false};
    private CheckingHandlerInstance movementHandler;
    /**
     * This variable represents the amount of moves made throughout the game by both players,
     * floor(moveNumber/2) or rather the returnTurn() function gives the current turn number for either player at any point in time.
     */
    private int moveNumber = 0;


    /** Creates a HexAroundGame board with the default player being PlayerName.BLUE
     * Using the information from the inputted GameConfiguration config
     * @param config
     */
    public HexAroundGame(GameConfiguration config) {
        for(Object pg : config.players().toArray()){
            if(pg !=null){
                PlayerConfiguration pConfig = (PlayerConfiguration)pg;
                inventories[pConfig.Player().ordinal()] = new HashMap<>(pConfig.creatures());
            }
        }
        creatureNameDefinitionMap = new HashMap<>();
        for(CreatureDefinition y: config.creatures()){creatureNameDefinitionMap.put(y.name(),y);}
        movementHandler = new CheckingHandlerInstance(config.creatures());
    }

    /**
     * @param name
     * @return returns the creature definition for the given CreatureName
     */
    public static CreatureDefinition getCreatureDefinition(CreatureName name){return creatureNameDefinitionMap.get(name);}


    /**
     * @param in
     * @return Returns the maxMoveDistance for the given Creature
     */
    public static int getCreatureMaxMoveDistance(Creature in){return getCreatureDefinition(in.name).maxDistance();}

    private boolean isValidPlacement(HexCoordinate in){
        boolean hasAllyNext = false;
        boolean hasEnemyNext = false;
        for(HexCoordinate tile: in.neighbors()){
            if(board.containsKey(tile)){
                Creature tileCreature = board.get(tile);
                if(tileCreature.player == currentPlayer){hasAllyNext=true;}
                if(tileCreature.player != currentPlayer){hasEnemyNext=true;}
            }
        }
        return (moveNumber <2) || (hasAllyNext && !hasEnemyNext);
    }
    private int returnTurn(){return moveNumber/2;}

    private GameState createValidMove(){
        currentPlayer = (currentPlayer == PlayerName.BLUE) ? PlayerName.RED : PlayerName.BLUE;
        return new GameState(MoveResult.OK,null);
    }
    private GameState createInvalidMove(String message){ return new GameState(MoveResult.MOVE_ERROR,message);}

    /**
     * @param creature
     * @param x
     * @param y
     * Attempts to place the creature at the given coordinate on the board, if it is the first turn you will be forced to place it at 0,0
     *
     * @return * Will return a gamestate that is either valid with a null message (MoveResult.OK)
     *      * or invalid with a message describing the error MOVERESULT.INVALID_MOVE)
     *
     */
    @Override
    public GameState placeCreature(CreatureName creature, int x, int y) {
        HexCoordinate givenTile = new HexCoordinate(x,y);
        int gameOverState= gameIsDone();
        if(gameOverState == 2) {return createInvalidMove("The game has been drawn");}
        if(gameOverState == 1) {return createInvalidMove("The game is over, somebody won");}

        if(moveNumber == 0){if(!givenTile.equals(origin)){
                return createInvalidMove("The first move has to be at <0,0>");}}
            if(moveNumber == 1){if(!givenTile.neighbors().contains(origin)){
                return createInvalidMove("The first move of the second player has to neighbor <0,0>");}}
            if(!inventories[currentPlayer.ordinal()].containsKey(creature)|| inventories[currentPlayer.ordinal()].get(creature) == 0){
                return createInvalidMove(
                        String.format("%s does not have %s available", (
                                currentPlayer == PlayerName.BLUE) ? "PLAYER-BLUE" : "PLAYER-RED",
                                creature.toString()));
            }
            if(board.containsKey(givenTile)){
                return createInvalidMove(
                        String.format("There is already a creature at: %s",
                                givenTile.toString()));
            }
            if(hasToPlaceButterfly(creature)){
                return createInvalidMove(
                        String.format("%s has to place a butterfly this turn.", (
                                        currentPlayer == PlayerName.BLUE) ? "PLAYER-BLUE" : "PLAYER-RED"
                                ));
            }
            if(!isValidPlacement(givenTile)){return createInvalidMove("Invalid Move: To place a creature it must be adjacent to an ally and not adjacent to an enemy.");}



        //Places creature
        plopTile(givenTile,new Creature(creature,currentPlayer));
        // Updates Inventory
        int amtOfCreature  = inventories[currentPlayer.ordinal()].get(creature);
        inventories[currentPlayer.ordinal()].replace(creature,amtOfCreature -1);
        //Updates if the player has placed the butterfly
        if(creature.equals(CreatureName.BUTTERFLY)){butterflyPlaced[currentPlayer.ordinal()] = true;}

        // Increments the move number, changes the turn and then switches the current player (Done within the createValidMove Function)
        moveNumber++;
        return createValidMove();
    }

    /**
     * @param board
     * Adds a quick way to add custom board for testing purposes
     */
    public void modifyForTests(HashMap<HexCoordinate,Creature> board, int turnNumber,boolean[] butterfliesPlaced){
        this.board = board;
        this.moveNumber = turnNumber;
        this.butterflyPlaced = butterfliesPlaced;
    }
    private boolean hasToPlaceButterfly(CreatureName in){return !butterflyPlaced[currentPlayer.ordinal()] && (returnTurn() >= 3) && !in.equals(CreatureName.BUTTERFLY);}

    /**
     * @param creature
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return
     *
     * Moves the creature from fromX,fromY to toX,toY
     * Will return a gamestate that is either valid with a null message (MoveResult.OK)
     * or invalid with a message describing the error MOVERESULT.INVALID_MOVE)
     */
    @Override
    public GameState moveCreature(CreatureName creature, int fromX, int fromY, int toX, int toY) {
        int gameOverState= gameIsDone();
        if(gameOverState == 2) {return createInvalidMove("The game has been drawn");}
        if(gameOverState == 1) {return createInvalidMove("The game is over, somebody won");}

        HexCoordinate fromTile = new HexCoordinate(fromX,fromY);
        HexCoordinate toTile = new HexCoordinate(toX,toY);
//        boolean isTrappingTile = board.containsKey(toTile) && !getCreatureDefinition(creature).properties().contains(CreatureProperty.TRAPPING);
//             Creature toPlace = isTrappingTile ? new Creature(creature, currentPlayer,board.get(toTile)) : new Creature(creature, currentPlayer) ;
         Creature toPlace = new Creature(creature, currentPlayer) ;


        if(hasToPlaceButterfly(creature)){
            return createInvalidMove(
                    String.format("%s has to place a butterfly this turn.", (
                            currentPlayer == PlayerName.BLUE) ? "PLAYER-BLUE" : "PLAYER-RED"
                    ));
        }
        if(fromTile.equals(toTile)){return createInvalidMove("Please input two separate coordinates to move between");}
        if(!board.containsKey(fromTile)){
            return createInvalidMove(
                    String.format("There is no creature at: %s",
                            fromTile.toString()));
        }
//        if(isTrappingTile){
//            return createInvalidMove(
//                    String.format("There is already a creature at: %s",
//                            toTile.toString()));
//        }
        if(!board.get(fromTile).name.equals(creature)){
            return createInvalidMove(
                    String.format("The topmost creature at: %s is not a %s",
                            fromTile.toString(),
                            creature.toString()
                    ));
        }
        if(getCreatureDefinition(creature) == null) {return createInvalidMove("The described creature does not exist");}
        if(!movementHandler.checkMove(board,toTile,fromTile,toPlace.name)){return createInvalidMove(movementHandler.getErrorLog());};


        plopTile(toTile,popTile(fromTile));
        moveNumber++;
        return createValidMove();
    }

    private int gameIsDone(){
        Collection<HexCoordinate> coordinates = board.keySet();
        int butterflysSurrounded = 0;
        for (HexCoordinate h: coordinates){
            CreatureName cn = board.get(h).name;
//            if(cn !=null)
            if(getCreatureDefinition(cn).properties().contains(CreatureProperty.QUEEN)){
                int amountOfNeighborsFilled = 0;
                for(HexCoordinate neighbor: h.neighbors()){
                    if(board.get(neighbor) !=null){
                        amountOfNeighborsFilled++;
                    }
                }
                if(amountOfNeighborsFilled == 6) butterflysSurrounded++;
            }
        }
        return butterflysSurrounded;

    }

    private Creature popTile(HexCoordinate fromTile){
        Creature toReturn = board.get(fromTile);
        board.put(fromTile,toReturn.linked);
        if(toReturn.linked==null){board.remove(fromTile);}
        return toReturn;
    }

    private void plopTile(HexCoordinate toTile, Creature toPlace){
        toPlace.linked = board.get(toTile);
        board.put(toTile,toPlace);
    }


}
