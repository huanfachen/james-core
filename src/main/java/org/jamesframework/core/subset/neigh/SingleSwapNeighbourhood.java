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

package org.jamesframework.core.subset.neigh;

import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.core.subset.neigh.moves.SwapMove;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.util.SetUtilities;

/**
 * <p>
 * A subset neighbourhood that generates swap moves only (see {@link SwapMove}). A swap move is a subtype of
 * {@link SubsetMove}. When applying swap moves generated by this neighbourhood to a given subset solution, the set
 * of selected IDs will always remain of the same size. Therefore, this neighbourhood is only suited for fixed size
 * subset selection. If desired, a set of fixed IDs can be provided which are not allowed to be swapped.
 * </p>
 * <p>
 * Note that this neighbourhood is thread-safe: it can be safely used to concurrently generate moves in different
 * searches running in separate threads.
 * </p>
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleSwapNeighbourhood extends SubsetNeighbourhood {
    
    /**
     * Creates a basic single swap neighbourhood.
     */
    public SingleSwapNeighbourhood(){
        this(null);
    }
    
    /**
     * Creates a single swap neighbourhood with a given set of fixed IDs which are not allowed to be swapped. None of
     * the generated swap moves will add nor remove any of these IDs.
     * 
     * @param fixedIDs set of fixed IDs which are not allowed to be swapped
     */
    public SingleSwapNeighbourhood(Set<Integer> fixedIDs){
        super(fixedIDs);
    }
    
    /**
     * Generates a random swap move for the given subset solution that removes a single ID from the set of currently selected IDs,
     * and replaces it with a random ID taken from the set of currently unselected IDs. Possible fixed IDs are not considered to be
     * swapped. If no swap move can be generated, <code>null</code> is returned.
     * 
     * @param solution solution for which a random swap move is generated
     * @param rnd source of randomness used to generate random move
     * @return random swap move, <code>null</code> if no swap move can be generated
     */
    @Override
    public SubsetMove getRandomMove(SubsetSolution solution, Random rnd) {
        // get set of candidate IDs for removal and addition (possibly fixed IDs are discarded)
        Set<Integer> removeCandidates = getRemoveCandidates(solution);
        Set<Integer> addCandidates = getAddCandidates(solution);
        // check if swap is possible
        if(removeCandidates.isEmpty() || addCandidates.isEmpty()){
            // impossible to perform a swap
            return null;
        }
        // select random ID to remove from selection
        int del = SetUtilities.getRandomElement(removeCandidates, rnd);
        // select random ID to add to selection
        int add = SetUtilities.getRandomElement(addCandidates, rnd);
        // create and return swap move
        return new SwapMove(add, del);
    }

    /**
     * Generates a list of all possible swap moves that transform the given subset solution by removing a single ID from
     * the current selection and replacing it with a new ID which is currently not selected. Possible fixed IDs are not 
     * considered to be swapped. May return an empty list if no swap moves can be generated.
     * 
     * @param solution solution for which all possible swap moves are generated
     * @return list of all swap moves, may be empty
     */
    @Override
    public List<SubsetMove> getAllMoves(SubsetSolution solution) {
        // get set of candidate IDs for removal and addition (possibly fixed IDs are discarded)
        Set<Integer> removeCandidates = getRemoveCandidates(solution);
        Set<Integer> addCandidates = getAddCandidates(solution);
        // first check if swaps are possible, for efficiency (avoids superfluous loop iterations)
        if(removeCandidates.isEmpty() || addCandidates.isEmpty()){
            // no swap moves can be applied, return empty set
            return Collections.emptyList();
        }
        // create swap move for all combinations of add and remove candidates
        return addCandidates.stream()
                            .flatMap(add -> removeCandidates.stream().map(remove -> new SwapMove(add, remove)))
                            .collect(Collectors.toList());
    }

}
