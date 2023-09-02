package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/*
   1) This class is used in version 4 to create the initial grid holding the total population for each grid cell
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) Note that merging the grids from the left and right subtasks should NOT be done in this class.
      You will need to implement the merging in parallel using a separate parallel class (MergeGridTask.java)
 */

public class PopulateGridTask extends RecursiveTask<int[][]> {
    final static int SEQUENTIAL_CUTOFF = 10000;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;

    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners, double cellWidth, double cellHeight) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;

    }

    @Override
    protected int[][] compute() {
        if (hi- lo <= SEQUENTIAL_CUTOFF) {
            return sequentialPopulateGrid(lo, hi);
        } else {
            PopulateGridTask left = new PopulateGridTask(censusGroups, lo, (lo+hi)/2, numRows, numColumns, corners, cellWidth, cellHeight);
            PopulateGridTask right = new PopulateGridTask(censusGroups, (lo+hi)/2, hi, numRows, numColumns, corners, cellWidth, cellHeight);
            left.fork();
            int[][] r = right.compute();
            int[][] l = left.join();
           POOL.invoke(new MergeGridTask(l, r, 0, numRows, 0, numColumns));
           return l;
        }
    }

    private int[][] sequentialPopulateGrid(int lo, int hi) {
        int[][] grid = new int[numRows][numColumns];
        for (int i = lo; i < hi; i++) {
            double val = censusGroups[i].longitude + (-1.0) * corners.west;
            double val2 = censusGroups[i].latitude - corners.south;
            // get the row and col of each census group add their population
            double c = val / cellWidth;

            double r = val2 / cellHeight;

            int ii = (int)Math.floor(r);
            int jj = (int) Math.floor(c);

            // it's on the north eastern border (500, 100)
            if (ii == numRows && jj == numColumns) {
                grid[ii-1][jj-1] += censusGroups[i].population;
            } else if (ii == numRows) {
                grid[ii-1][jj] += censusGroups[i].population;

            } else if (jj == numColumns) {
                grid[ii][jj-1] += censusGroups[i].population;
            } else {
                grid[ii][jj] += censusGroups[i].population;
            }

        }
        return grid;
    }
}

