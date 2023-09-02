package experiment;

import cse332.types.CensusGroup;
import main.PopulationQuery;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import queryresponders.ComplexSequential;
import cse332.types.CensusGroup;
import cse332.interfaces.QueryResponder;
import queryresponders.SimpleParallel;
import queryresponders.SimpleSequential;

import java.util.Random;

public class ExperimentMain {
    double totalTime = 0;
    static CensusGroup[] data;
    ComplexSequential comple;
    SimpleSequential simp;
    ComplexSequential test;
    SimpleSequential te;
    public int[]  randomQuarry(int amount){
        Random ye = new Random();
        int[] ret = new int[4*amount];
        for(int i = 0; i < amount; i++) {
            // upperbound-lowerbound) + lowerbound
            ret[i*4] = ye.nextInt(98) + 1;
            ret[i*4+1] = ye.nextInt(498) + 1;
            ret[i*4+2] = ye.nextInt(99 - ret[i*4]) + ret[i*4];
            ret[i*4+3] = ye.nextInt(499 - ret[i*4+1]) + ret[i*4+1];
        }
        return ret;
    }
    @Test
    public void complexTime() {
        //number of quarries
        int n = 350;
        //times we do it
        for (int i = 0; i < 300; i++) {

            int[] cord = randomQuarry(n);
            long startTime = System.nanoTime();
            for (int j = 0; j < n; j++) {
                //we want to time the first time we create the ComplexSequential
                if (j == 0) {
                    test =  new ComplexSequential(data, 100, 500);
                }
                test.getPopulation(cord[j*4],cord[j*4+1],cord[j*4+2],cord[j*4+3]);
            }
            // ... the code being measured ...
            long endTime = System.nanoTime();
            // Throw away first NUM_WARMUP runs to exclude JVM warmup
            if (50 <= i) {
                totalTime += (endTime - startTime);
            }
        }
        double averageRuntime = totalTime / (300 - 50);
    }
    @Test
    public void simpTest(){
        //number of quarries
        int n = 400;
        data = PopulationQuery.parse("CenPop2010.txt");
        //times we do it
        data = PopulationQuery.parse("CenPop2010.txt");

        for (int i = 0; i < 300; i++) {
            int[] cord = randomQuarry(n);
            long startTime = System.nanoTime();
            for (int j = 0; j < n; j++) {

                //we want to time the first time we create the ComplexSequential
                if (j == 0) {
                    te = new SimpleSequential(data,100,500);
                }
                te.getPopulation(cord[j*4],cord[j*4+1],cord[j*4+2],cord[j*4+3]);
            }
            // ... the code being measured ...
            long endTime = System.nanoTime();
            // Throw away first NUM_WARMUP runs to exclude JVM warmup
            if (50 <= i) {
                totalTime += (endTime - startTime);
            }
        }
        double averageRuntime = totalTime / (300 - 50);
    }


}
