package edu.ucsd.dbmi.perank.computation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class ComputationBatchJob {
	
	public void runJob(String configFile, String configNumberFile) throws Exception{
		
		
		ArrayList<Integer> list=readConfigNumber(configNumberFile);
				
		for (int i = 0; i < list.size(); i++) {
			int scale=list.get(i);
			ConfigReader config=new ConfigReader();
			config.read(configFile);
			String graphIndex=config.getGraphIndex();
			String schemaIndex=config.getSchemaIndex();
			int length=config.getLength();
			String type=config.getType();
			
			String ppvIniFile=config.getPpvIniFile()+"_"+scale;
			String ppvDir=config.getPpvDir()+"/"+scale;
			File dir=new File(ppvDir);
			dir.mkdirs();
			
			BufferedWriter bw1 =new BufferedWriter(new FileWriter(new File(config.getPreferneceIndexFile()+"_"+scale)));
			BufferedWriter bw2 =new BufferedWriter(new FileWriter(new File(config.getTimeInput()+"_"+scale)));
			
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
//				PERankWithRAMIndex fg = new PERankWithRAMIndex( graphIndex,  schemaIndex,  length,  preference,   type);
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
		
	}

	public ArrayList<Integer> readConfigNumber(String file) throws IOException{
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		 ArrayList<Integer> list=new ArrayList<>();
		while((line=br.readLine())!=null){
			list.add(Integer.valueOf(line.trim()));
		}
		br.close();
		return list;
	}
	
public void runJob2(String configFile) throws Exception{
		
		
		ArrayList<Integer> list=new ArrayList<>();
		list.add(200);
		list.add(400);
		list.add(500);
		list.add(600);
		list.add(800);
		list.add(1000);
		for (int i = 0; i < list.size(); i++) {
			int scale=list.get(i);
			ConfigReader config=new ConfigReader();
			config.read(configFile);
			String graphIndex=config.getGraphIndex();
			String schemaIndex=config.getSchemaIndex();
			int length=config.getLength();
			String type=config.getType();
			
			String ppvIniFile=config.getPpvIniFile()+"_"+scale;
			String ppvDir=config.getPpvDir()+"/"+scale;
			File dir=new File(ppvDir);
			dir.mkdirs();
			
			BufferedWriter bw1 =new BufferedWriter(new FileWriter(new File(config.getPreferneceIndexFile()+"_"+scale)));
			BufferedWriter bw2 =new BufferedWriter(new FileWriter(new File(config.getTimeInput()+"_"+scale)));
			
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
//				PERankWithRAMIndex fg = new PERankWithRAMIndex( graphIndex,  schemaIndex,  length,  preference,   type);
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
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ComputationBatchJob job=new ComputationBatchJob();
		job.runJob(args[0],args[1]);
		
	}

}
