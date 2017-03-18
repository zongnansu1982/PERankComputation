package edu.ucsd.dbmi.perank.computation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;


public class PERankWithRAMMap {
	
	HashMap<String,ArrayList<String>> preferenceRelated;
	HashMap<String,ArrayList<String>> preferenceNonRelated;
	HashMap<String,HashMap<String,Boolean>> clsAndLink;
	
	int length = 10;
	Double damping = 0.15;
	String preference;
	String type="";
	public PERankWithRAMMap(String graphIndexDir, String clsLinkIndexDir, Integer length, String preference,  String type) throws Exception {
		
		this.length = length;
		this.preference=preference;
		this.type=type;
		feedRAMWithSchema(clsLinkIndexDir);
		feedRAMWithOutLinks(graphIndexDir);
		System.out.println("Map Feeding finished ...");
	}
	
	public void feedRAMWithOutLinks(String graphIndexDir) throws IOException{
		preferenceRelated=new HashMap<String,ArrayList<String>>();
		preferenceNonRelated=new HashMap<String,ArrayList<String>>();
		
		RAMDirectory dir=new RAMDirectory(FSDirectory.open(new File(graphIndexDir)));
		IndexSearcher searcher = new IndexSearcher(dir);
		
		for(int i=0; i<searcher.maxDoc(); i++){
			
			ArrayList<String> nextMove = new ArrayList<String>();
			ArrayList<String> tmp = new ArrayList<String>();
			
			String url = searcher.doc(i).get("url").trim();
			String[] content=searcher.doc(i).get("content").trim().split("	");
			String ntriples = content[1];
			InputStream   inputStream   =   new   ByteArrayInputStream(ntriples.getBytes());
			NxParser nxp = new NxParser(inputStream,false);
			while (nxp.hasNext()) {
				Node[] quard = (Node[]) nxp.next();
				
				String s = quard[0].toN3().trim();
				String p = quard[1].toN3().trim();
				String o = quard[2].toN3().trim();
				if(s.startsWith("<http://")&&o.startsWith("<http://")){
					String preference = p;
					String outlink = o.substring(o.lastIndexOf("<") + 1, o.lastIndexOf(">")).trim();
					
					if(this.type.equals(PPVWalkWithRAMIndex.nodeType)){
						if(this.clsAndLink.containsKey(outlink)){
							if(this.clsAndLink.get(outlink).containsKey(this.preference)){
								nextMove.add(outlink);
							}else{
								tmp.add(outlink);	
							}
						}else{
							tmp.add(outlink);
						}
							
					}else {
						if(this.preference.equals(preference)){
							nextMove.add(outlink);	
						}else{
						tmp.add(outlink);	
						}	
					}
				}
			}
			if(nextMove.size()>0){
				preferenceRelated.put(url, nextMove);
			}else{
				preferenceNonRelated.put(url, tmp);
			}
		}
//		System.out.println(preferenceRelated);
		searcher.close();
		dir.close();
		clsAndLink.clear();
	}
	
	public void feedRAMWithSchema(String clsLinkIndexDir) throws IOException{
		clsAndLink=new HashMap<String,HashMap<String,Boolean>>();
		RAMDirectory dir=new RAMDirectory(FSDirectory.open(new File(clsLinkIndexDir)));
		IndexSearcher clsearcher = new IndexSearcher(dir);
		
		for (int i = 0; i < clsearcher.maxDoc(); i++) {
			String url=clsearcher.doc(i).get("url").trim();
			String c=clsearcher.doc(i).get("content").trim();
			String string=c.substring(c.indexOf("	")+1, c.length());
			String[] elements=string.split("\t");
			HashMap<String,Boolean> map=new HashMap<String, Boolean>();
			for(String element:elements){
				map.put(element.trim(), true);
			}
			clsAndLink.put(url, map);
		}
		clsearcher.close();
		dir.close();
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
			pool.submit(new PPVWithRAMMapRunable(this.preference, preferenceRelated, preferenceNonRelated, line,  bw, length));
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
		preferenceRelated.clear();
		preferenceNonRelated.clear();
		
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
		PERankWithRAMMap fg = new PERankWithRAMMap( graphIndex,  schemaIndex,  length,  cp,   type);
		fg.computeFingerPrints("D:/data/perank/query/2017ppvIniTest.txt", "D:/data/perank/query/2017ppv_cls_drug.txt");

	}

}
