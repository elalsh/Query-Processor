package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;

/*
   1) This class will do the corner finding from version 1 in parallel for use in versions 2, 4, and 5
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The compute method returns a result of a MapCorners and an Integer.
        - The MapCorners will represent the extremes/bounds/corners of the entire land mass (latitude and longitude)
        - The Integer value should represent the total population contained inside the MapCorners
 */

public class CornerFindingTask extends RecursiveTask<CornerFindingResult> {
    final int SEQUENTIAL_CUTOFF = 10000;
    CensusGroup[] censusGroups;
    int lo, hi;

    public CornerFindingTask(CensusGroup[] censusGroups, int lo, int hi) {

        this.lo = lo;
        this.hi = hi;
        this.censusGroups = censusGroups;
    }

    // Returns a pair of MapCorners for the grid and Integer for the total population
    // Key = grid, Value = total population
    @Override
    protected CornerFindingResult compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialCornerFinding(censusGroups, lo, hi);
        } else {
            CornerFindingTask left = new CornerFindingTask(censusGroups, lo, (lo+hi)/2);
            CornerFindingTask right = new CornerFindingTask(censusGroups, (lo+hi)/2, hi);
            left.fork();
            CornerFindingResult r = right.compute();
            CornerFindingResult l = left.join();
            MapCorners ans = r.getMapCorners().encompass(l.getMapCorners());
            int res = l.getTotalPopulation() + r.getTotalPopulation();
            return (new CornerFindingResult(ans, res));
        }
    }

    private CornerFindingResult sequentialCornerFinding(CensusGroup[] censusGroups, int lo, int hi) {
        int pop = 0;
        MapCorners temp = new MapCorners(censusGroups[lo]);
        for (int i = lo; i < hi; i++) {
            pop += censusGroups[i].population;
            temp = temp.encompass(new MapCorners(censusGroups[i]));
        }
        return new CornerFindingResult(temp, pop);
    }
}

