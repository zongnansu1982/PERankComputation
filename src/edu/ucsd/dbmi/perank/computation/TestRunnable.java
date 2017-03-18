package edu.ucsd.dbmi.perank.computation;

public class TestRunnable implements Runnable {
	int num=0;
	public TestRunnable(int num){
		this.num=num;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(this.num);
	}

}
