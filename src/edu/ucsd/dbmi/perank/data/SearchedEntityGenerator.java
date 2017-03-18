package edu.ucsd.dbmi.perank.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;

import edu.ucsd.dbmi.perank.seasrch.QueryModel;



/**
 * generate the links from the query for computing fingerPrint
 * @author nansu
 *
 */
public class SearchedEntityGenerator {
	
	public String mainIndexDir = null;
	
	public SearchedEntityGenerator(String index){
	
	this.mainIndexDir=index;
		
	}
		public void generate(String queryFile,String outFile) throws IOException, ParseException{
			
//			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outFile)));
			System.out.println("generating links");
			ArrayList<String> links= new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(new File(queryFile)));
			String line=null;
			ArrayList<String> queries= new ArrayList<String> ();
			while((line=br.readLine())!=null){
				String[] v=line.split(">");
				queries.add(v[1].trim().replaceAll("_", " "));
			}
			for(String query:queries){
				String standarquery = query.toLowerCase();
				
				System.out.println("query: "+standarquery);

				FSDirectory dir = FSDirectory.open(new File(mainIndexDir));
				IndexSearcher searcher = new IndexSearcher(dir);
				ScoreDoc[] results =	searcher.search(QueryModel.getORQueryInOneDoc(standarquery), searcher.maxDoc()).scoreDocs;
				System.out.println("Number of hits: " + results.length);
				if (results.length > 0) {
					
					int docID = 1;
					for (final ScoreDoc doc : results) {
		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
						String link = searcher.doc(doc.doc).get("url").trim();
//						System.out.println(link);
//						System.out.println(searcher.doc(doc.doc).get("content").trim());
						docID++;
						if(!links.contains(link)){
							links.add(link);	
						}
					}
					searcher.close();

				}else{
					System.err.println("empty results, searched by :"+ query);
				}

			}
			System.out.println("link size: "+links.size());
//			for(String link:links){
//				bw.write(link+"\r\n");
//			}
//			bw.flush();
//			bw.close();
			
		}
		
	/*
	 * CD11A_HUMAN
	 */
		public void queryTest(String query) throws IOException{
				String standarquery = query.toLowerCase();
				
				System.out.println("query: "+standarquery);

				FSDirectory dir = FSDirectory.open(new File(mainIndexDir));
				IndexSearcher searcher = new IndexSearcher(dir);
				ScoreDoc[] results =	searcher.search(new TermQuery(new Term("url",query)), 10).scoreDocs;
				System.out.println("Number of hits: " + results.length);
				if (results.length > 0) {
					
					for (final ScoreDoc doc : results) {
		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
						String content = searcher.doc(doc.doc).get("content").trim();
						String url = searcher.doc(doc.doc).get("url").trim();
						System.out.println(url);
						System.out.println(content);
					}
					searcher.close();

				}else{
					System.err.println("empty results, searched by :"+ query);
				}

			
		}
		
		public void checkContent(String file, String url) throws IOException{
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line=null;
			while((line=br.readLine())!=null){
				String[] v=line.split("<");
				if(v[0].trim().equals(url)){
					System.out.println(line);
				}
			}
			br.close();
		}

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		SearchedEntityGenerator generator= new SearchedEntityGenerator("D:/data/perank/starIndex/dataGraph");
		generator.generate("D:/data/perank/query/2017allQuery.txt", "D:/data/perank/query/2017SearchedEntities.txt");
		
	}

}
