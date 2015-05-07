package dk.itu.stcl.agents;

import org.ejml.simple.SimpleMatrix;

public class SARSA_Eligibility extends SARSALearner {
	
	private SimpleMatrix traceMatrix;
	private double lambda;
	
	public SARSA_Eligibility(int numStates, int numActions, double alpha,
			double gamma, double lambda) {
		super(numStates, numActions, alpha, gamma);
		traceMatrix = new SimpleMatrix(numStates, numActions);
		this.lambda = lambda;
	}
	
	@Override
	public void updateQMatrix(int originState, int action, int nextState, int nextAction,
			double reward) {
		
		//Calculate TD error
		double q = qMatrix.get(originState, action);
		double nextQ = qMatrix.get(nextState, nextAction);
		double delta = reward + gamma * nextQ - q;
		
		//Update trace of origin state-action pair
		updateTraceMatrix(originState, action);
		
		//Update q-matrix
		qMatrix = qMatrix.plus(alpha * delta, traceMatrix);	
	
	}
	
	
	private void updateTraceMatrix(int state, int action){
		
		//Decay traces
		traceMatrix = traceMatrix.scale(lambda * gamma);
		
		//Calculate new trace for state-action pair
		double newTrace = traceMatrix.get(state, action) + 1;
				
		//Set trace of unchosen actions in current state to zero
		SimpleMatrix actionVector = traceMatrix.extractVector(true, state);
		actionVector.set(0);
		traceMatrix.insertIntoThis(state, 0, actionVector);
		
		//Set trace of current state-action pair
		traceMatrix.set(state, action, newTrace);
		
	}

}
