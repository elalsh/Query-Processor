package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusData;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.Arrays;

// simple sequential
public class SimpleSequential extends QueryResponder {
    MapCorners map;
    int colums;
    int rows;

    CensusGroup[] censusData;
    double [][] cells;


    /**
     * Makes the big Rectangle the encompasses the whole earth
     * and maps each group to a location?
     * @param censusData
     * @param numColumns
     * @param numRows
     */
    public SimpleSequential(CensusGroup[] censusData, int numColumns, int numRows) {

        colums = numColumns;
        rows = numRows;

        this.censusData = censusData;

        //create the map with the corners and data
        cells = new double [censusData.length][2];
        map = new MapCorners(censusData[0]);
        for (CensusGroup curr: censusData) {
            map = map.encompass(new MapCorners(curr));
            totalPopulation += curr.population;
        }

        // Compute the grid cells for all    CensusGroups

        double leftF = ((map.east-map.west)) /  numColumns;
        double bottomF = ((map.north-map.south)) / numRows;

        for (int i = 0; i < censusData.length; i++) {
            double val =  censusData[i].longitude + (-1.0)*map.west;
            double val2 = censusData[i].latitude - map.south;

            double c = val/leftF;
            double r = val2 / bottomF;

                    cells[i][1] = Math.floor(c);

                    cells[i][0] = Math.floor(r);
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {

        if(!checker(west,south,east,north)){
            throw new IllegalArgumentException("One of your Coordinates are incorrect");
        } else {
            //Check if group is in small rectangle

            int pop = 0;
            for (int i = 0; i < censusData.length; i++) {
                if (include(cells[i][0], cells[i][1], west, east, south, north)) {
                    pop += censusData[i].population;
                }
            }

            return pop;
        }
    }
    private boolean checker(int west, int south, int east, int north){
        if(west< 1 || west > this.colums){
            return false;
        }
        if(south < 1 || south > rows){
            return false;
        }
        if((east < west || east > this.colums)){
            return false;
        }
        if((north < south|| north > rows)){
            return false;
        }
        return true;
    }

    /**
     * Checks to see if group is included in the given small rectangle
     //* @param group The group that is currently being looked at
     * @param west the west cord of the group
     * @param south the south cord of the group
     * @param east the east coordinate of the group
     * @param north the north coordinate of the group
     * @return true if group is in given rectangle
     */

    private boolean include(double i, double j, int west, int east, int south, int north) {
        //the group is inside our small rectangle (west and south border are okay)

        if (j >= west-1 && j < east  && i >= south-1 && i < north ) {
            return true;
        }

        // if its on the northern border
        if (north == rows && j < east && j >= west-1  && i == north && i >= south -1) {
            return true;
        }
        //
        if (east == colums && (i < north) && i >= south-1 && j == east && j >= west-1) {
            return true;
        }
        if (east == colums && north == rows && j == east && i == north && j >= west-1 && i >= south-1) {
            return true;
        }
        return false;
    }

}
