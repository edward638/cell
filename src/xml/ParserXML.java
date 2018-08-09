package xml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.w3c.dom.*;
import javax.xml.parsers.*;

/**
 * ParserXML class, designed to parse XML files to interpret for simulation
 * @author Edward Zhuang
 *	Help received from:
 *	https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 */
public class ParserXML {
	
	private Document doc;
	private static final String DEFAULT_RESOURCE_PATH = "resources/";
	private static final String PARSER_TERMS = "ParserTerms";
	private ResourceBundle myResources;

	/**
	 * Constructor for ParserXML
	 * @param file XML file to be parsed
	 */
	public ParserXML(File file) {
		myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PATH + PARSER_TERMS);
		try {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(file);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
		doc.getDocumentElement().normalize();
	}
	
	/**
	 * Finds cell types and prints them out
	 */
	public void findCellTypes() {
		NodeList typeList = doc.getElementsByTagName(myResources.getString("STATE"));
		for (int x = 0; x < typeList.getLength(); x++) {
			Node typeNode = typeList.item(x);
			System.out.println("\nCurrent Element:" + typeNode.getNodeName());
			Element eElement = (Element) typeNode;
			System.out.println("Type: " + eElement.getAttribute(myResources.getString("CATEGORY")));
			System.out.println("Name: " + eElement.getElementsByTagName(myResources.getString("LABEL")).item(0).getTextContent());
		}	
	}
	
	/**
	 * 
	 * @param cellNode cellNode which contains x and y coordinates of a cell
	 * @return returns the coordinates of a cell
	 */
	public int[] getCellInfo(Node cellNode) {
		Element eElement = (Element) cellNode;
		int xCoord = Integer.parseInt(eElement.getElementsByTagName(myResources.getString("XCOORD")).item(0).getTextContent());
		int yCoord = Integer.parseInt(eElement.getElementsByTagName(myResources.getString("YCOORD")).item(0).getTextContent());					
		int[] cell = {xCoord, yCoord};
		return cell;
	}
	
	/**
	 * checks to see if a simulation is random
	 * @return returns true if the XML file calls for a random simulation, false otherwise
	 */
	public boolean isRandom() {
		NodeList checkRandom = doc.getElementsByTagName(myResources.getString("CELL_LIST"));
		Node isRandom = checkRandom.item(0);
		Element eElement = (Element) isRandom;
		return (!Boolean.parseBoolean(eElement.getAttribute(myResources.getString("IS_ON"))));
	}
	
	/**
	 * Collects all the cells from an XML file, uses getCellInfo
	 * @return returns a map with key of cell type and value of int[][] which contains list of coordinates of that type of cell
	 */
	public Map<String, int[][]> getAllCells() {
		
		Map<String, int[][]> cellMap = new HashMap<>();		
			
		NodeList types = doc.getElementsByTagName(myResources.getString("TYPE"));
		for (int i = 0; i < types.getLength(); i++) {
			Node node = types.item(i);
			Element eElement = (Element) node;
			String temp = eElement.getAttribute(myResources.getString("ID"));
			NodeList childNodes = eElement.getElementsByTagName(myResources.getString("CELL"));
			int[][] tempArray = new int[childNodes.getLength()][];
			for (int x = 0; x < childNodes.getLength(); x++) {
				Node cellNode = childNodes.item(x);
				tempArray[x] = getCellInfo(cellNode);
			}
			cellMap.put(temp, tempArray);
		}
		return cellMap;
	}
	
	/**
	 * Gets the parameters of an XML file
	 * @return returns map with keys as parameters and values as doubles
	 */
	public Map<String, Double> getParameters(){
		Map<String, Double> paramMap = new HashMap<>();
		NodeList params = doc.getElementsByTagName(myResources.getString("PARAMETERS"));
		for (int i = 0; i < params.getLength(); i++) {
			Node node = params.item(i);
			Element eElement = (Element) node;
			String temp = eElement.getAttribute(myResources.getString("ID"));
			Double tempDouble = Double.parseDouble(eElement.getElementsByTagName(myResources.getString("VALUE")).item(0).getTextContent());
			paramMap.put(temp, tempDouble);
		}
		return paramMap;
	}

	
	/**
	 * Gets the type of simulation
	 * @return String simulation type
	 */
	public String getSimulationType() {
		return doc.getDocumentElement().getNodeName();
	}
	
	/**
	 * Gets the dimensions of simulation
	 * @return int[] containing width and height of a simulation
	 */
	public int[] getDimensions(){
			NodeList dimensions = doc.getElementsByTagName(myResources.getString("DIMENSIONS"));
			Node dimensionsNode = dimensions.item(0);
			Element eElement = (Element) dimensionsNode;						
			int xsize = Integer.parseInt(eElement.getElementsByTagName(myResources.getString("XSIZE")).item(0).getTextContent());
			int ysize = Integer.parseInt(eElement.getElementsByTagName(myResources.getString("YSIZE")).item(0).getTextContent());
			int[] dim = {xsize, ysize};
			return dim;
	}	
}