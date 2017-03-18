package edu.ucsd.dbmi.perank.bean;

public class RandomWalkBean {
	
	public String getStartPosition() {
		return startPosition;
	}


	public void setStartPosition(String startPosition) {
		this.startPosition = startPosition;
	}


	public String getPreviousPosition() {
		return previousPosition;
	}


	public void setPreviousPosition(String previousPosition) {
		this.previousPosition = previousPosition;
	}


	public String getCurrentPosition() {
		return currentPosition;
	}


	public void setCurrentPosition(String currentPosition) {
		this.currentPosition = currentPosition;
	}


	public int getSteps() {
		return steps;
	}


	public void setSteps(int steps) {
		this.steps = steps;
	}


	private String startPosition="";
	private String previousPosition="";
	private String currentPosition="";
	private int steps=0;
	private boolean fitPreference=false;
	private boolean keepWalk=true;
	public boolean isKeepWalk() {
		return keepWalk;
	}


	public void setKeepWalk(boolean keepWalk) {
		this.keepWalk = keepWalk;
	}


	private String lastFitPosition="";

	public String getLastFitPosition() {
		return lastFitPosition;
	}


	public void setLastFitPosition(String lastFitPosition) {
		this.lastFitPosition = lastFitPosition;
	}


	


	public boolean isFitPreference() {
		return fitPreference;
	}


	public void setFitPreference(boolean fitPreference) {
		this.fitPreference = fitPreference;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
