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

package hexaround.game.BoardChecks;

import hexaround.config.CreatureDefinition;
import hexaround.game.Creature;
import hexaround.game.HexCoordinate;
import hexaround.required.CreatureName;
import hexaround.required.CreatureProperty;

import java.util.*;
import java.util.function.BiPredicate;

import static hexaround.game.HexAroundGame.getCreatureMaxMoveDistance;

public class CheckingHandlerInstance {

    private static final HashMap<CreatureName,BiPredicate<HashMap<HexCoordinate,Creature>,HexVector>> moveChecks = new HashMap<>();
    private StringBuilder errorLog = new StringBuilder();

    /**
     * @return the error log: To be used in case of an invalid move.
     */
    public String getErrorLog(){return errorLog.toString();}

    private void appendToError(String in){
        errorLog.append(in);
        errorLog.append("\n");
    };


    /**
     * @param definitionsIn
     * Builds the map of creature names to predicates for checkMoves to work later
     */
    public CheckingHandlerInstance(Collection<CreatureDefinition> definitionsIn){
        for(CreatureDefinition cd: definitionsIn){
            CreatureName mapCN = cd.name();
            Collection<CreatureProperty> props = cd.properties();
            if(props.contains(CreatureProperty.FLYING)){
                moveChecks.put(mapCN,moveFLYING);

            } else if (props.contains(CreatureProperty.WALKING) || props.contains(CreatureProperty.QUEEN)) {
                moveChecks.put(mapCN,moveWALKING);
            }
            else if (props.contains(CreatureProperty.RUNNING)) {
                moveChecks.put(mapCN,moveRUNNING);
            }
            else if (props.contains(CreatureProperty.JUMPING)) {
                moveChecks.put(mapCN,moveJUMPING);
            }
            else{
                moveChecks.put(mapCN,moveWALKING);
            }
        }
    }

    /**
     * Resets the error log
     */
    private void resetErrorLog(){errorLog = new StringBuilder();}


    /**
     * @param board
     * @param to
     * @param from
     * @param cn
     *
     * Given the parameters, returns a boolean whether the creature going is able to make the move [from,to] given its definition
     * @return
     */
    public boolean checkMove(HashMap<HexCoordinate,Creature> board,HexCoordinate to, HexCoordinate from, CreatureName cn){
        resetErrorLog();
        BiPredicate<HashMap<HexCoordinate, Creature>, HexVector> moveCheck = moveChecks.get(cn);
        if (moveCheck != null) {
            return moveCheck.test(board, new HexVector(to, from));
        }
        // Handle the case where no move check is defined for CreatureName cn
        return false;


    }
    private BiPredicate<HashMap<HexCoordinate,Creature>,HexVector> moveWALKING = (board, vector) ->{
        HashMap<HexCoordinate,Creature> boardCopy = (HashMap<HexCoordinate, Creature>) board.clone();
        Set<HexCoordinate> visited = new HashSet<>();
        Creature creatureOnTile = board.get(vector.from);
        if(creatureAtSpot(board,vector.to)) {
            appendToError("InvalidMove: Theres a creature where you are trying to move");return false;}
        boolean pathFound = walkSearch(vector.from, vector.to, getCreatureMaxMoveDistance(creatureOnTile), visited, boardCopy,creatureOnTile);
        if(!pathFound){appendToError("InvalidMove: Cannot find a valid path to that position");}

        return pathFound;
    };
    private  BiPredicate<HashMap<HexCoordinate,Creature>,HexVector> moveFLYING = (board, vector)  ->{
        Creature creatureOnTile = board.get(vector.from);
        int distanceMax = getCreatureMaxMoveDistance(creatureOnTile);
        if(creatureAtSpot(board,vector.to)) {
            appendToError("InvalidMove: Theres a creature where you are trying to move");return false;}
        if(vector.from.distanceTo(vector.to) > distanceMax) {
            appendToError("InvalidMove: That distance is too far to traverse");return false;}
        if(checkSurroundedness(board,vector.from)){
            appendToError("InvalidMove: Your creature is surrounded");return false;}

        HashMap<HexCoordinate,Creature> nextBoard = (HashMap<HexCoordinate, Creature>) board.clone();

        nextBoard.put(vector.to, creatureOnTile);
        nextBoard.remove(vector.from);
        boolean validMove = checkContinuity(nextBoard,vector);
        if(!validMove) {appendToError("InvalidMove: This breaks continuity");}
        return validMove;
    };
    private  BiPredicate<HashMap<HexCoordinate,Creature>,HexVector> moveJUMPING = (board, vector)  ->{
        Creature creatureOnTile = board.get(vector.from);
        if(creatureAtSpot(board,vector.to)) {appendToError("InvalidMove: Theres a creature where you are trying to move");return false;}
        if(!checkLinearity(vector)) {appendToError("InvalidMove: The vector you have inputted is not linear");return false;}

        HashMap<HexCoordinate,Creature> nextBoard = (HashMap<HexCoordinate, Creature>) board.clone();

        nextBoard.put(vector.to, creatureOnTile);
        nextBoard.remove(vector.from);
        boolean validMove = checkContinuity(nextBoard,vector);
        if(!validMove) {appendToError("InvalidMove: This breaks continuity");}
        return validMove;
    };
    private BiPredicate<HashMap<HexCoordinate,Creature>,HexVector> moveRUNNING = (board, vector)  ->{
        HashMap<HexCoordinate,Creature> boardCopy = (HashMap<HexCoordinate, Creature>) board.clone();
        Set<HexCoordinate> visited = new HashSet<>();
        Creature creatureOnTile = board.get(vector.from);
        if(creatureAtSpot(board,vector.to)) {
            appendToError("InvalidMove: Theres a creature where you are trying to move");return false;}
        boolean pathFound = runSearch(vector.from, vector.to, getCreatureMaxMoveDistance(creatureOnTile), visited, boardCopy,creatureOnTile);
        if(!pathFound){appendToError("InvalidMove: Cannot find a valid path to that position");}
        return pathFound;
    };

    private boolean checkContinuity (HashMap<HexCoordinate,Creature> board, HexVector vectorIn){
        if (board.isEmpty()) {
            return true;
        }

        Set<HexCoordinate> visited = new HashSet<>();
        HexCoordinate startCoord = board.keySet().iterator().next(); // Start from any coordinate

        dfsForContinuity(board, startCoord, visited);

        // Check if all coordinates have been visited
        return visited.size() == board.size();

    }

    private void dfsForContinuity(HashMap<HexCoordinate, Creature> board, HexCoordinate current, Set<HexCoordinate> visited) {
        if (visited.contains(current) || !board.containsKey(current)) {
            return;
        }

        visited.add(current);

        for (HexCoordinate neighbor : current.neighbors()) {
            dfsForContinuity(board, neighbor, visited);
        }
    }

    private boolean checkDragabilityGivenTwoNeighbors(HashMap<HexCoordinate,Creature> board, HexVector vectorIn){
        Collection<HexCoordinate> toCheck =  vectorIn.to.neighbors();
        toCheck.retainAll(vectorIn.from.neighbors());
        for(HexCoordinate edgeCheck: toCheck){
            if(board.get(edgeCheck) == null){ return true;}
        }
        return false;
    }


    private boolean creatureAtSpot(HashMap<HexCoordinate,Creature> board, HexCoordinate coord){
        boolean toReturn =  board.get(coord) !=null;
//        if (!toReturn){appendToError("INVALID MOVE: There is a creature at the spot");};
        return toReturn;
    }
    private boolean checkSurroundedness (HashMap<HexCoordinate,Creature> board, HexCoordinate coord){


        int neighbors = 6;
        for(HexCoordinate neighbor: coord.neighbors()){
            if(board.get(neighbor) != null){
                neighbors--;
            }
        }
        boolean toReturn = neighbors == 0;
//        if (!toReturn){appendToError("INVALID MOVE: The creature is surrounded");};
        return toReturn;
    }
    private boolean checkLinearity(HexVector vector)
    {
        boolean toReturn = vector.from.isLinear(vector.to);
//        if (!toReturn){appendToError("INVALID MOVE: This vector is not linear");};
        return toReturn;
    }


    private boolean walkSearch(HexCoordinate current, HexCoordinate destination, int distanceTraversed, Set<HexCoordinate> visited, HashMap<HexCoordinate, Creature> board, Creature movedCreature) {


        if(board.get(current) != null && board.get(current) != movedCreature) {return false;}
        if(!checkContinuity(board,new HexVector(current,destination))){return false;}


        if (current.equals(destination)) {return true;}

        if(distanceTraversed == 0) {return false;}

        visited.add(current);

        for (HexCoordinate neighbor : current.neighbors()) {
            if (!visited.contains(neighbor)) {
                if(checkDragabilityGivenTwoNeighbors(board,new HexVector(current,neighbor))) {
                    HashMap<HexCoordinate, Creature> nextBoard = (HashMap<HexCoordinate, Creature>) board.clone();
                    nextBoard.put(neighbor, movedCreature);
                    nextBoard.remove(current);
                    if (walkSearch(neighbor, destination, distanceTraversed - 1, visited, (nextBoard), movedCreature)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private boolean runSearch(HexCoordinate current, HexCoordinate destination, int distanceTraversed, Set<HexCoordinate> visited, HashMap<HexCoordinate, Creature> board, Creature movedCreature) {

        if(board.get(current) == null  && board.get(current) != movedCreature) {return false;}
        if(!checkContinuity(board,new HexVector(current,destination))){return false;}

        if (current.equals(destination)) {
            return distanceTraversed == 0;
        }
        visited.add(current);
        if(distanceTraversed == 0) {return false;}

        for (HexCoordinate neighbor : current.neighbors()) {
            if (!visited.contains(neighbor)) {
                if(checkDragabilityGivenTwoNeighbors(board,new HexVector(current,neighbor))) {
                    HashMap<HexCoordinate, Creature> nextBoard = (HashMap<HexCoordinate, Creature>) board.clone();
                    nextBoard.put(neighbor, movedCreature);
                    nextBoard.remove(current);
                    if (runSearch(neighbor, destination, distanceTraversed - 1, visited, (nextBoard), movedCreature)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }






    private class HexVector{
        protected HexCoordinate to;
        protected HexCoordinate from;

        public HexVector(HexCoordinate to, HexCoordinate from) {
            this.to = to;
            this.from = from;
        }
    }





}
