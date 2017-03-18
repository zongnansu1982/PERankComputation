package edu.ucsd.dbmi.perank.seasrch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.function.*;


public class IPPVSearcher {

	public String mainIndexDir = null;

	public IPPVSearcher(String mainIndexDir) {
		this.mainIndexDir = mainIndexDir;

	}


	

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 */
	
		public HashMap<String,Double> mergePreferenceWithWeightSearch(String query,
			HashMap<String,Double> preferences, int returnUum) throws IOException, ParseException {

		System.out.println("Preference Search Use Default Weights start...");
		HashMap<String,Double> result= new HashMap<String, Double>();
		String standarquery = query.toLowerCase();
		HashMap<String,SearchBean> searchbeans = new HashMap<String,SearchBean>();
		

		IndexSearcher[] searchers = new IndexSearcher[new File(mainIndexDir)
				.listFiles().length];
		IndexReader[] readers = new IndexReader[new File(mainIndexDir)
				.listFiles().length];
		IndexSearcher searcher = null;
		IndexReader reader = null;
		int indexFileID = 0;
		for (File indexFile : new File(mainIndexDir).listFiles()) {
			FSDirectory dir = FSDirectory.open(indexFile);
			searcher = new IndexSearcher(dir);
			searchers[indexFileID] = searcher;

			indexFileID++;

		}

		MultiSearcher multisearcher = new MultiSearcher(searchers);

		ScoreDoc[] results = multisearcher.search(IPPVQueryModel
				.getWeightDesignatatedPreferenceQuery(standarquery, preferences),
				multisearcher.maxDoc()).scoreDocs;

		System.out.println("Number of hits: " + results.length);
		if (results.length > 0) {
			int docID = 1;
			for (final ScoreDoc doc : results) {

				Double score = (double) doc.score;
				String vector = multisearcher.doc(doc.doc).get("Vector").trim();
				String cls = multisearcher.doc(doc.doc).get("Class").trim();
				String outlink = multisearcher.doc(doc.doc).get("OutLink")
						.trim();
				String FingerPrint = multisearcher.doc(doc.doc).get("Score")
						.trim();
				docID++;
				for(Entry<String,Double> preference:preferences.entrySet()){
					if(cls.contains(preference.getKey().toLowerCase())){
					
					if(!searchbeans.containsKey(outlink)){
						SearchBean bean= new SearchBean();
						bean.setLink(vector);
						bean.setOutLink(outlink);
						bean.setLocalizedFingerPrint(Double.valueOf(FingerPrint)*preference.getValue());
						searchbeans.put(outlink, bean);
					}else{
						SearchBean bean= new SearchBean();
						bean.setLink(vector);
						bean.setOutLink(outlink);
						bean.setLocalizedFingerPrint(getFPFromMap(searchbeans,outlink)+Double.valueOf(FingerPrint)*preference.getValue());
						searchbeans.remove(outlink);
						searchbeans.put(outlink, bean);
						}
					}
				}
			}
			
		}

		multisearcher.close();
		Entry<String,SearchBean>[] entries=getSortedHashtableByValueGeneral(searchbeans);
		int i=0;
//		System.err.println(entries.length);
//		System.err.println(returnUum);
		for(Entry<String,SearchBean> entry:entries){
			if(i<returnUum){
				System.out.println(i+"  "+" score: "+entry.getValue().getLocalizedFingerPrint()+ " Outlink: "+entry.getValue().getOutLink()+" search entity: "+entry.getValue().getLink());
				result.put(entry.getValue().getOutLink(), entry.getValue().getLocalizedFingerPrint());
				i++;	
			}else{
				break;
			}
			
		}
		System.err.println(result.size());
		return result;
		
		}
		
		
		public void mergePreferenceWithoutWeightSearch(String query,
				ArrayList<String> preferences) throws IOException, ParseException {
			HashMap<String,SearchBean> searchbeans = new HashMap<String,SearchBean>();
			System.out.println("Preference Search Use Default Weights start...");

			String standarquery = query.toLowerCase();

			IndexSearcher[] searchers = new IndexSearcher[new File(mainIndexDir)
					.listFiles().length];
			IndexReader[] readers = new IndexReader[new File(mainIndexDir)
					.listFiles().length];
			IndexSearcher searcher = null;
			IndexReader reader = null;
			int indexFileID = 0;
			for (File indexFile : new File(mainIndexDir).listFiles()) {
				FSDirectory dir = FSDirectory.open(indexFile);
				searcher = new IndexSearcher(dir);
				searchers[indexFileID] = searcher;

				indexFileID++;

			}

			MultiSearcher multisearcher = new MultiSearcher(searchers);

			ScoreDoc[] results = multisearcher.search(IPPVQueryModel
					.getWeightAutoSignedPreferenceQuery(standarquery, preferences),
					multisearcher.maxDoc()).scoreDocs;

			System.out.println("Number of hits: " + results.length);
			if (results.length > 0) {
				int docID = 1;
				for (final ScoreDoc doc : results) {

					Double score = (double) doc.score;
					String vector = multisearcher.doc(doc.doc).get("Vector").trim();
					String cls = multisearcher.doc(doc.doc).get("Class").trim();
					String outlink = multisearcher.doc(doc.doc).get("OutLink")
							.trim();
					String FingerPrint = multisearcher.doc(doc.doc).get("Score")
							.trim();
					docID++;
//					System.out.println("FingerPrints: " + FingerPrint + "link: "
//							+ vector + " outlink: " + outlink + " class: " + cls
//							+ " doc score: " + score);
					
					for(String preference:preferences){
						if(cls.contains(preference.toLowerCase())){
						
						if(!searchbeans.containsKey(outlink)){
							SearchBean bean= new SearchBean();
							bean.setLink(vector);
							bean.setOutLink(outlink);
							bean.setLocalizedFingerPrint(Double.valueOf(FingerPrint)/preferences.size());
							searchbeans.put(outlink, bean);
						}else{
							SearchBean bean= new SearchBean();
							bean.setLink(vector);
							bean.setOutLink(outlink);
							bean.setLocalizedFingerPrint(getFPFromMap(searchbeans,outlink)+Double.valueOf(FingerPrint)/preferences.size());
							searchbeans.remove(outlink);
							searchbeans.put(outlink, bean);
							}
						}
					}

				}
				multisearcher.close();

				}
			
			Entry<String,SearchBean>[] entries=getSortedHashtableByValueGeneral(searchbeans);
			
			for(Entry<String,SearchBean> entry:entries){
				System.out.println(" score: "+entry.getValue().getLocalizedFingerPrint()+ " Outlink: "+entry.getValue().getOutLink()+" search entity: "+entry.getValue().getLink());
				
			}
			
		}
		public HashMap<String,SearchBean> singlePreferenceWithoutWeightSearch(String query,
				String preference,int returnNum) throws IOException, ParseException {
			HashMap<String,SearchBean> searchbeans = new HashMap<String,SearchBean>();
			ArrayList<String>	 preferences= new ArrayList<String>();

			IndexSearcher searcher = null; 
				FSDirectory dir = FSDirectory.open(new File(mainIndexDir));
				searcher = new IndexSearcher(dir);
				
		
			Sort sort = new Sort(new SortField("score", SortField.DOUBLE, true));
			ScoreDoc[] results = searcher.search(IPPVQueryModel
					.getSinglePreferenceQuery(query, preference),
					returnNum,sort).scoreDocs;
			System.out.println("num of hits: "+results.length);
			if (results.length > 0) {
				for (final ScoreDoc doc : results) {

					String vector = searcher.doc(doc.doc).get("url").trim();
					String cls = searcher.doc(doc.doc).get("preference").trim();
					String outlink = searcher.doc(doc.doc).get("ppv")
							.trim();
					String FingerPrint = searcher.doc(doc.doc).get("score")
							.trim();
					SearchBean bean = new SearchBean();
					bean.setLink(vector);
					bean.setLocalizedFingerPrint(Double.valueOf(FingerPrint));
					bean.setOutLink(outlink);
					bean.setPreference(cls);
					searchbeans.put(outlink, bean);
					System.out.println(vector+" "+cls+" "+outlink+" "+FingerPrint);
					if(!preferences.contains(cls)){
						preferences.add(cls);
					}
					
					
				}
				
				}
			searcher.close();
//			System.out.println(preferences);
			return searchbeans;
		}
		
		
		public HashMap<String,SearchBean> singlePreferenceWithoutWeightSearch(String query,
				String preference) throws IOException, ParseException {
			HashMap<String,SearchBean> searchbeans = new HashMap<String,SearchBean>();
			ArrayList<String>	 preferences= new ArrayList<String>();

			IndexSearcher searcher = null; 
				FSDirectory dir = FSDirectory.open(new File(mainIndexDir));
				searcher = new IndexSearcher(dir);
				
		
			Sort sort = new Sort(new SortField("score", SortField.DOUBLE, true));
			ScoreDoc[] results = searcher.search(IPPVQueryModel
					.getSinglePreferenceQuery(query, preference),
					searcher.maxDoc(),sort).scoreDocs;
//			System.out.println("num of hits: "+results.length);
			if (results.length > 0) {
				for (final ScoreDoc doc : results) {

					String vector = searcher.doc(doc.doc).get("url").trim();
					String cls = searcher.doc(doc.doc).get("preference").trim();
					String outlink = searcher.doc(doc.doc).get("ppv")
							.trim();
					String FingerPrint = searcher.doc(doc.doc).get("score")
							.trim();
					SearchBean bean = new SearchBean();
					bean.setLink(vector);
					bean.setLocalizedFingerPrint(Double.valueOf(FingerPrint));
					bean.setOutLink(outlink);
					bean.setPreference(cls);
					searchbeans.put(outlink, bean);
//					System.out.println(vector+" "+cls+" "+outlink+" "+FingerPrint);
					if(!preferences.contains(cls)){
						preferences.add(cls);
					}
				}
				}
			searcher.close();
//			System.out.println(preferences);
			return searchbeans;
		}
		
		public static Double getFPFromMap(HashMap<String,SearchBean> map,String key){
			Double fingerprint=0.0;
			fingerprint=map.get(key).getLocalizedFingerPrint();
			map.remove(key);
			return fingerprint;
		}
		
		public static Map.Entry<String,SearchBean>[] getSortedHashtableByValueGeneral(Map<String,SearchBean> h) {
			 
			 
			   Set set = h.entrySet();  
		       Map.Entry<String,SearchBean>[] entries = (Map.Entry<String,SearchBean>[]) set.toArray(new Map.Entry[set  
		               .size()]);  
		       Arrays.sort(entries, new Comparator() {  
		           public int compare(Object arg0, Object arg1) {  
		               Double key1 = ((Map.Entry<String,SearchBean>) arg0).getValue().getLocalizedFingerPrint();  
		               Double key2 = ((Map.Entry<String,SearchBean>) arg1).getValue().getLocalizedFingerPrint();  
		               return key2.compareTo(key1);  
		           }  
		       });  
		  
		       return entries;  
		   }  
		
		public Integer keyWordSearch(String query,
				int returnNum) throws IOException, ParseException {


			IndexSearcher searcher = null;
				FSDirectory dir = FSDirectory.open(new File(mainIndexDir));
				searcher = new IndexSearcher(dir);


			Sort sort = new Sort(new SortField("score", SortField.DOUBLE, true));
			TermQuery tq = new TermQuery(new Term("url", query));
//			ScoreDoc[] results = searcher.search(tq,
//					returnNum,sort).scoreDocs;
			ScoreDoc[] results = searcher.search(tq,
					returnNum,sort).scoreDocs;
			System.out.println("Number of hits: " + results.length);
			for(ScoreDoc doc:results){
				Document document=searcher.doc(doc.doc);
				System.out.println(document.getField("url")+" "+document.getField("preference")+" "+document.getField("ppv")+" "+document.getField("score"));
			}
			
			return results.length;
		}

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
	}

}
