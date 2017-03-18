package edu.ucsd.dbmi.perank.computation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import edu.ucsd.dbmi.perank.bean.RandomWalkBean;


public class PPVWithRAMIndexRunable implements Runnable {

	RAMDirectory ddirectory;
	RAMDirectory cldirectory;
	String line;
	BufferedWriter bw;
	int length = 10;
	Double damping = 0.15;
	String preference;
	String type;
	public PPVWithRAMIndexRunable(RAMDirectory ddir, RAMDirectory cldir, String line, BufferedWriter bw, Integer length, String preference, String type) throws IOException {
		this.ddirectory = ddir;
		this.cldirectory = cldir;
		this.line = line;
		this.bw = bw;
		this.length = length;
		this.preference=preference;
		this.type=type;
	}
	public void RamKill(){
		this.ddirectory.close();
		this.cldirectory.close();
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
		bean.setFitPreference(false);
		bean.setPreviousPosition(position);
		bean.setStartPosition(position);
		bean.setSteps(0);
		bean.setKeepWalk(true);
		Double rd=0.0;
		long ID=System.currentTimeMillis();
		while (bean.getSteps() < length && bean.isKeepWalk()) {
			rd = Math.random();
			if (rd < damping) {
				bean.setKeepWalk(false);
				bean.setCurrentPosition(position);
			}else{
				PPVWalkWithRAMIndex randomWalk = null;
				try {
					randomWalk = new PPVWalkWithRAMIndex(ddirectory, cldirectory,	preference, type);
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

		if (bean.getLastFitPosition() != "") {
			position = bean.getLastFitPosition();
		} else {
			position = bean.getStartPosition();
		}

		System.out.println(bean.getSteps()+" "+bean.isKeepWalk()+" "+rd);
		try {
			bw.write(key + " " + position + "\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
