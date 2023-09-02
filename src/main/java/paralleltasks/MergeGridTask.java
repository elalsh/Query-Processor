package paralleltasks;

import cse332.exceptions.NotYetImplementedException;

import java.util.concurrent.RecursiveAction;

/*
   1) This class is used by PopulateGridTask to merge two grids in parallel
   2) SEQUENTIAL_CUTOFF refers to the maximum number of grid cells that should be processed by a single parallel task
 */

public class MergeGridTask extends RecursiveAction {
    final static int SEQUENTIAL_CUTOFF = 10;
    int[][] left, right;
    int rowLo, rowHi, colLo, colHi;

    public MergeGridTask(int[][] left, int[][] right, int rowLo, int rowHi, int colLo, int colHi) {
        this.left = left;
        this.right = right;
        this.rowLo = rowLo;
        this.rowHi = rowHi;
        this.colLo = colLo;
        this.colHi = colHi;

    }

    @Override
    protected void compute() {
        if ((colHi-colLo)*(rowHi-rowLo) <= SEQUENTIAL_CUTOFF) {
            sequentialMergeGird(rowLo, rowHi, colLo, colHi);
        } else {
            MergeGridTask l = new MergeGridTask(left, right, rowLo, (rowLo+rowHi)/2,colLo, (colLo+colHi)/2);
            MergeGridTask r = new MergeGridTask(left, right,(rowLo+rowHi)/2, rowHi,colLo, (colLo+colHi)/2);
            MergeGridTask ll = new MergeGridTask(left, right, rowLo, (rowLo+rowHi)/2,(colLo+colHi)/2, colHi);
            MergeGridTask rr = new MergeGridTask(left, right, (rowLo+rowHi)/2, rowHi,(colLo+colHi)/2, colHi);
            l.fork();

            r.compute();

            ll.compute();

            rr.compute();


            l.join();
        }

    }

    // according to google gird means "prepare oneself for something difficult or challenging" so this typo is intentional :)
    private void sequentialMergeGird(int rowLo, int rowHi, int colLo, int colHi) {
        for (int i = rowLo; i < rowHi; i++) {
            for (int j = colLo; j < colHi; j++) {
                left[i][j] = left[i][j] + right[i][j];
            }
        }
    }
}
