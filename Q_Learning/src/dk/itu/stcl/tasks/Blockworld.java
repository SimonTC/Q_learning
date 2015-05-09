package dk.itu.stcl.tasks;

import java.util.Random;

import org.ejml.simple.SimpleMatrix;

import dk.itu.stcl.agents.QLearner;



public class Blockworld {
	
	private SimpleMatrix world;
	private enum ACTIONS {N,S,E,W};
	private final double END_REWARD = 1;
	private State goal;
	private State hole;
	private Random rand = new Random();
	
	public void setup(int worldSize){
		world = new SimpleMatrix(worldSize, worldSize);
		goal = selectRandomState(true);
		world.set(goal.row, goal.col, END_REWARD);
	}
	
	public void runEpisode(QLearner agent, double explorationChance){
		State state = selectRandomState(true);
		int actionID = -1;
		agent.newEpisode();
		while(!isGoalState(state)){
			if (rand.nextDouble() < explorationChance){
				actionID = rand.nextInt(ACTIONS.values().length);
			} else {
				actionID = agent.selectBestAction(state.id);
			}
			
			State nextState = move(state, ACTIONS.values()[actionID]);
			
			double reward = world.get(nextState.row, nextState.col);
			if (reward > 0){
				//System.out.println();
			}
			
			agent.updateQMatrix(state.id, actionID, nextState.id, reward);
			
			state = nextState;			
		}
	}
	
	public void printPolicyMap(QLearner agent){
		for (int row = 0; row < world.numRows(); row++){
			for (int col = 0; col < world.numCols(); col++){
				State s = new State(row, col, world);
				if (s.equals(goal)){
					System.out.print("*  ");
				} else if (s.equals(hole)){
					System.out.print("/  ");
				} else {
					int bestAction = agent.selectBestAction(s.id);
					System.out.print(ACTIONS.values()[bestAction].name() + "  ");
				}
			}
			System.out.println();
		}
	}

	private State move(State state, ACTIONS action){
		int rowChange = 0, colChange = 0;
		switch(action){
		case E: rowChange = 0; colChange = 1;  break;
		case N: rowChange = -1; colChange = 0;  break;
		case S: rowChange = 1; colChange = 0;  break;
		case W: rowChange = 0; colChange = -1;  break;		
		}
		
		int newRow = state.getRow() + rowChange;
		int newCol = state.getCol() + colChange;
		
		if(newRow < 0 || newRow > world.numRows() - 1) newRow = state.getRow();
		if(newCol < 0 || newCol > world.numCols() - 1) newCol = state.getCol();
		
		State newState = new State(newRow, newCol, world);
		return newState;
	}
	
	private State selectRandomState(boolean includeGoalState){
		boolean stateFound = false;
		State s = null;
		int id = -1;
		while(!stateFound){
			id = rand.nextInt(world.getNumElements());
			if (includeGoalState){
				stateFound = true;
			} else {
				if (id != goal.id) stateFound = true;
			}
		}
		s = new State(id, world);
		return s;
	}
	
	private boolean isGoalState(State s){
		return s.equals(goal);
	}
	
	public int getNumActions(){
		return ACTIONS.values().length;
	}
	
	public int getNumStates(){
		return world.getNumElements();
	}
	
	private class State{
		private int row, col, id;
		public State(int row, int col, SimpleMatrix world){
			this.row = row;
			this.col = col;
			this.id = world.getIndex(row, col);
		}
		public State(int id, SimpleMatrix world){
			int numCols = world.numCols();
			row = (int) Math.floor(id / (double)numCols);
			col = id - row * numCols;
			this.id = id;
			
		}
		public int getRow(){
			return row;
		}
		public int getCol(){
			return col;
		}
		public int getID(){
			return id;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + col;
			result = prime * result + id;
			result = prime * result + row;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (col != other.col)
				return false;
			if (id != other.id)
				return false;
			if (row != other.row)
				return false;
			return true;
		}
		private Blockworld getOuterType() {
			return Blockworld.this;
		}
		
	}
}
