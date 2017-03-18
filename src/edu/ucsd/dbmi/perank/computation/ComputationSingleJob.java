package edu.ucsd.dbmi.perank.computation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;

public class ComputationSingleJob {
	
	public void runJob(String configFile) throws Exception{
		
		ConfigReader config=new ConfigReader();
		config.read(configFile);
		String graphIndex=config.getGraphIndex();
		String schemaIndex=config.getSchemaIndex();
		int length=config.getLength();
		String type=config.getType();
		
		String ppvIniFile=config.getPpvIniFile();
		String ppvDir=config.getPpvDir();
		
		BufferedWriter bw1 =new BufferedWriter(new FileWriter(new File(config.getPreferneceIndexFile())));
		BufferedWriter bw2 =new BufferedWriter(new FileWriter(new File(config.getTimeInput())));
		HashSet<String> preferences=new HashSet<String>();
		BufferedReader br=new BufferedReader(new FileReader(new File(config.getTypeInput())));
		String line=null;
		while((line=br.readLine())!=null){
			preferences.add(line);
		}
		int id=0;
		long allTime=0;
		for(String preference:preferences){
			long start=System.currentTimeMillis();
//			PERankWithRAMIndex fg = new PERankWithRAMIndex( graphIndex,  schemaIndex,  length,  preference,   type);
			PERankWithRAMMap fg = new PERankWithRAMMap( graphIndex,  schemaIndex,  length,  preference,   type);
			fg.computeFingerPrints(ppvIniFile, ppvDir+"/"+id+".txt");	
			bw1.write(id+"\t"+preference+"\n");
			long end=System.currentTimeMillis();
			id++;
			allTime+=end-start;
			bw2.write(id+"\t"+preference+"\t"+(end-start)+"\t"+allTime+"\n");
			bw2.flush();
			bw1.flush();
		}
		bw1.flush();
		bw1.close();
		bw2.flush();
		bw2.close();
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ComputationSingleJob job=new ComputationSingleJob();
		job.runJob(args[0]);
		
		
	}

}
