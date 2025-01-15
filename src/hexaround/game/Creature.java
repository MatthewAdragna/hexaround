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
import hexaround.required.PlayerName;

import java.util.ArrayList;

public class Creature{
    public CreatureName name;
    protected PlayerName player;
//    protected ArrayList<ICreatureAttribute> attributes = new ArrayList<>();
    protected Creature linked;
   public Creature(CreatureName name, PlayerName player) {
        this.name = name;
        this.player = player;
        this.linked = null;
    }

//    public Creature(CreatureName name, PlayerName player,Creature linked) { // this was in the case of
//        this.name = name;
//        this.player = player;
//        this.linked = linked;
//    }
}