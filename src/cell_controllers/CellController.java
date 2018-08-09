package cell_controllers;

import java.util.HashMap;
import java.util.Map;

import cells.Cell;
import javafx.scene.paint.Color;
import shapes.HexagonNeighborFinder;
import shapes.NeighborFinder;
import shapes.SquareNeighborFinder;
import xml.WriterException;

/**
 * 
 * @author jeffreyli, edwardzhuang
 * the superclass for organising the grid of cells
 */
public abstract class CellController {

	protected Cell[][] cellGrid;
	protected int xSize;
	protected int ySize;
	protected Map<String, Map<Color, Integer>> data;
	protected boolean isRandom;
	protected boolean torroidal;

	/**
	 * 
	 * @param dimensions
	 *            the dimensions of the cellGrid
	 */
	public CellController(int[] dimensions, boolean random) {
		xSize = dimensions[0];
		ySize = dimensions[1];
		isRandom = random;
		torroidal = false;

		data = new HashMap<>();

		cellGrid = new Cell[xSize][ySize];
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				Cell tempCell = new Cell("default");
				cellGrid[x][y] = tempCell;
				tempCell.setState(Color.WHITE);
			}
		}
	}
	/**
	 * 
	 * @return data for projecting into a graph
	 */
	public Map getData() {
		updateData();
		return data;
	}
	
	// this wouldn't be necessary after refactoring, as seen in the firecontroller class
	/**
	 * updates the data in the data map
	 */
	protected void updateData() {
		Color colour;
		String type;
		data = new HashMap<>();
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				Cell toGet = cellGrid[x][y];
				colour = toGet.getColor();
				type = toGet.getState();
				if (!data.containsKey(type)) {
					data.put(type, new HashMap<>());
					data.get(type).put(colour, 0);

				}

				data.get(type).put(colour, data.get(type).get(colour) + 1);
			}
		}
	}
	
	/**
	 * initialises values in the data map
	 */
	protected void initialValues() {
		Color colour;
		String type;
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				Cell toGet = cellGrid[x][y];
				colour = toGet.getColor();
				type = toGet.getState();
				data.get(type).put(colour, data.get(type).get(colour) + 1);
			}
		}
	}
	
	/**
	 * increments value in map by 1
	 * @param type to increase
	 * @param colour to increase
	 */
	public void increaseData(String type, Color colour) {
		data.get(type).put(colour, data.get(type).get(colour) + 1);
	}
	
	/**
	 * decrements value in map by 1
	 * @param type to decrease
	 * @param colour to decrease
	 */
	public void decreaseData(String type, Color colour) {
		data.get(type).put(colour, data.get(type).get(colour) -1);
	}
	
	/**
	 * sets up specific configuration of grid with cells set to spots
	 * @param map of cell locations
	 */
	public abstract void setUpSpecific(Map<String, int[][]> map);

	/**
	 * generates cells randomly throughout the grid based on their probability
	 * @param paramMap contains probabilities of generation for each cell
	 */
	public abstract void setUpRandom(Map<String, Double> paramMap);

	/**
	 * changes size of grid dynamically
	 * @param dimensions of new grid
	 */
	public void resize(int dimensions) {
		if (dimensions < xSize)
			truncate(dimensions);
		if (dimensions > xSize)
			enlarge(dimensions);
	}

	/**
	 * reduces size of grid
	 * @param new dimensions
	 */
	public void truncate(int dimensions) {
		Cell[][] cellGrid2 = new Cell[dimensions][dimensions];
		for (int i = 0; i < dimensions; i++) {
			for (int j = 0; j < dimensions; j++) {
				cellGrid2[i][j] = cellGrid[i][j];

			}
		}
		cellGrid = cellGrid2;

		xSize = dimensions;
		ySize = dimensions;
	}
	
	/**
	 * 
	 * @return cell to populate the rest of simulation if grid size increases dynamically
	 */
	public abstract Cell getDefaultCell();
	
	/**
	 * toggles whether the simulation is torroidal or finite
	 */
	public void switchTorroidal() {
		torroidal = !torroidal;
		initializeNeighbors();
	}
	
	/**
	 * increases the size of the grid
	 * @param dimensions
	 */
	public void enlarge(int dimensions) {
		Cell[][] cellGrid2 = new Cell[dimensions][dimensions];
		for (int i = 0; i < dimensions; i++) {
			for (int j = 0; j < dimensions; j++) {
				if (i < xSize && j < ySize) {
					cellGrid2[i][j] = cellGrid[i][j];
				} else {
					cellGrid2[i][j] = getDefaultCell();
				}
			}
		}

		cellGrid = cellGrid2;

		xSize = dimensions;
		ySize = dimensions;

		initializeNeighbors();
	}

	/**
	 * Goes through the cellGrid, and sets up the neighbors of all the cells. Cells
	 * have neighbors around them stored in an array, with this configuration: 0 1 2
	 * 3 x 4 5 6 7
	 */
	public void initializeNeighbors() {
		
		// create if statements to figure out with neighborfinder
		NeighborFinder finder = new SquareNeighborFinder(cellGrid, torroidal);
		finder.initializeNeighbors();
		cellGrid = finder.getCellGrid();
	}

	
	/**
	 * goes through cells in grid and sets their next states given their surrounding cells
	 */
	public abstract void setNextStates();
	
	/**
	 * changes cells to their next state
	 */
	public void updateCells() {
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				cellGrid[x][y].updateState();
			}
		}
	}
	
	/**
	 * 
	 * @return grid of colours to project in the simulation
	 */
	public Color[][] getColors() {
		Color[][] colors = new Color[xSize][ySize];
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				colors[x][y] = cellGrid[x][y].getColor();
			}
		}
		return colors;
	}
	/**
	 * 
	 * @return map of current cell locations.
	 */
	public abstract Map<String, int[][]> makeCellMap();
	
	/**
	 * makes an XML file containing the current locations of cells
	 * @param filename to name the newly created XML file
	 */
	public abstract void writeToXML(String filename) throws WriterException;

}
