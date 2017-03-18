package edu.ucsd.dbmi.perank.seasrch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;
import org.sindice.siren.analysis.AnyURIAnalyzer;
import org.sindice.siren.analysis.TupleAnalyzer;
import org.sindice.siren.analysis.AnyURIAnalyzer.URINormalisation;

public class IPPVQueryModel {
	
	
	

		public static Query getWeightDesignatatedPreferenceQuery(String queryword, HashMap<String,Double> preferences) throws ParseException, IOException {
			System.out.println("with preference and weight setting, start weight designated query...");
			System.out.println("Keyword : " + queryword);
			System.out.println("Preferences: "+preferences);
			BooleanQuery bq= new BooleanQuery();

			AnyURIAnalyzer anyURIAnalyzer = new AnyURIAnalyzer(Version.LUCENE_34);
			
			anyURIAnalyzer.setUriNormalisation(URINormalisation.LOCALNAME);

			
			if(queryword.contains(" ")){
				String[] queries=queryword.split(" ");
				for(String q:queries){
					for(Entry<String, Double> preference:preferences.entrySet()){
						BooleanClause.Occur[] clauses= {BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
						String[] fields={"Vector","Class"};
						String[] querys={q,preference.getKey().toLowerCase()};
						
						Query query = MultiFieldQueryParser.parse(Version.LUCENE_34,querys, fields, clauses,
								anyURIAnalyzer);	
						
						bq.add(query,BooleanClause.Occur.SHOULD);
//						bq.setBoost(Float.valueOf(preference.getValue().toString()));
					}	
				}
				
			}else{
				for(Entry<String, Double> preference:preferences.entrySet()){
					BooleanClause.Occur[] clauses= {BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
					String[] fields={"Vector","Class"};
					String[] querys={queryword,preference.getKey().toLowerCase()};
					
					Query query = MultiFieldQueryParser.parse(Version.LUCENE_34,querys, fields, clauses,
							anyURIAnalyzer);	
					bq.add(query,BooleanClause.Occur.SHOULD);
//					bq.setBoost(Float.valueOf(preference.getValue().toString()));
					 
				}	
				
				
			}
			
			
			
			return bq;
	  }
		
		public static Query getWeightAutoSignedPreferenceQuery( String queryword,ArrayList<String> preferences) throws ParseException {
			System.out.println("with preference and without weight setting, start auto wight  query...");
			System.out.println("Keyword : " + queryword);
			System.out.println("preference : " + preferences);
			BooleanQuery bq= new BooleanQuery();
			AnyURIAnalyzer anyURIAnalyzer = new AnyURIAnalyzer(Version.LUCENE_34);
			anyURIAnalyzer.setUriNormalisation(URINormalisation.LOCALNAME);
			
			if(queryword.contains(" ")){
				String[] queries=queryword.split(" ");
				for(String q:queries){
					for(String preference:preferences){
						BooleanClause.Occur[] clauses= {BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
						String[] fields={"Vector","Class"};
						String[] querys={q,preference.toLowerCase()};
						
						Query query = MultiFieldQueryParser.parse(Version.LUCENE_34,querys, fields, clauses,
								anyURIAnalyzer);
						bq.add(query,BooleanClause.Occur.SHOULD);
						 
					}	
				}
				
			}else{
				for(String preference:preferences){
					BooleanClause.Occur[] clauses= {BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
					String[] fields={"Vector","Class"};
					String[] querys={queryword,preference.toLowerCase()};
					
					Query query = MultiFieldQueryParser.parse(Version.LUCENE_34,querys, fields, clauses,
							anyURIAnalyzer);	
					
					bq.add(query,BooleanClause.Occur.SHOULD);
					
					
				}	
				
				
			}
			
			
			
			return bq;
		}
		
	public static Query getNormalizedQuery(String queryword) throws ParseException {
	 	System.out.println("With out preference, Start normalized query...");
	 	System.out.println("Keyword : " + queryword);
	 	AnyURIAnalyzer anyURIAnalyzer = new AnyURIAnalyzer(Version.LUCENE_34);
		anyURIAnalyzer.setUriNormalisation(URINormalisation.LOCALNAME);
		BooleanQuery bq= new BooleanQuery();
	 	if(queryword.contains(" ")){
			String[] queries=queryword.split(" ");
			for(String q:queries){
				
					Query query= new TermQuery(new Term("Vector",q));
					bq.add(query, BooleanClause.Occur.SHOULD);
			}
			
		}else{
			Query query= new TermQuery(new Term("Vector",queryword));
			bq.add(query, BooleanClause.Occur.MUST);
		}
	    return bq;
	  }
	
	public static Query getFromUrl(String url) throws ParseException {
	 	System.out.println("With out preference, Start normalized query...");
	 	System.out.println("Keyword : " + url);
	 	
		Query query= new TermQuery(new Term("url",url));
		
	    return query;
	  }
	
	public static Query getPreferenceQuery(String queryword, ArrayList<String> preferences) throws ParseException, IOException {
		System.out.println("with preference and no weight setting, start no weight designated object query...");
		System.out.println("Keyword : " + queryword);
		System.out.println("Preferences: "+preferences);
		BooleanQuery bq= new BooleanQuery();

		AnyURIAnalyzer anyURIAnalyzer = new AnyURIAnalyzer(Version.LUCENE_34);
		
		anyURIAnalyzer.setUriNormalisation(URINormalisation.LOCALNAME);

		
		if(queryword.contains(" ")){
			String[] queries=queryword.split(" ");
			for(String q:queries){
				for(String preference:preferences){
					BooleanClause.Occur[] clauses= {BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
					String[] fields={"Vector","OutLink"};
					String[] querys={q,preference.toLowerCase()};
					
					Query query = MultiFieldQueryParser.parse(Version.LUCENE_34,querys, fields, clauses,
							anyURIAnalyzer);	
					
					bq.add(query,BooleanClause.Occur.SHOULD);
//					bq.setBoost(Float.valueOf(preference.getValue().toString()));
				}	
			}
			
		}else{
			for(String preference:preferences){
				BooleanClause.Occur[] clauses= {BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
				String[] fields={"Vector","OutLink"};
				String[] querys={queryword,preference.toLowerCase()};
				
				Query query = MultiFieldQueryParser.parse(Version.LUCENE_34,querys, fields, clauses,
						anyURIAnalyzer);	
				bq.add(query,BooleanClause.Occur.SHOULD);
//				bq.setBoost(Float.valueOf(preference.getValue().toString()));
				 
			}	
			
			
		}
		return bq;
  }
	
	
	public static Query getSinglePreferenceQuery(String url, String preference){
		
//		doc.add(new Field("url", url, Field.Store.YES,
//				Field.Index.NOT_ANALYZED));
//		doc.add(new Field("preference", preference, Field.Store.YES,
//				Field.Index.ANALYZED));
//		doc.add(new Field("ppv", ppv, Field.Store.YES, Field.Index.NOT_ANALYZED));
//		doc.add(new Field("score", String.valueOf(score), Field.Store.YES, Field.Index.NOT_ANALYZED));
//		System.out.println(url+" "+preference);
		BooleanQuery bq = new BooleanQuery();
		if(!preference.contains(" ")){
		TermQuery tq1= new TermQuery(new Term("url",url));
		TermQuery tq2= new TermQuery(new Term("preference",preference));
		bq.add(tq1, Occur.MUST);
		bq.add(tq2, Occur.MUST);
		}else{
			for(String p:preference.split(" ")){
				BooleanQuery bq1 = new BooleanQuery();
				TermQuery tq1= new TermQuery(new Term("url"));
				TermQuery tq2= new TermQuery(new Term("preference",p));
				bq1.add(tq1, Occur.MUST);
				bq1.add(tq2, Occur.MUST);
				bq.add(bq1, Occur.SHOULD);
			}
		}
		
		return bq;
		
	}

}