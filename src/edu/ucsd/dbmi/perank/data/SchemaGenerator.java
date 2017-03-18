package edu.ucsd.dbmi.perank.data;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;


public class SchemaGenerator {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String graphIndex="D:/data/perank/starIndex/dataGraph";
		String schemaIndex="D:/data/perank/starIndex/clsAndLink";
		String mixIndex="D:/data/perank/mixIndex/ppv20130420_diseases";
		 
		FSDirectory dir = FSDirectory.open(new File(graphIndex));
		IndexSearcher searcher = new IndexSearcher(dir);
		HashSet<String> cls= new HashSet<String>();
		HashSet<String> predicate= new HashSet<String>();
		BufferedWriter bw1= new BufferedWriter(new FileWriter(new File("D:/data/perank/query/2017clsAll.txt")));
		BufferedWriter bw2= new BufferedWriter(new FileWriter(new File("D:/data/perank/query/2017linkAll.txt")));
		
		int maxDoc=searcher.maxDoc();
		System.err.println(maxDoc);
		int j=0;
		for(int i=0;i<maxDoc;i++){
			String url = searcher.doc(i).get("url").trim();
			String[] content=searcher.doc(i).get("content").trim().split("	");
			String ntriples = content[1];
			
			InputStream   inputStream   =   new   ByteArrayInputStream(ntriples.getBytes());
			NxParser nxp = new NxParser(inputStream,false);
			while (nxp.hasNext()) {
				j++;
				Node[] quard = (Node[]) nxp.next();
				
				String s = quard[0].toN3().trim();
				String p = quard[1].toN3().trim();
				String o = quard[2].toN3().trim();
//				if(s.startsWith("<http://")&&o.startsWith("<http://")){
//					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")){
//						if(o.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>")){
//							predicate.add(s);
//						} else if(o.equals("<http://www.w3.org/2000/01/rdf-schema#Class>")){
//							cls.add(s);
//						} else{
//							cls.add(o);
//						}
//						
//					}else{
//						predicate.add(p);
//					}
//				}
				
				if(s.startsWith("<http://")&&o.startsWith("<http://")){
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")){
						cls.add(o);
					}else{
						predicate.add(p);
					}
				}
			}
			
		}
		System.err.println("triple size: "+j);
		System.err.println("size cls: "+cls.size());
		System.err.println("size predicate: "+predicate.size());
		searcher.close();
		for(String s:cls){
			bw1.write(s+"\n");
		}
		for(String p:predicate){
			bw2.write(p+"\n");
		}
		bw1.flush();
		bw2.flush();
		bw1.close();
		bw2.close();
	}

}
