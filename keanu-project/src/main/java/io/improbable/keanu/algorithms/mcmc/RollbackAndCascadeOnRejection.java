package io.improbable.keanu.algorithms.mcmc;

import io.improbable.keanu.algorithms.Variable;
import io.improbable.keanu.algorithms.graphtraversal.VertexValuePropagation;
import io.improbable.keanu.algorithms.mcmc.proposal.Proposal;
import io.improbable.keanu.vertices.IVertex;

import java.util.HashMap;
import java.util.Map;

/**
 * When a proposal is created, take a snapshot of the vertices' values.
 * When a proposal is rejected, reset the Bayes Net to those values and cascade update.
 * This is less performant than {@link RollBackToCachedValuesOnRejection}
 */
public class RollbackAndCascadeOnRejection implements ProposalRejectionStrategy {

    private Map<IVertex, Object> fromValues;

    @Override
    public void onProposalCreated(Proposal proposal) {

        fromValues = new HashMap<>();
        for (Variable variable : proposal.getVariablesWithProposal()) {

            if (variable instanceof IVertex) {
                fromValues.put((IVertex) variable, variable.getValue());
            } else {
                throw new IllegalArgumentException(this.getClass().getSimpleName() + " is to only be used with Keanu's Vertex");
            }

        }
    }

    @Override
    public void onProposalRejected(Proposal proposal) {

        for (Map.Entry<IVertex, Object> entry : fromValues.entrySet()) {
            Object oldValue = entry.getValue();
            IVertex vertex = entry.getKey();
            vertex.setValue(oldValue);
        }
        VertexValuePropagation.cascadeUpdate(fromValues.keySet());
    }
}
