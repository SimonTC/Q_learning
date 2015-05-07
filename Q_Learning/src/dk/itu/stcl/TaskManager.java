package dk.itu.stcl;

import dk.itu.stcl.agents.QLearner;
import dk.itu.stcl.tasks.Blockworld;

public class TaskManager {

	public static void main(String[] args){
		TaskManager tm = new TaskManager();
		tm.run(1000);
	}
	
	public void run(int numEpisodes){
		Blockworld bw = new Blockworld();
		bw.setup(4);
		QLearner agent = new QLearner(bw.getNumStates(), bw.getNumActions(), 0.1, 0.9);
		
		for (int i = 0; i < numEpisodes; i++){
			bw.runEpisode(agent, 1 - ((double) i / numEpisodes));
		}
		
		System.out.println("Policy map");
		bw.printPolicyMap(agent);
		System.out.println();
		
		System.out.println("Q matrix");
		agent.getQMatrix().print();
		
	}

}
