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

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;


public class PERankSearcher {
	
	String graphIndex;
	String perankIndex;
	
	public PERankSearcher(String graphIndex, String perankIndex){
		this.graphIndex=graphIndex;
		this.perankIndex=perankIndex;
	}
	
	
	public void search(String query, String prefernce) throws IOException, ParseException{
		FSDirectory dir = FSDirectory.open(new File(graphIndex));
		IndexSearcher searcher = new IndexSearcher(dir);
		ScoreDoc[] results = searcher.search(
				QueryModel.getORQueryInOneDoc(query.toLowerCase()),
				searcher.maxDoc()).scoreDocs;
		ArrayList<String> links = new ArrayList<String>(); 
		
		if (results.length > 0) {

			for (final ScoreDoc doc : results) {
				String link = searcher.doc(doc.doc).get("url").trim();
				links.add(link);
			}
			searcher.close();
		}

		
		HashMap<String, SearchBean> totalBeans = new HashMap<String, SearchBean>();
		for (String link : links) {
			HashMap<String, SearchBean> beans = new HashMap<String, SearchBean>();
			IPPVSearcher mixsearcher = new IPPVSearcher(
					this.perankIndex);
			beans = mixsearcher.singlePreferenceWithoutWeightSearch(link,
					prefernce);
			for (Entry<String, SearchBean> bean : beans.entrySet()) {
				// if(bean.getKey().trim()!=link){ // this is test for
				// anchor

				if (!totalBeans.containsKey(bean.getKey())) {
					totalBeans.put(bean.getKey(), bean.getValue());
				} else {

					SearchBean old = totalBeans.get(bean.getKey());
					totalBeans.remove(bean.getKey());
					SearchBean newbean = new SearchBean();
					newbean.setLink(old.getLink());
					newbean.setOutLink(old.getOutLink());
					newbean.setPreference(old.getPreference());
					newbean.setLocalizedFingerPrint(old
							.getLocalizedFingerPrint()
							+ bean.getValue().getLocalizedFingerPrint());
					totalBeans.put(bean.getKey(), newbean);
				}
				
			}

		}

		// sort total beans

		Map.Entry<String, SearchBean>[] enties = getSortedHashtableByValueGeneral(totalBeans);
		
	}

	public static Map.Entry<String, SearchBean>[] getSortedHashtableByValueGeneral(
			Map<String, SearchBean> h) {

		Set set = h.entrySet();
		Map.Entry<String, SearchBean>[] entries = (Map.Entry<String, SearchBean>[]) set
				.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Double key1 = ((Map.Entry<String, SearchBean>) arg0).getValue()
						.getLocalizedFingerPrint();
				Double key2 = ((Map.Entry<String, SearchBean>) arg1).getValue()
						.getLocalizedFingerPrint();
				return key2.compareTo(key1);
			}
		});

		return entries;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
