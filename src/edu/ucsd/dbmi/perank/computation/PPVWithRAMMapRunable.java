package edu.ucsd.dbmi.perank.computation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import edu.ucsd.dbmi.perank.bean.RandomWalkBean;


public class PPVWithRAMMapRunable implements Runnable {

	HashMap<String,ArrayList<String>> preferenceRelated;
	HashMap<String,ArrayList<String>> preferenceNonRelated;
	String prefernece;
	String line;
	BufferedWriter bw;
	int length = 10;
	Double damping = 0.15;
	public PPVWithRAMMapRunable(String prefernece, HashMap<String,ArrayList<String>> preferenceRelated, HashMap<String,ArrayList<String>> preferenceNonRelated, 
			String line, BufferedWriter bw, Integer length) throws IOException {
		this.prefernece=prefernece;
		this.preferenceRelated = preferenceRelated;
		this.preferenceNonRelated = preferenceNonRelated;
		this.line = line;
		this.bw = bw;
		this.length = length;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String[] contents = line.split(" "); // this is sapce

		String key = contents[0].trim();
		String sampleNum = contents[1].trim();
		String position = contents[2].trim();

		RandomWalkBean bean = new RandomWalkBean();
		bean.setCurrentPosition(position);
		bean.setFitPreference(true);
		bean.setPreviousPosition(position);
		bean.setLastFitPosition(position);
		bean.setStartPosition(position);
		bean.setSteps(0);
		bean.setKeepWalk(true);
		
		long ID=System.currentTimeMillis();
		Double rd=0.0;
		while (bean.getSteps() < length && bean.isKeepWalk()) {
			Random random=new Random();
			rd = random.nextDouble();
			if (rd < damping) {
				bean.setKeepWalk(false);
				bean.setCurrentPosition(bean.getCurrentPosition());
				bean.setLastFitPosition(bean.getLastFitPosition());
			}else{
				PPVWalkWithRAMMap randomWalk = null;
				try {
					
					randomWalk = new PPVWalkWithRAMMap(preferenceRelated, preferenceNonRelated);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					randomWalk.getNext(bean);
					
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

//		System.out.println(bean.getSteps()+" "+bean.isKeepWalk()+" "+rd);
		
		if (bean.getLastFitPosition() != "") {
			position = bean.getLastFitPosition();
		} else {
			position = bean.getCurrentPosition();
		}

		// System.out.println(key + " " + position);
		try {
			bw.write(key + " "+ this.prefernece+" "+ position + "\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
