package edu.ucsd.dbmi.perank.computation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigReader {
	
	private String graphIndex="";
	public String getGraphIndex() {
		return graphIndex;
	}

	public void setGraphIndex(String graphIndex) {
		this.graphIndex = graphIndex;
	}

	public String getSchemaIndex() {
		return schemaIndex;
	}

	public void setSchemaIndex(String schemaIndex) {
		this.schemaIndex = schemaIndex;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getTypeInput() {
		return typeInput;
	}

	public void setTypeInput(String typeInput) {
		this.typeInput = typeInput;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String schemaIndex="";
	private int length=10;
	private String typeInput="";
	private String type="";
	
	private String timeInput="";
	private String ppvIniFile="";
	private String preferneceIndexFile="";
	
	public String getPreferneceIndexFile() {
		return preferneceIndexFile;
	}

	public void setPreferneceIndexFile(String preferneceIndexFile) {
		this.preferneceIndexFile = preferneceIndexFile;
	}

	public String getPpvIniFile() {
		return ppvIniFile;
	}

	public void setPpvIniFile(String ppvIniFile) {
		this.ppvIniFile = ppvIniFile;
	}

	public String getPpvDir() {
		return ppvDir;
	}

	public void setPpvDir(String ppvDir) {
		this.ppvDir = ppvDir;
	}

	private String ppvDir="";
	
	public String getTimeInput() {
		return timeInput;
	}

	public void setTimeInput(String timeInput) {
		this.timeInput = timeInput;
	}

	public void read(String input) throws SAXException, IOException, ParserConfigurationException{
		File fXmlFile = new File(input);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		doc.getDocumentElement().normalize();

		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("config");
		
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);


			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				System.out.println("graphIndex : " + eElement.getElementsByTagName("graphIndex").item(0).getTextContent());
				System.out.println("schemaIndex : " + eElement.getElementsByTagName("schemaIndex").item(0).getTextContent());
				System.out.println("length : " + eElement.getElementsByTagName("length").item(0).getTextContent());
				System.out.println("typeInput : " + eElement.getElementsByTagName("typeInput").item(0).getTextContent());
				System.out.println("type : " + eElement.getElementsByTagName("type").item(0).getTextContent());
				System.out.println("timeInput : " + eElement.getElementsByTagName("timeInput").item(0).getTextContent());
				this.graphIndex=eElement.getElementsByTagName("graphIndex").item(0).getTextContent();
				this.schemaIndex=eElement.getElementsByTagName("schemaIndex").item(0).getTextContent();
				this.length=Integer.valueOf(eElement.getElementsByTagName("length").item(0).getTextContent());
				this.typeInput=eElement.getElementsByTagName("typeInput").item(0).getTextContent();
				this.type=eElement.getElementsByTagName("type").item(0).getTextContent();
				this.timeInput=eElement.getElementsByTagName("timeInput").item(0).getTextContent();
				this.ppvIniFile=eElement.getElementsByTagName("ppvIniFile").item(0).getTextContent();
				this.ppvDir=eElement.getElementsByTagName("ppvDir").item(0).getTextContent();
				this.preferneceIndexFile=eElement.getElementsByTagName("preferneceIndexFile").item(0).getTextContent();
				
			}
		}
	}

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		ConfigReader reader=new ConfigReader();
		reader.read("src/edu/ucsd/dbmi/perank/config_c.txt");
	}

}
