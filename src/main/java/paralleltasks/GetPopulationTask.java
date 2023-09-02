package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;

/*
   1) This class is the parallel version of the getPopulation() method from version 1 for use in version 2
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The double parameters(w, s, e, n) represent the bounds of the query rectangle
   4) The compute method returns an Integer representing the total population contained in the query rectangle
 */
public class GetPopulationTask extends RecursiveTask<Integer> {
    final static int SEQUENTIAL_CUTOFF = 1000;
    CensusGroup[] censusGroups;
    int lo, hi, row, col;
    double w, s, e, n;
    MapCorners grid;

    public GetPopulationTask(CensusGroup[] censusGroups, int lo, int hi, double w, double s,
                             double e, double n, MapCorners grid, int col, int row) {

        this.lo = lo;
        this.hi = hi;
        this.w = w;
        this.e = e;
        this.s = s;
        this.n = n;
        this.censusGroups = censusGroups;
        this.grid = grid;
        this.col = col;
        this.row = row;

    }


    // Returns a number for the total population
    @Override
    protected Integer compute() {
        if (hi-lo <= SEQUENTIAL_CUTOFF) {
            return sequentialGetPopulation(censusGroups, lo, hi, w, s, e, n);
        } else {
            GetPopulationTask left = new GetPopulationTask(censusGroups, lo, (lo + hi)/2, w, s, e, n, grid, col, row);
            GetPopulationTask right = new GetPopulationTask(censusGroups, (lo + hi)/2, hi, w, s, e, n, grid, col, row);
            left.fork();
            int r = right.compute();
            int l = left.join();
            return l + r;
        }
    }

    private Integer sequentialGetPopulation(CensusGroup[] censusGroups, int lo, int hi, double w,
                                            double s, double e, double n) {

        double one = 0;
        double two = 0;
        double leftF = ((grid.east-grid.west)) / col ;
        double bottomF = ((grid.north-grid.south)) / row;
        int popp = 0;
        for (int i = lo; i < hi; i++) {
            double val =  censusGroups[i].longitude + (-1.0)*grid.west;
            double val2 = censusGroups[i].latitude - grid.south;
            one = Math.floor(val/leftF);
            two = Math.floor(val2 / bottomF);
            if (include(two, one, w,s,e,n)) {
                popp += censusGroups[i].population;
            }
        }
        return popp;
    }

    private boolean include(double i, double j, double w, double s, double e, double n) {
        if (j >= w-1 && j < e  && i >= s-1 && i < n ) {
            return true;
        }

        // if its on the northern border
        if (n== row && j < e && j >= w-1  && i == n && i >= s -1) {
            return true;
        }
        //
        if (e== col && (i < n) && i >= s-1 && j == e&& j >= w-1) {
            return true;
        }
        if (e == col && n == row && j == e && i == n && j >= w-1 && i >= s-1) {
            return true;
        }
        return false;

    }
}
