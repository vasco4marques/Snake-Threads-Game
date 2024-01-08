package game;

import environment.LocalBoard;

public class ObstacleMover extends Thread {
	private Obstacle obstacle;
	private LocalBoard board;
	
	public ObstacleMover(Obstacle obstacle, LocalBoard board) {
		super();
		this.obstacle = obstacle;
		this.board = board;
	}

	@Override
	//While the obstacle has remaining moves he will get renewed
	public void run() {
		while(obstacle.getRemainingMoves()>0) {
			try {
				sleep(Obstacle.OBSTACLE_MOVE_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			board.renewObstacle(obstacle);
			board.setChanged();
		}
	}
}
