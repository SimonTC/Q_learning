package dk.itu.stcl.agents;

public class SARSALearner extends QLearner {
	
	private int nextAction, actionBefore, stateBefore;

	public SARSALearner(int numStates, int numActions, double alpha,
			double gamma) {
		super(numStates, numActions, alpha, gamma);
		nextAction = -1;
		actionBefore = -1;
		stateBefore = -1;
	}
	
	@Override
	public void updateQMatrix(int originState, int action, int nextState,
			double reward) {
		
		if (actionBefore != -1){
			double q = qMatrix.get(stateBefore, actionBefore);
			double nextQ = qMatrix.get(originState, action);
			double delta = alpha * (reward + gamma * nextQ - q);
			double newQ = q + delta;
			qMatrix.set(stateBefore, actionBefore, newQ);
		}
		
		stateBefore = originState;
		actionBefore = action;
	}
	
	

}
