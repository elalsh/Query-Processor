package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.Arrays;


public class ComplexSequential extends QueryResponder {
    MapCorners map;
    int col;
    int row;
    int[][] grid;
    int[][] dp;

    public ComplexSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        this.col = numColumns;
        this.row = numRows;
        map = new MapCorners(censusData[0]);
        grid = new int[numRows][numColumns];
        for (CensusGroup curr: censusData) {
            map = map.encompass(new MapCorners(curr));
        }
        double leftF = ((map.east-map.west)) /  numColumns;
        double bottomF = ((map.north-map.south)) / numRows;

        for (int i = 0; i < censusData.length; i++) {
            double val =  censusData[i].longitude + (-1.0)*map.west;
            double val2 = censusData[i].latitude - map.south;
            // get the row and col of each census group add their population
            double c = val/leftF;
            totalPopulation += censusData[i].population;
            double r = val2 / bottomF;

            int ii = (int)Math.floor(r);
            int jj = (int) Math.floor(c);

            // it's on the north eastern border (500, 100)
            if (ii == numRows && jj == numColumns) {
                grid[ii-1][jj-1] += censusData[i].population;
            } else if (ii == numRows) {
                grid[ii-1][jj] += censusData[i].population;

            } else if (jj == numColumns) {
                grid[ii][jj-1] += censusData[i].population;
            } else {
                    grid[ii][jj] += censusData[i].population;
            }
        }
        dp = new int[numRows+2][numColumns+2];
        for (int j = 1; j <= numColumns; j++) {
            for (int i = 1; i <= numRows; i++) {
                dp[i][j] = grid[i-1][j-1] + dp[i-1][j] + dp[i][j-1]-dp[i-1][j-1];
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (!checker(west, south, east, north)) {
            throw new IllegalArgumentException();
        } else {

            return dp[north][east]-dp[south-1][east]-dp[north][west-1]+dp[south-1][west-1];
        }

    }
    private boolean checker(int west, int south, int east, int north){
        if(west< 1 || west > this.col){
            return false;
        }
        if(south < 1 || south > row){
            return false;
        }
        if((east < west || east > this.col)){
            return false;
        }
        if((north < south|| north > row)){
            return false;
        }
        return true;
    }

}
