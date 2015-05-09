package dk.itu.stcl.agents;

import org.ejml.simple.SimpleMatrix;

public class QLearner {
	
	protected SimpleMatrix qMatrix;
	protected double alpha, gamma;
	
	public QLearner(int numStates, int numActions, double alpha, double gamma){
		qMatrix = new SimpleMatrix(numStates, numActions);
		this.alpha = alpha;
		this.gamma = gamma;
	}
	
	/**
	 * Selects the best action to perform given the current state
	 * @param stateID
	 * @return
	 */
	public int selectBestAction(int state) {
		SimpleMatrix actionVector = qMatrix.extractVector(true, state);
		
		double maxValue = Double.NEGATIVE_INFINITY;
		int bestAction = -1;
		
		for(int i = 0; i < actionVector.getNumElements(); i++){
			double d = actionVector.get(i);
			if (d > maxValue){
				maxValue = d;
				bestAction = i;
			}
		}
		return bestAction;
	}
	
	/**
	 * Updates the q-entry for the state-action pair (originState,action)
	 * @param originState State agent started in
	 * @param action Action performed in originState
	 * @param nextState State agent ended up in after performing action in origin state
	 * @param reward reward received for performing action in origin state and ending up in nextState
	 */
	public void updateQMatrix(int originState, int action, int nextState,
			double reward) {
		
		double q = qMatrix.get(originState, action);
		double maxQ = maxQ(nextState);
		double delta = alpha * (reward + gamma * maxQ - q);
		double newQ = q + delta;
		qMatrix.set(originState, action, newQ);
		
		
	}
	
	public void updateQMatrix(int originState, int action, int nextState, int nextAction,
			double reward) {
		this.updateQMatrix(originState, action, nextState, reward);
	}
	
	/**
	 * Returns the maximum possible q-value it is possible to be rewarded by doing actions from the given state
	 * @param state
	 * @return
	 */
	private double maxQ(int state){
		SimpleMatrix actionVector = qMatrix.extractVector(true, state);
		
		double maxValue = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < actionVector.getNumElements(); i++){
			double d = actionVector.get(i);
			if (d > maxValue) maxValue = d;
		}
		
		return maxValue;
	}
	
	public void setAlpha(double alpha){
		this.alpha = alpha;
	}
	
	public void setGamma(double gamma){
		this.gamma = gamma;
	}
	
	public SimpleMatrix getQMatrix(){
		return qMatrix;
	}
	
	public void newEpisode(){
		
	}
	

}
