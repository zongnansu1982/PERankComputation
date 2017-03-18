package edu.ucsd.dbmi.perank.data;

import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PERankFingerPrintIni {
	public int sampleNum=0;
	public PERankFingerPrintIni(int sampleNumber){
		this.sampleNum=sampleNumber;
	}
	
	public void generateFPIni(String inFile,String outFile) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outFile)));
		BufferedReader br = new BufferedReader(new FileReader(new File(inFile)));
		String line=null;
		int num=0;
		while((line=br.readLine())!=null){
			for (int i = 0; i < sampleNum; i++) {
				num++;
				bw.write(line+" "+i+" "+line+"\r\n");
			}
			bw.flush();
		}
		bw.flush();
		bw.close();
		br.close();
		System.out.println(num);
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<Integer> list=new ArrayList<>();
		list.add(100);
		list.add(200);
		list.add(400);
		list.add(500);
		list.add(600);
		list.add(800);
		list.add(1000);
		
		for (int i = 0; i < list.size(); i++) {
			PERankFingerPrintIni Ini= new PERankFingerPrintIni(list.get(i));
			Ini.generateFPIni("D:/data/perank/query/allEntities.txt", "D:/data/perank/query/2017ppvIni_"+list.get(i)+".txt");	
//			Ini.generateFPIni("D:/data/perank/query/2017SearchedEntities.txt", "D:/data/perank/query/2017ppvIni_"+list.get(i)+".txt");	
		}
	}

}
