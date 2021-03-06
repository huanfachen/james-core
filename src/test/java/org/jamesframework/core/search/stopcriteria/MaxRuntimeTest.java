/*
 * Copyright 2014 Ghent University, Bayer CropScience.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jamesframework.core.search.stopcriteria;

import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.test.util.DoubleComparatorWithPrecision;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test maximum runtime stop criterion.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MaxRuntimeTest extends SearchTestTemplate {

    // search to work with (random descent)
    private Search<SubsetSolution> search;
    
    // maximum runtime
    private final long MAX_RUNTIME = 2345;
    private final long LOW_MAX_RUNTIME = 20;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    // coarse check period
    private final long COARSE_CHECK_PERIOD = 5;
    private final TimeUnit CHECK_PERIOD_TIME_UNIT = TimeUnit.SECONDS;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing MaxRuntime ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing MaxRuntime!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create random descent search
        search = new RandomDescent<>(problem, neigh);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor(){
        System.out.println(" - test constructor");
        new MaxRuntime(999, TimeUnit.MICROSECONDS);
    }
    
    /**
     * Test maximum runtime stop criterion.
     */
    @Test
    public void testMaxRuntime() {
        
        System.out.println(" - test with default period");
        System.out.println("   >>> max: " + MAX_RUNTIME_TIME_UNIT.toMillis(MAX_RUNTIME) + " ms");
        
        // add stop criterion to search
        search.addStopCriterion(new MaxRuntime(MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT));
        
        // run search (should stop)
        search.start();
        
        System.out.println("   >>> run: " + search.getRuntime() + " ms");
        System.out.println("   >>> best: " + search.getBestSolutionEvaluation());
        
    }
    
    /**
     * Test maximum runtime stop criterion with coarse check period.
     */
    @Test
    public void testMaxRuntimeWithCoarsePeriod() {
        
        System.out.println(" - test with coarse period ("
                                + CHECK_PERIOD_TIME_UNIT.toSeconds(COARSE_CHECK_PERIOD)
                                + " sec)");
        System.out.println("   >>> max: " + MAX_RUNTIME_TIME_UNIT.toMillis(MAX_RUNTIME) + " ms");
        
        // add stop criterion to search
        search.addStopCriterion(new MaxRuntime(MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT));
        // set coarse check period
        search.setStopCriterionCheckPeriod(COARSE_CHECK_PERIOD, CHECK_PERIOD_TIME_UNIT);
        
        // run search (should stop)
        search.start();
        
        System.out.println("   >>> run: " + search.getRuntime() + " ms");
        System.out.println("   >>> best: " + search.getBestSolutionEvaluation());
        
    }
    
    /**
     * Test subsequent runs.
     */
    @Test
    public void testSubsequentRuns() {
        
        System.out.println(" - test subsequent runs");
        
        // add stop criterion
        search.addStopCriterion(new MaxRuntime(LOW_MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT));
        // set check period to same value
        search.setStopCriterionCheckPeriod(LOW_MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        
        // perform 5 search runs
        Evaluation prevBestSolEval = null;
        for(int i=0; i<5; i++){
            search.start();
            // check best solution evaluation
            Evaluation bestSolEval = search.getBestSolutionEvaluation();
            System.out.println("   >>> best: " + bestSolEval);
            if(prevBestSolEval != null){
                assertTrue(DoubleComparatorWithPrecision.greaterThanOrEqual(
                                bestSolEval.getValue(),
                                prevBestSolEval.getValue(),
                                TestConstants.DOUBLE_COMPARISON_PRECISION));
            }
            prevBestSolEval = bestSolEval;
        }
        
    }
    
    /**
     * Test subsequent runs (minimizing).
     */
    @Test
    public void testSubsequentRunsMinimizing() {
        
        System.out.println(" - test subsequent runs (minimizing)");
        
        // set objective to minimize
        obj.setMinimizing();
        
        // add stop criterion
        search.addStopCriterion(new MaxRuntime(LOW_MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT));
        // set check period to same value
        search.setStopCriterionCheckPeriod(LOW_MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        
        // perform 5 search runs
        Evaluation prevBestSolEval = null;
        for(int i=0; i<5; i++){
            search.start();
            // check best solution evaluation
            Evaluation bestSolEval = search.getBestSolutionEvaluation();
            System.out.println("   >>> best: " + bestSolEval);
            if(prevBestSolEval != null){
                assertTrue(DoubleComparatorWithPrecision.smallerThanOrEqual(
                                bestSolEval.getValue(),
                                prevBestSolEval.getValue(),
                                TestConstants.DOUBLE_COMPARISON_PRECISION));
            }
            prevBestSolEval = bestSolEval;
        }
        
    }

}