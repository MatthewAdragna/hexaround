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

import hexaround.config.CoordinateException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * This class represents a standard Hex coordinate. It
 * currently only has a default constructor. You should
 * need a constructor that takes the two axis values (x, y).
 */
public class HexCoordinate{
    static protected int[][] adjacentValues = {{-1,0},{-1,1},{0,1},{1,0},{1,-1},{0,-1}};
    protected int x, y;
    /**
     * Constructor for a HexCoordinate
     * @param x
     * @param y
     * Creates a HexCoordinate of coordinate {x,y}
     * */
    public HexCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Computes the distance to another coordinate. Distance
     * is calculated based upon the type of coordinate and how it
     * is defined for the specific game type.
     * @param otherCoordinate
     * @return the distance to the other coordinate.
     */

    public int distanceTo(HexCoordinate otherCoordinate) {
        if(otherCoordinate == null){throw new CoordinateException("IsLinear: otherCoordinate parameter is Null | Cannot compute");}

        int otherX = ((HexCoordinate)otherCoordinate).getX(), otherY = ((HexCoordinate)otherCoordinate).getY();

        int otherZ = -1 * (otherX + otherY), thisZ = -1 * (this.getX() + this.getY());

        return (Math.abs(this.getX() - otherX) + Math.abs(this.getY() - otherY) + Math.abs(thisZ - otherZ))/2;
    }



    /**
     * @param otherCoordinate
     * @return true if the other coordinate is in a straight
     * line (defined by the coordinate type)
     */

    public boolean isLinear(HexCoordinate otherCoordinate) {
        if(otherCoordinate == null){throw new CoordinateException("IsLinear: otherCoordinate parameter is Null | Cannot compute");}

        int otherX = (otherCoordinate).getX();
        int otherY = (otherCoordinate).getY();

        int relativeX = this.getX() - otherX;
        int relativeY = this.getY() - otherY;
        // {-1,0},{0,1},{1,0},{0,-1}
        // {-1,1},{1,-1}

        if( (-1 * relativeY) == relativeX) return true; // Cases of following linear paths: {-1,1},{1,-1})
        if(Math.abs(relativeY) == 0 || Math.abs(relativeX) == 0) return true; // Cases of following linear paths: {-1,0},{0,1},{1,0},{0,-1} (Would also return linear if both coordinates are the same)

        return false;








    }

    /**
     * @return a collection of all of the coordinates that
     * are neighbors (usually adjacent coordinates) of this
     * coordinate
     */

    public Collection<HexCoordinate> neighbors() {
        ArrayList<HexCoordinate> hexCoordCollection = new ArrayList<>();
        for(int[] offset : adjacentValues){
            hexCoordCollection.add(new HexCoordinate(this.getX() + offset[0],this.getY() + offset[1]));
        }
        return hexCoordCollection;
    }

    /**
     * @return x field of the Hex Coordinate
     */

    public int getX() {
        return x;
    }


    /**
     * @return y field of the Hex Coordinate
     */

    public int getY() {
        return y;
    }


    /**
     * Returns a hash code value for this HexCoordinate
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexCoordinate that = (HexCoordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
