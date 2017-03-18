package edu.ucsd.dbmi.perank.seasrch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.sindice.siren.search.SirenCellQuery;
import org.sindice.siren.search.SirenTermQuery;
import org.sindice.siren.search.SirenTupleClause;
import org.sindice.siren.search.SirenTupleQuery;

public class QueryModel {
	
	
	
	public static Query getNormalizedQuery(String queryword) throws ParseException {
	 	System.out.println("Keyword : " + queryword);
	 	AnyURIAnalyzer anyURIAnalyzer = new AnyURIAnalyzer(Version.LUCENE_34);
		anyURIAnalyzer.setUriNormalisation(URINormalisation.LOCALNAME);
		BooleanQuery bq= new BooleanQuery();
	 	if(queryword.contains(" ")){
			String[] queries=queryword.split(" ");
			for(String q:queries){
					Query query= new TermQuery(new Term("content",q));
					bq.add(query, BooleanClause.Occur.SHOULD);
			}
			
		}else{
			Query query= new TermQuery(new Term("content",queryword));
			bq.add(query, BooleanClause.Occur.MUST);
		}
	    return bq;
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
	public static Query getORQueryInOneDoc(String inputSeparatedBySpace){
		String[] input=inputSeparatedBySpace.split(" ");
		final BooleanQuery booleanQuery = new BooleanQuery();
		
		for(String in:input){
			final SirenCellQuery cq = new SirenCellQuery();
			final SirenTupleQuery tuple= new SirenTupleQuery();
			SirenTermQuery term= new SirenTermQuery(new Term("content", in));
			cq.setQuery(term);
			tuple.add(cq,SirenTupleClause.Occur.MUST);
			booleanQuery.add(tuple, Occur.SHOULD);
		}
		
		
		return booleanQuery;
	}
	
	public static Query checkRelevant(String query, String url){
			final BooleanQuery booleanQuery = new BooleanQuery();
			
			for(String in:query.split(" ")){
				BooleanQuery booleanQuery1 = new BooleanQuery();
				TermQuery tq1= new TermQuery(new Term("url",url));
				booleanQuery1.add(tq1, Occur.MUST);	
				TermQuery tq2= new TermQuery(new Term("content",in));
				booleanQuery1.add(tq2, Occur.MUST);	
				booleanQuery.add(booleanQuery1, Occur.SHOULD);
			}
		
		return booleanQuery;
	}
	
	
	
	public static Query getPreferenceFit(String url,String preference){
		 TermQuery tq1 = new TermQuery(new Term("url", url));
		 TermQuery tq2 = new TermQuery(new Term("content", preference));
		 BooleanQuery bq = new BooleanQuery();
		 bq.add(tq1, Occur.MUST);
		 bq.add(tq2, Occur.MUST);
		return bq;
	}
	
	
	public static Query getORQuery(String inputSeparatedBySpace){
		System.out.println("OR Query Model(one doc)");
		System.out.println("Keyword : " + inputSeparatedBySpace);
		String[] input=inputSeparatedBySpace.split(" ");
		final BooleanQuery booleanQuery = new BooleanQuery();
		
		for(String in:input){
			TermQuery tq = new TermQuery(new Term("content", in) );
		
			booleanQuery.add(tq, Occur.SHOULD);
		}
		
		
		return booleanQuery;
	}
}