package edu.ucsd.dbmi.perank.computation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;


public class PERankWithRAMIndex {
	
	RAMDirectory graphIndex;
	RAMDirectory clsLinkIndex;
	
	int length = 10;
	Double damping = 0.15;
	String preference;
	String type="";
	public PERankWithRAMIndex(String graphIndexDir, String clsLinkIndexDir, Integer length, String preference,  String type) throws Exception {
		
		this.graphIndex = new RAMDirectory(FSDirectory.open(new File(graphIndexDir)));
		this.clsLinkIndex = new RAMDirectory(FSDirectory.open(new File(clsLinkIndexDir)));
		this.length = length;
		this.preference=preference;
		this.type=type;
	}
	
	
	public Integer lineReader(String file) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		int i=0;
		while ((line = br.readLine()) != null) {
			i++;
		}
		br.close();
		return i;
	}

	public void computeFingerPrints(String ppvIniFile, String outFile) throws IOException, InterruptedException {
		long startTime = System.currentTimeMillis();
		int i = 0;
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outFile)));
		BufferedReader br = new BufferedReader(new FileReader(new File(ppvIniFile)));
		String line = null;
		int number=lineReader(ppvIniFile);
		
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		while ((line = br.readLine()) != null) {
			i++;
			if (i % 10000 == 0) {
				System.out.println("@ computing: " + (double) i / number + "%");
//				Executors.newScheduledThreadPool(corePoolSize);
			}
			pool.submit(new PPVWithRAMIndexRunable( this.graphIndex, this.clsLinkIndex, line,  bw, length ,  preference, type));
		}
		
		pool.shutdown();
		
		 while(!pool.awaitTermination(10, TimeUnit.MINUTES))
         {
			 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//è®¾ç½®æ—¥æœŸæ ¼å¼�
			 System.out.println(df.format(new Date()));
         }
	
		
		 
		if(pool.isTerminated()){
			System.gc();
			bw.flush();
			bw.close();
			br.close();
			long endTime = System.currentTimeMillis();
			System.out.println("end!");
			System.out.println("time spend: " + (endTime - startTime) + " ms");	
		}
		
		
	}

	public void delFiles(String dir) {
		for (File f : new File(dir).listFiles()) {
			f.deleteOnExit();
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String graphIndex="D:/data/perank/starIndex/dataGraph";
		String schemaIndex="D:/data/perank/starIndex/clsAndLink";
		int length=10;
		String cp= "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugs>"; 
		String lp= "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/possibleDrug>"; 
		String type=PPVWalkWithRAMIndex.nodeType;
		PERankWithRAMIndex fg = new PERankWithRAMIndex( graphIndex,  schemaIndex,  length,  cp,   type);
		fg.computeFingerPrints("D:/data/perank/query/2017ppvIniTest.txt", "D:/data/perank/query/2017ppv_cls_drug.txt");

	}

}
