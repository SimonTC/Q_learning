package dk.itu.stcl.agents;

import org.ejml.simple.SimpleMatrix;

public class SARSALearner extends QLearner {
	
	private int nextAction;
	private SimpleMatrix traceMatrix;
	private double lambda;

	public SARSALearner(int numStates, int numActions, double alpha,
			double gamma, double lambda) {
		super(numStates, numActions, alpha, gamma);
		traceMatrix = new SimpleMatrix(numStates, numActions);
		this.lambda = lambda;
		nextAction = -1;
	}
	
	@Override
	public int selectBestAction(int state) {
		if (nextAction == -1){
			return super.selectBestAction(state);
		}
		
		int action = nextAction;
		nextAction = super.selectBestAction(state);
		return action;
	}
	
	@Override
	public void updateQMatrix(int originState, int action, int nextState,
			double reward) throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}
	
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
		double newTrace = 1;//traceMatrix.get(state, action) + 1;
				
		//Set trace of unchosen actions in current state to zero
		SimpleMatrix actionVector = traceMatrix.extractVector(true, state);
		actionVector.set(0);
		traceMatrix.insertIntoThis(state, 0, actionVector);
		
		//Set trace of current state-action pair
		traceMatrix.set(state, action, newTrace);
		
	}
	
	public void newEpisode(){
		traceMatrix.set(0);
	}
	
	public SimpleMatrix getTraceMatrix(){
		return traceMatrix;
	}
	

}
