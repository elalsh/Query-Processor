package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateLockedGridTask;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ComplexLockBased extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool(); // only to invoke CornerFindingTask
    public int NUM_THREADS = 4;
    private int col;
    private int row;


    private MapCorners map;



    private int[][] popGrid;
    private Lock[][] ls;
    private int[][] ddp;

    private Lock t;


    public ComplexLockBased(CensusGroup[] censusData, int numColumns, int numRows) {

        //grid = new lockNode[numRows][numColumns];
        popGrid = new int[numRows][numColumns];
        this.col = numColumns;
        this.row = numRows;
        CornerFindingResult temp = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        map = temp.getMapCorners();
        totalPopulation += temp.getTotalPopulation();
        double leftF = ((map.east-map.west)) /  numColumns;
        double bottomF = ((map.north-map.south)) / numRows;

        PopulateLockedGridTask[] ts = new PopulateLockedGridTask[NUM_THREADS];


        ls = new Lock[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                ls[i][j] = new ReentrantLock();
            }
        }

            t = new ReentrantLock();

        //@@
        for(int i = 0; i < NUM_THREADS; i++){
            int constant = (censusData.length*i)/(NUM_THREADS);
            int high = (censusData.length*(i+1))/(NUM_THREADS);
            ts[i] = new PopulateLockedGridTask(censusData,constant,high,numRows,numColumns,map,leftF,bottomF, ls, popGrid,t);
            ts[i].start();
        }
        for(int i = 0; i < ts.length; i++){
           try {
               ts[i].join();
           } catch(Exception e){
              throw new RuntimeException();
           }
        }
        ddp = new int[numRows+2][numColumns+2];
        for (int j = 1; j <= numColumns; j++) {
            for (int i = 1; i <= numRows; i++) {

                 ddp[i][j] = popGrid[i-1][j-1] + ddp[i-1][j] + ddp[i][j-1]-ddp[i-1][j-1];
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {

        if (!checker(west, south, east, north)) {
            throw new IllegalArgumentException();
        } else {
            return ddp[north][east]-ddp[south-1][east]-ddp[north][west-1]+ddp[south-1][west-1];
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
