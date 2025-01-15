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

import hexaround.required.CreatureName;
import hexaround.required.GameState;
import hexaround.required.MoveResult;
import hexaround.required.PlayerName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Stream;

import static hexaround.required.CreatureName.*;
import static hexaround.required.CreatureName.RABBIT;
import static hexaround.required.MoveResult.MOVE_ERROR;
import static hexaround.required.MoveResult.OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class GameTests {


    @Test
    void testRunning() throws IOException {
        String hgcFile = "testConfigurations/testRunning.hgc";
        IHexAroundGameManager manager2 = HexAroundGameBuilder.buildGameManager(hgcFile);
        HexAroundGame game = (HexAroundGame) manager2;
        HashMap<HexCoordinate, Creature> board = createTestBoard();
        game.modifyForTests(board,7,new boolean[]{true,true});
        GameState state;

        state = game.placeCreature(RABBIT,2,-2); //Placing one down
        assertEquals(OK, state.moveResult());

        state = game.placeCreature(RABBIT,-2,2); //Placing the other
        assertEquals(OK, state.moveResult());

        state = game.moveCreature(RABBIT,2,-2,-2,0); //too short
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.moveCreature(RABBIT,2,-2,-3,3); //too far
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.moveCreature(RABBIT,2,-2,-2,1); // just right
        assertEquals(OK, state.moveResult());
    }


    @Test
    void testWalking() throws IOException {
        String hgcFile = "testConfigurations/testWalking.hgc";
        IHexAroundGameManager manager2 = HexAroundGameBuilder.buildGameManager(hgcFile);
        HexAroundGame game = (HexAroundGame) manager2;
        HashMap<HexCoordinate, Creature> board = createTestBoard();
        game.modifyForTests(board,7,new boolean[]{true,true});
        GameState state;

        state = game.placeCreature(RABBIT,2,-2); //placing one
        assertEquals(OK, state.moveResult());

        state = game.placeCreature(RABBIT,-2,2); //placing the other
        assertEquals(OK, state.moveResult());

        state = game.moveCreature(RABBIT,2,-2,-2,0); // short - which works on walk
        assertEquals(OK, state.moveResult());

        state = game.moveCreature(RABBIT,-2,2,-3,-3); // too far
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.moveCreature(RABBIT,-2,0,2,-1); // At the limit
        assertEquals(OK, state.moveResult());
    }


    @Test
    void testConnectedness() throws IOException {
        String hgcFile = "testConfigurations/testConnectedness.hgc";
        IHexAroundGameManager manager2 = HexAroundGameBuilder.buildGameManager(hgcFile);
        HexAroundGame game = (HexAroundGame) manager2;
        HashMap<HexCoordinate, Creature> board = createTestBoard();
        board.put(new HexCoordinate(2,0), new Creature(DUCK, PlayerName.RED));
        board.put(new HexCoordinate(3,0), new Creature(DUCK, PlayerName.BLUE));
        game.modifyForTests(board,7,new boolean[]{true,true});
        GameState state;

        state = game.moveCreature(DUCK,1,0,0,1); //placing one
        assertEquals(MOVE_ERROR, state.moveResult());
    }

    @Test
    void testFirstTwo() throws IOException {
        String hgcFile = "testConfigurations/testFirstTwo.hgc";
        IHexAroundGameManager manager2 = HexAroundGameBuilder.buildGameManager(hgcFile);
        HexAroundGame game = (HexAroundGame) manager2;
        GameState state;

        state = game.placeCreature(DUCK,0,1); //placing not on origin
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.placeCreature(DUCK,0,0); //placing on origin
        assertEquals(OK, state.moveResult());


        state = game.placeCreature(DUCK,2,1); //placing not next to origin
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.placeCreature(DUCK,0,1); //placing next to origin
        assertEquals(OK, state.moveResult());


    }




    @Test
    void testFlying() throws IOException {
        String hgcFile = "testConfigurations/testFlying.hgc";
        IHexAroundGameManager manager2 = HexAroundGameBuilder.buildGameManager(hgcFile);
        HexAroundGame game = (HexAroundGame) manager2;
        HashMap<HexCoordinate, Creature> board = createTestBoard();
        game.modifyForTests(board,7,new boolean[]{true,true});
        GameState state;

        state = game.placeCreature(RABBIT,2,-2); //placing one
        assertEquals(OK, state.moveResult());

        state = game.placeCreature(RABBIT,-2,2); //placing the other
        assertEquals(OK, state.moveResult());

        state = game.moveCreature(RABBIT,2,-2,-2,0); // farthest possible
        assertEquals(OK, state.moveResult());

        state = game.moveCreature(RABBIT,-2,2,-3,-3); // too far by 1
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.moveCreature(RABBIT,-2,2,2,-2); // perfecto
        assertEquals(OK, state.moveResult());
    }



    @Test
    void testJumping() throws IOException {
        String hgcFile = "testConfigurations/testJumping.hgc";
        IHexAroundGameManager manager2 = HexAroundGameBuilder.buildGameManager(hgcFile);
        HexAroundGame game = (HexAroundGame) manager2;
        HashMap<HexCoordinate, Creature> board = createTestBoard();
        game.modifyForTests(board,7,new boolean[]{true,true});
        GameState state;

        state = game.placeCreature(RABBIT,2,-2); //placing one
        assertEquals(OK, state.moveResult());

        state = game.placeCreature(RABBIT,-2,2); //placing the other
        assertEquals(OK, state.moveResult());

        state = game.moveCreature(RABBIT,2,-2,2,0); // small lil jump
        assertEquals(OK, state.moveResult());

        state = game.moveCreature(RABBIT,-2,2,-3,-3); // too far by 1
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.moveCreature(RABBIT,-2,2,-1,-1); // not linear
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.moveCreature(RABBIT,-2,2,2,-2); // perfecto
        assertEquals(OK, state.moveResult());
    }



    @Test
    void butterflyTests() throws IOException { // Dragability, win and draw, butterfly placement
        String hgcFile = "testConfigurations/testConfig.hgc";
        IHexAroundGameManager manager2 = HexAroundGameBuilder.buildGameManager(hgcFile);
        HexAroundGame game = (HexAroundGame) manager2;
        HashMap<HexCoordinate, Creature> board = createTestBoard();
        game.modifyForTests(board,7,new boolean[]{true,true});
        GameState state;

        state = game.moveCreature(BUTTERFLY,0,0,0,1); //Dragability test
        assertEquals(MOVE_ERROR, state.moveResult());

        board.remove(new HexCoordinate(0,0));
        game.modifyForTests(board,7,new boolean[]{false,true});

        state = game.moveCreature(RABBIT,1,0,2,-1); //Seeing if im forced to place one
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.placeCreature(BUTTERFLY,0,0); //Can't place next to an enemy
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.placeCreature(BUTTERFLY,2,-2); // finally place
        assertEquals(OK, state.moveResult());


        HashMap<HexCoordinate, Creature> boardEmpty = new HashMap<HexCoordinate, Creature>();
        game.modifyForTests(boardEmpty,0,new boolean[]{false,false});

        state = game.placeCreature(DUCK,0,1);
        assertEquals(MOVE_ERROR, state.moveResult()); // Checking first move has to

        state = game.placeCreature(DUCK,0,0); // place it on origin
        assertEquals(OK, state.moveResult());

        state = game.placeCreature(DUCK,2,0); // second has to touch origin
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.placeCreature(DUCK,0,1); // this one is touching origin
        assertEquals(OK, state.moveResult());

        game.modifyForTests(boardEmpty,8,new boolean[]{false,false});

        state = game.moveCreature(DUCK,0,0,-1,1); // last turn, have to put down butterfly
        assertEquals(MOVE_ERROR, state.moveResult());

        state = game.placeCreature(RABBIT,-1,1); // last turn, have to put down butterfly
        assertEquals(MOVE_ERROR, state.moveResult());


        state = game.placeCreature(BUTTERFLY,-1,0); // if it is not touching an enemy, it is fine
        assertEquals(OK, state.moveResult());

        state = game.moveCreature(DUCK,0,1,-1,1); // same thing as above, just for other player
        assertEquals(MOVE_ERROR, state.moveResult());


    }


    @Test
    void winDrawTests() throws IOException {
        String hgcFile = "testConfigurations/testWinDraw.hgc";
        IHexAroundGameManager manager2 = HexAroundGameBuilder.buildGameManager(hgcFile);
        HexAroundGame game = (HexAroundGame) manager2;
        HashMap<HexCoordinate, Creature> board = createTestBoard();
        game.modifyForTests(board,7,new boolean[]{true,true});

        board.put(new HexCoordinate(0,1),new Creature(DUCK, PlayerName.RED));



        GameState state;
        state = game.moveCreature(BUTTERFLY,0,0,0,1); // IS not allowed due to the game being won
        assertEquals(MOVE_ERROR, state.moveResult());

        boolean isBlue = true;
        for(HexCoordinate neighbors: new HexCoordinate(0,-1).neighbors()){
            board.put(neighbors,new Creature(DUCK, isBlue ? PlayerName.BLUE: PlayerName.RED));
            isBlue = !isBlue;
        }
        board.put(new HexCoordinate(0,0),new Creature(BUTTERFLY, PlayerName.RED)); // I erased the previous one :) with the loop above
        state = game.moveCreature(BUTTERFLY,0,0,0,1); // IS not allowed due to the game being drawn
        assertEquals(MOVE_ERROR, state.moveResult());

    }



    private static HashMap<HexCoordinate, Creature> createTestBoard(){
        HashMap<HexCoordinate, Creature> board = new HashMap<>();
        boolean isBlue = true;
        for(HexCoordinate neighbors: new HexCoordinate(0,0).neighbors()){
            board.put(neighbors,new Creature(DUCK, isBlue ? PlayerName.BLUE: PlayerName.RED));
            isBlue = !isBlue;
        }
        board.put(new HexCoordinate(0,0),new Creature(BUTTERFLY, isBlue ? PlayerName.BLUE: PlayerName.RED));
        isBlue = !isBlue;
        board.put(new HexCoordinate(0,-1),new Creature(BUTTERFLY, isBlue ? PlayerName.BLUE: PlayerName.RED));
        board.remove(new HexCoordinate(0,1));
        return board;
    }


    private static IHexAroundGameManager manager;

    @BeforeAll
    public static void setup () throws IOException {
        manager = HexAroundGameBuilder.buildGameManager("testConfigurations/testConfig.hgc");
    }



    @ParameterizedTest
    @MethodSource("placeProvider")
    void testPlacing(CreatureName creature, MoveResult result, int x, int y) {
        GameState state = manager.placeCreature(creature, x, y);
        assertEquals(result, state.moveResult());
        if (result != OK) {
            assertNotNull(state.message());
        } else {
            assertNull(state.message());
        }
    }



    static Stream<Arguments> placeProvider() {
        return Stream.of(
                arguments(DUCK, MOVE_ERROR, 1, 0),//
                arguments(DUCK, OK, 0, 0), //
                arguments(DUCK, MOVE_ERROR, 0, 0),//
                arguments(DUCK, OK, 0, 1),
                arguments(RABBIT, MOVE_ERROR, 0, 1),
                arguments(DUCK, MOVE_ERROR, 0, 2),
                arguments(RABBIT, MOVE_ERROR, 15, 24),
                arguments(HORSE, MOVE_ERROR, 5, 42)
        );
    }

    @Test
    void testMovingFunctionality() throws IOException {
        IHexAroundGameManager manager2 = HexAroundGameBuilder.buildGameManager("testConfigurations/testConfig.hgc");

        GameState state = manager2.placeCreature(DUCK, 0, 0); // PLACING a DUCK INITIALLY
        assertEquals(OK, state.moveResult());
        assertNull(state.message());

        state = manager2.placeCreature(DUCK, 0, 1);
        assertEquals(OK, state.moveResult());
        assertNull(state.message());

        state = manager2.moveCreature(DUCK, 0, 0,1,1); // MOVING NORMALLY
        assertEquals(OK, state.moveResult());

        state = manager2.moveCreature(DUCK, 1, 1,1,1); // MOVING TO THE SAME SPOT -> SHOULD FAIL
        assertEquals(MOVE_ERROR, state.moveResult());

        state = manager2.moveCreature(DUCK, 0, 0,1,0); // MOVING WITHOUT ANYTHING BEING THERE -> SHOULD FAIL
        assertEquals(MOVE_ERROR, state.moveResult());

        state = manager2.placeCreature(RABBIT, 0, 0); // PLACING ANOTHER PIECE
        assertEquals(OK, state.moveResult());
        assertNull(state.message());

        state = manager2.moveCreature(DUCK, 1, 0,0,0); // MOVING TO THE SAME SPOT AS ANOTHER CREATURE -> SHOULD FAIL
        assertEquals(MOVE_ERROR, state.moveResult());

        state = manager2.moveCreature(HORSE, 1, 0,2,2); // NOT THE RIGHT CREATURE -> SHOULD FAIL
        assertEquals(MOVE_ERROR, state.moveResult());

        state = manager2.moveCreature(RABBIT, 0, 0,15,15); // TOO FAR -> SHOULD FAIL
        assertEquals(MOVE_ERROR, state.moveResult());

        state = manager2.moveCreature(RABBIT, 0, 0,2,1); // TESTING RUNNING CREATURE
        assertEquals(OK, state.moveResult());


    }


}
