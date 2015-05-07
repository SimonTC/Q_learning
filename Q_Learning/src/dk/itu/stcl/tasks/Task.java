package dk.itu.stcl.tasks;

import dk.itu.stcl.agents.QLearner;

public interface Task {
	
	public void runEpisode(QLearner agent, double explorationChance);
	
	public void printPolicyMap(QLearner agent);
	
	public int getNumActions();
	
	public int getNumStates();
}
