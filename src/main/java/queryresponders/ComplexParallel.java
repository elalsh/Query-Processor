package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class ComplexParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();

    MapCorners map;
    int[][] dp;
    int[][] grid;
    int col, row;
    public ComplexParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        this.col = numColumns;
        this.row = numRows;
        CornerFindingResult temp = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        map = temp.getMapCorners();
        totalPopulation += temp.getTotalPopulation();
        double leftF = ((map.east-map.west)) /  numColumns;
        double bottomF = ((map.north-map.south)) / numRows;
        grid  = POOL.invoke(new PopulateGridTask(censusData, 0, censusData.length, numRows, numColumns, map,leftF, bottomF));
        dp = new int[numRows+2][numColumns+2];
        for (int j = 1; j <= numColumns; j++) {
            for (int i = 1; i <= numRows; i++) {
                dp[i][j] = grid[i-1][j-1] + dp[i-1][j] + dp[i][j-1]-dp[i-1][j-1];
            }
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
    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (!checker(west, south, east, north)) {
            throw new IllegalArgumentException();
        } else {

            return dp[north][east]-dp[south-1][east]-dp[north][west-1]+dp[south-1][west-1];
        }
    }
}
