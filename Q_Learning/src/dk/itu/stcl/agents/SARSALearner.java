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
		
		double q = qMatrix.get(originState, action);
		double nextQ = qMatrix.get(nextState, nextAction);
		double delta = alpha * (reward + gamma * nextQ - q);
		double newQ = q + delta;
		qMatrix.set(originState, action, newQ);
	}
	

}
