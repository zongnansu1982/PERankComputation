package edu.ucsd.dbmi.perank.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.sindice.siren.analysis.AnyURIAnalyzer;
import org.sindice.siren.analysis.TupleAnalyzer;
import org.sindice.siren.analysis.AnyURIAnalyzer.URINormalisation;
/**
 * sharing with topic sensitive 
 * @author nansu
 *
 */
public class IPPVIndexer {

	public Directory dir;

	public IndexWriter writer;

	public IndexReader reader;

	public TupleAnalyzer analyzer;

	public void mainIndex(String indir, String outdir) throws IOException {
		File f = new File(outdir);
		f.mkdirs();
		dir = FSDirectory.open(f);
		Analyzer stringAnalyzer = new StandardAnalyzer(Version.LUCENE_34);

		AnyURIAnalyzer anyURIAnalyzer = new AnyURIAnalyzer(Version.LUCENE_34);
		anyURIAnalyzer.setUriNormalisation(URINormalisation.LOCALNAME);

		analyzer = new TupleAnalyzer(Version.LUCENE_34, stringAnalyzer,
				anyURIAnalyzer);
//		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34,
//				analyzer);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34,
				anyURIAnalyzer);
		writer = new IndexWriter(dir, config);
		if(new File(indir).isFile()){
			fingerPrintIndex(new File(indir), writer);
		}else{
		for (File in : new File(indir).listFiles()) {
			fingerPrintIndex(in, writer);
			System.out.println("indexing file: "+in.getAbsolutePath());
		}
		}
		writer.commit();
		writer.close();
		System.out.println("index finish");

	}

	public void fingerPrintIndex(File file, IndexWriter writer)
			throws CorruptIndexException, LockObtainFailedException,
			IOException {

		InputStream in = new FileInputStream(file);
		InputStreamReader ir = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(ir);
		String line=null;

		ArrayList<String> drugs = new ArrayList<String>();
		//<StartNodes><iterationClass><Endnodes> Score
		while ((line = br.readLine()) != null) {
			String[] vs=line.trim().split("\t"); //this is tab
			String url=vs[0].split(" ")[0];
			String preference=vs[0].split(" ")[1];
			String ppv=vs[0].split(" ")[2];
			Double score=Double.valueOf(vs[1].trim());
			
			Document doc = new Document();

			System.out.println(url+" "+preference+" "+ppv+" "+score);
			doc.add(new Field("url", url, Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			doc.add(new Field("preference", preference, Field.Store.YES,
					Field.Index.ANALYZED));
			doc.add(new Field("ppv", ppv, Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("score", String.valueOf(score), Field.Store.YES, Field.Index.NOT_ANALYZED));
			writer.addDocument(doc);
		}
		writer.commit();
		br.close();
		ir.close();

	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	}

}
