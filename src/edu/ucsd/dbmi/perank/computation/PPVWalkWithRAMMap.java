package edu.ucsd.dbmi.perank.computation;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import edu.ucsd.dbmi.perank.bean.RandomWalkBean;


public class PPVWalkWithRAMMap {
	HashMap<String,ArrayList<String>> preferenceRelated;
	HashMap<String,ArrayList<String>> preferenceNonRelated;
	static String nodeType="class";
	static String linkType="predicate";
	public PPVWalkWithRAMMap(HashMap<String,ArrayList<String>> preferenceRelated, HashMap<String,ArrayList<String>> preferenceNonRelated) throws MalformedURLException {
		this.preferenceRelated = preferenceRelated;
		this.preferenceNonRelated = preferenceNonRelated;
	}

	/**
	 * notcie node with http:// searches link: without http:// searches
	 * 
	 * @param bean
	 * 
	 * @throws SolrServerException
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */

	public void getNext(RandomWalkBean bean) throws  CorruptIndexException, IOException {
		String position = bean.getCurrentPosition();
		// String
		// q=position.substring(position.indexOf(":")+3,position.length());
		
		String q=position;
		
		ArrayList<String> nextMove = new ArrayList<String>();
		
		if(this.preferenceRelated.containsKey(q)){
			nextMove=preferenceRelated.get(q);
		}else if (this.preferenceNonRelated.containsKey(q)){
			nextMove=preferenceNonRelated.get(q);
			bean.setFitPreference(false);
		}
		
		// System.out.println("same: "+samePreference);
		// System.out.println("different: "+differentPreference);
		String nextPostion = "";
		if (nextMove.size() > 0) {
			if(nextMove.size()==1){
				if(nextMove.get(0).equals(position)){
					nextPostion = position;
					bean.setKeepWalk(false);
				}else{
					nextPostion = nextMove.get(0);
				}
			}else{
				Random rand = new Random();
				int randidx = rand.nextInt(nextMove.size());
				nextPostion = nextMove.get(randidx);
			}
			
		} else {
			nextPostion = position;
			bean.setKeepWalk(false);
		}
		
		bean.setLastFitPosition(nextPostion);
		bean.setCurrentPosition(nextPostion);
		bean.setPreviousPosition(position);
		bean.setSteps(bean.getSteps() + 1);
//		System.out.println(bean.getSteps()+" "+nextMove.size());
	}

	public static HashMap<String, Integer> statics(ArrayList<String> S) {
		HashMap<String, Integer> re = new HashMap<String, Integer>();
		for (String s : S) {
			if (!re.containsKey(s)) {
				re.put(s, 1);
			} else {
				int num = re.get(s) + 1;
				re.remove(s);
				re.put(s, num);
			}
		}
		return re;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		

	}
}