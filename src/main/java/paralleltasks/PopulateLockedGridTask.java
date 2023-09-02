package paralleltasks;

import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.locks.Lock;

/*
   1) This class is used in version 5 to create the initial grid holding the total population for each grid cell
        - You should not be using the ForkJoin framework but instead should make use of threads and locks
        - Note: the resulting grid after all threads have finished running should be the same as the final grid from
          PopulateGridTask.java
 */

public class PopulateLockedGridTask extends Thread {
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;


    Lock[][] ls;
    int[][] arrGrid;
   /* Lock[][] lockGrid;*/

    Lock t;

    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners,
                                  double cellWidth, double cellHeight, Lock [][]ls, int[][] popGr,Lock t) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.ls = ls;
        arrGrid = popGr;
        this.t = t;
    }

    @Override
    public void run() {

        for(int i = lo; i < hi;i++){
            double val = (censusGroups[i].longitude + (-1.0) * corners.west);
            double val2 = censusGroups[i].latitude - corners.south;
            // get the row and col of each census group add their population
            double c = val / cellWidth;

            double r = val2 / cellHeight;

            int ii = (int)Math.floor(r);
            int jj = (int) Math.floor(c);

            // it's on the north eastern border (500, 100)
            if (ii == numRows && jj == numColumns) {
                ls[ii-1][jj-1].lock();
                try {
                    arrGrid[ii-1][jj-1] += censusGroups[i].population;
                } finally {
                    // t.unlock();
                    ls[ii-1][jj-1].unlock();
                }
            } else if (ii == numRows) {
                ls[ii-1][jj].lock();
                try {
                    arrGrid[ii-1][jj] += censusGroups[i].population;
                } finally {
                    // t.unlock();
                    ls[ii-1][jj].unlock();
                }
            } else if (jj == numColumns) {
                ls[ii][jj-1].lock();
                try {
                    arrGrid[ii][jj-1] += censusGroups[i].population;
                } finally {
                    // t.unlock();
                    ls[ii][jj-1].unlock();
                }
            } else {
                ls[ii][jj].lock();
                try {
                    arrGrid[ii][jj] += censusGroups[i].population;
                } finally {
                    // t.unlock();
                    ls[ii][jj].unlock();
                }
            }

        }
    }
}
