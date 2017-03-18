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


public class PPVWalkWithRAMIndex {
	RAMDirectory ddirectory;
	RAMDirectory cldirectory;
	static String nodeType="class";
	static String linkType="predicate";
	String type="";
	String preference;
	public PPVWalkWithRAMIndex(RAMDirectory ddirectory, RAMDirectory cldirectory, String preference, String type) throws MalformedURLException {
		this.ddirectory = ddirectory;
		this.cldirectory = cldirectory;
		this.preference= preference;
		this.type=type;
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
		
		Query query = new TermQuery(new Term("url", q));  
		
		IndexSearcher searcher = new IndexSearcher(ddirectory);
		TopDocs result =searcher.search(query, 1);
		ArrayList<String> nextMove = new ArrayList<String>();
		ArrayList<String> tmp = new ArrayList<String>();
		for(ScoreDoc doc:result.scoreDocs){
			String url = searcher.doc(doc.doc).get("url").trim();
			String[] content=searcher.doc(doc.doc).get("content").trim().split("	");
			String ntriples = content[1];
			InputStream   inputStream   =   new   ByteArrayInputStream(ntriples.getBytes());
			NxParser nxp = new NxParser(inputStream,false);
			IndexSearcher clsearcher = new IndexSearcher(this.cldirectory);
			while (nxp.hasNext()) {
				Node[] quard = (Node[]) nxp.next();
				
				String s = quard[0].toN3().trim();
				String p = quard[1].toN3().trim();
				String o = quard[2].toN3().trim();
				if(s.startsWith("<http://")&&o.startsWith("<http://")){
					String preference = p;
					String outlink = o.substring(o.lastIndexOf("<") + 1, o.lastIndexOf(">")).trim();
					
					if(this.type.equals(PPVWalkWithRAMIndex.nodeType)){
						
						ScoreDoc[] scoreDocs=clsearcher.search(new TermQuery(new Term("url",outlink)), 1).scoreDocs;
						for(ScoreDoc d:	scoreDocs){
							String c=clsearcher.doc(d.doc).get("content").trim();
							String string=c.substring(c.indexOf("	")+1, c.length());
							String[] elements=string.split("\t");
							HashMap<String,Boolean> map=new HashMap<String, Boolean>();
							for(String element:elements){
								map.put(element.trim(), true);
							}
							if(map.containsKey(this.preference)){
								nextMove.add(outlink);
							}else{
								tmp.add(outlink);	
							}
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
			clsearcher.close();
		}
		searcher.close();
		if (nextMove.size() == 0) {
			nextMove=tmp;
			bean.setFitPreference(false);
		}
		
		// System.out.println("same: "+samePreference);
		// System.out.println("different: "+differentPreference);
		String nextPostion = "";
		if (nextMove.size() != 0) {
			if(nextMove.size()==1){
				if(nextMove.get(0).equals(position)){
					nextPostion = position;
					bean.setKeepWalk(false);
				}
			}else{
				Random rand = new Random();
				int randidx = rand.nextInt(nextMove.size());
				nextPostion = nextMove.get(randidx);
				bean.setLastFitPosition(nextPostion);	
			}
			
		} else {
			for(ScoreDoc doc:result.scoreDocs){
				System.err.println(searcher.doc(doc.doc).get("content"));
			}
			nextPostion = position;
			bean.setKeepWalk(false);
		}

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