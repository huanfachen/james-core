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

package org.jamesframework.test.fakes;

import org.jamesframework.core.exceptions.IncompatibleDeltaValidationException;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.test.util.MinMaxObjective;

/**
 * A fake subset objective that evaluates a subset solution to the sum of scores corresponding to the selected IDs,
 * where scores are provided by an instance of {@link ScoredFakeSubsetData}. Used for testing only.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SumOfScoresFakeSubsetObjective extends MinMaxObjective<SubsetSolution, ScoredFakeSubsetData>{

    /**
     * Evaluate a subset solution by computing the sum of scores of selected entities.
     * 
     * @param solution solution to evaluate
     * @param data underlying fake subset data
     * @return sum of scores of selected entities
     */
    @Override
    public Evaluation evaluate(SubsetSolution solution, ScoredFakeSubsetData data) {
        return new SimpleEvaluation(
                    solution.getSelectedIDs().stream().mapToDouble(data::getScore).sum()
        );
    }
    
    /**
     * Delta evaluation. Subtracts scores of removed IDs and adds scores of newly selected IDs.
     * 
     * @param move move to be applied
     * @param curSol current solution
     * @param curEval current evaluation
     * @param data underlying data
     * @return modified evaluation
     */
    @Override
    public Evaluation evaluate(Move move, SubsetSolution curSol, Evaluation curEval, ScoredFakeSubsetData data){
        if(!(move instanceof SubsetMove)){
            throw new IncompatibleDeltaValidationException("Expected move of type SubsetMove.");
        }
        SubsetMove sMove = (SubsetMove) move;
        
        // update evaluation
        double e = curEval.getValue();
        e += sMove.getAddedIDs().stream().mapToDouble(data::getScore).sum();
        e -= sMove.getDeletedIDs().stream().mapToDouble(data::getScore).sum();
        
        return new SimpleEvaluation(e);
    }
    
    @Override
    public String toString(){
        return "Sum of scores";
    }

}
