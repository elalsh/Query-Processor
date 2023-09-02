package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.GetPopulationTask;

import java.util.concurrent.ForkJoinPool;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    MapCorners temp;
    CensusGroup[] censusData;
    int row, col;
    public SimpleParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        row = numRows;
        col = numColumns;
        this.censusData = censusData;
        CornerFindingResult ans = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        temp = ans.getMapCorners();
        totalPopulation = ans.getTotalPopulation();
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (!checker(west, south, east, north)) {
            throw new IllegalArgumentException();
        }
        return POOL.invoke(new GetPopulationTask(censusData, 0, censusData.length, west, south, east, north, temp, col, row));
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
