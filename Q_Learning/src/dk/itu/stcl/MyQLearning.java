package dk.itu.stcl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class MyQLearning {
	final DecimalFormat df = new DecimalFormat("#.##");
	private enum ACTIONS {N,S,E,W,Ø};
	private Random rand = new Random();
	
	private ACTIONS[][][] possibleActions;
	
	private int[][] world = {
			{1,0,0,0},
			{0,0,0,0},
			{0,0,0,0},
			{0,0,0,0}
	};
	
	private double alpha = 0.1;
	private double gamma = 0.9;
	
	private int stateCount;
	private double[][][] qMatrix;
	
	public static void main(String[] args){
		MyQLearning q = new MyQLearning();
		q.run();
	}
	
	public void run(){
		setup();
		runTraining(1000);
		printPolicyMap();
		System.out.println();
		printQMatrix();
	}
	
	public void setup(){
		createPossibleActions();
		qMatrix = new double[world.length][world[0].length][ACTIONS.values().length];
	}
	
	private void printPolicyMap(){
		for (int row = 0; row < world.length; row++){
			for (int col = 0; col < world[row].length; col++){
				ACTIONS bestAction = chooseBestAction(row, col);
				System.out.print(bestAction.name() + "  ");
			}
			System.out.println();
		}
	}
	
	private void printQMatrix(){
		for (int row = 0; row < world.length; row++){
			for (int col = 0; col < world[row].length; col++){
				for (int i = 0; i < ACTIONS.values().length; i++){
					 System.out.print(df.format(qMatrix[row][col][i]) + "  ");
				}
			}
			System.out.println();
		}
	}
	
	private void runTraining(int maxEpisodes){
		for (int i = 0; i < maxEpisodes; i++){
			doEpisode();
		}
	}
	
	private void doEpisode(){
		int[] state = selectRandomState();
		while(!isGoalState(state)){
			int row = state[0];
			int col = state[1];
			//Select action to take
			ACTIONS[] actionsFromState = possibleActions[row][col];
			ACTIONS action = actionsFromState[rand.nextInt(actionsFromState.length)];
			
			//Move
			int[] nextState = move(row, col, action);
			if (!stateIsValid(nextState[0], nextState[1])) nextState = state;
			
			//Update Q-value
			double q = qMatrix[row][col][action.ordinal()];
			double maxQ = maxQ(nextState[0], nextState[1]);
			double r = world[nextState[0]][nextState[1]];
			double value = q + alpha * (r + gamma * maxQ - q);
			qMatrix[row][col][action.ordinal()]= value;
			
			state = nextState;
			
		}
	}
	
	private int[] selectRandomState(){
		int row = rand.nextInt(world.length);
		int col = rand.nextInt(world[0].length);
		int[] result = {row, col};
		return result;
	}
	
	private boolean isGoalState(int[] state){
		if(world[state[0]][state[1]] == 1) return true;
		return false;
	}
	
	private void createPossibleActions(){
		stateCount = world.length * world[0].length;
		possibleActions = new ACTIONS[world.length][world[0].length][];
		for (int row = 0; row < world.length; row++){
			for (int col = 0; col < world[row].length; col++){
				ArrayList<ACTIONS> allowedActions = new ArrayList<MyQLearning.ACTIONS>();
				for (ACTIONS a : ACTIONS.values()){
					allowedActions.add(a);
					//if (actionIsValid(row, col, a)) allowedActions.add(a);
				}
				
				possibleActions[row][col] = new ACTIONS[allowedActions.size()];
				for (int i = 0; i < allowedActions.size(); i++){
					possibleActions[row][col][i] = allowedActions.get(i);
				}
			}
		}
	}
	
	private boolean actionIsValid(int row, int col, ACTIONS action){
		int[] result = move(row, col, action);
		int newRow = result[0];
		int newCol = result[1];
		return stateIsValid(newRow, newCol);
	}
	
	private boolean stateIsValid(int row, int col){
		if(row < 0 || row > world.length - 1) return false;
		if(col < 0 || col > world[0].length - 1) return false;
		return true;
	}
	
	private int[] move(int row, int col, ACTIONS action){
		int rowChange = 0, colChange = 0;
		switch(action){
		case E: rowChange = 0; colChange = 1;  break;
		case N: rowChange = -1; colChange = 0;  break;
		case S: rowChange = 1; colChange = 0;  break;
		case W: rowChange = 0; colChange = -1;  break;		
		case Ø: rowChange = 0; colChange = 0; break;
		}
		
		int newCol = col + colChange;
		int newRow = row + rowChange;
		
		int[] result = {newRow, newCol};
		return result;		
	}
	
   private double maxQ(int row, int col) {
        ACTIONS[] actionsFromState = possibleActions[row][col];
        double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < actionsFromState.length; i++) {
        	ACTIONS action = actionsFromState[i];
        	double value = qMatrix[row][col][action.ordinal()];
 
            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }
   
   private ACTIONS chooseBestAction(int row, int col){
	   ACTIONS[] actionsFromState = possibleActions[row][col];
       double maxValue = Double.NEGATIVE_INFINITY;
       ACTIONS bestAction = null;
       for (int i = 0; i < actionsFromState.length; i++) {
    	   ACTIONS action = actionsFromState[i];
    	   double value = qMatrix[row][col][action.ordinal()];

           if (value > maxValue){
               maxValue = value;
               bestAction = action;
           }
       }
       
       return bestAction;

   }

}
