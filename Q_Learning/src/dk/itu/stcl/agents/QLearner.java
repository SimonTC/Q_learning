package dk.itu.stcl.agents;

import java.util.LinkedList;

import org.ejml.simple.SimpleMatrix;

public class QLearner {
	
	protected SimpleMatrix qMatrix;
	protected double alpha, gamma;
	private boolean offline;
	private LinkedList<Transition> transitions;
	
	public QLearner(int numStates, int numActions, double alpha, double gamma, boolean offline){
		qMatrix = new SimpleMatrix(numStates, numActions);
		this.alpha = alpha;
		this.gamma = gamma;
		this.offline = offline;
		if(offline) transitions = new LinkedList<QLearner.Transition>();
	}
	
	/**
	 * Selects the best action to perform given the current state
	 * @param stateID
	 * @return
	 */
	public int selectBestAction(int state) {
		SimpleMatrix actionVector = qMatrix.extractVector(true, state);

		int bestAction = maxElement(actionVector);
		return bestAction;
	}
	
	public int selectBestAction(SimpleMatrix stateProbabilities){
		SimpleMatrix actionValues = stateProbabilities.mult(qMatrix);
		int bestAction = maxElement(actionValues);
		return bestAction;
		
	}
	
	private int maxElement(SimpleMatrix m){
		double maxValue = Double.NEGATIVE_INFINITY;
		int maxElement = -1;
		
		for(int i = 0; i < m.getNumElements(); i++){
			double d = m.get(i);
			if (d > maxValue){
				maxValue = d;
				maxElement = i;
			}
		}
		return maxElement;
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
		this.updateQMatrix(originState, action, nextState, -1, reward);
	}
	
	public void updateQMatrix(int originState, int action, int nextState, int nextAction,
			double reward) {
		if (offline){
			Transition t = new Transition(originState, action, nextState,nextAction, reward);
			transitions.addLast(t);
		} else {
			QUpdate(originState, action, nextState, nextAction, reward);
		}		
		
	}
	
	protected void QUpdate(int originState, int action, int nextState, int nextAction,
			double reward){
		double q = qMatrix.get(originState, action);
		double maxQ = maxQ(nextState);
		double delta = alpha * (reward + gamma * maxQ - q);
		double newQ = q + delta;
		qMatrix.set(originState, action, newQ);
	}
	
	private void QUpdate(Transition t){
		this.QUpdate(t.originState, t.action, t.nextState, t.nextAction, t.reward);
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
		if (offline){
			while (!transitions.isEmpty()){
				Transition t = transitions.removeLast();
				QUpdate(t);
			}
		}
	}
	
	protected class Transition{
		
		private int originState, action, nextState, nextAction;
		private double reward;
		
		protected Transition(int originState, int action, int nextState,
				double reward){
			this(originState, action, nextState, -1, reward);
		}
		
		protected Transition (int originState, int action, int nextState, int nextAction,
				double reward) {
			this.originState = originState;
			this.action = action;
			this.nextState = nextState;
			this.nextAction = nextAction;
			this.reward = reward;
			
		}
	}
	

}
