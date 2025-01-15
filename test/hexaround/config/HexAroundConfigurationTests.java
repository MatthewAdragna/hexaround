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

package hexaround.config;





import hexaround.game.*;
import hexaround.required.CreatureName;
import hexaround.required.GameState;
import hexaround.required.MoveResult;
import hexaround.required.PlayerName;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.HashMap;
import java.util.stream.Stream;

import static hexaround.required.CreatureName.*;
import static hexaround.required.MoveResult.MOVE_ERROR;
import static hexaround.required.MoveResult.OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class HexAroundConfigurationTests {
    @Test
    void simpleTest() throws IOException {
        String hgcFile = "testConfigurations/FirstConfiguration.hgc";
        HexAroundConfigurationMaker maker = new HexAroundConfigurationMaker(hgcFile);
        GameConfiguration gc = maker.makeConfiguration();
        assertTrue(true);
        System.out.println(gc.toString());
    }


}
