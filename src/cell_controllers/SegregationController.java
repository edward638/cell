package cell_controllers;

import java.util.ArrayList;
import java.util.Map;

import cellsociety_team08.Cell;
import javafx.scene.paint.Color;

public class SegregationController extends CellController {
	private double threshold;


	public SegregationController(int[] dimensions, Map<String, int[][]> map, Map<String, Double> paramMap) {

		super(dimensions);
		int[][] cellsX =  map.get("x");
		int[][] cellsO =  map.get("o");
		threshold = paramMap.get("threshold");
		for (int x = 0; x < cellsX.length; x++) {
			int xCoord = cellsX[x][0];
			int yCoord = cellsX[x][1];
			cellGrid[xCoord][yCoord] = new Cell("X");
			cellGrid[xCoord][yCoord].setState(Color.RED);
		}
		for (int o = 0; o < cellsO.length; o++) {
			int xCoord = cellsO[o][0];
			int yCoord = cellsO[o][1];
			cellGrid[xCoord][yCoord] = new Cell("O");
			cellGrid[xCoord][yCoord].setState(Color.BLUE);
		}
		initializeNeighbors();
	}
	

	@Override
	public void setNextStates() {
		ArrayList<String> movers = new ArrayList<>();
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				
				Cell toSet = retrieveCell(x, y);
				String toSetType = toSet.getState();
				
				// deal with x and o
				if (!toSetType.equals("default")) {
					double same = 0;
					double diff = 0;
					String[] neighbours = toSet.getNeighborStateNames();
					for (String type:neighbours) {
						if (!type.equals("default")) {
							if (type.equals(toSetType)) {
								same++;
							}
							else if (!type.equals(toSetType) && !type.equals("default")){
								diff++;
							}
						}
					}
					if (diff > 0) {
					double myRatio = same/diff;
					if (myRatio < threshold) {
						toSet.setNextStateDefault();
						movers.add(toSetType);
					}
					else if (myRatio >= threshold) {
						toSet.setNextState(toSetType);
					}
					}
					else {
						toSet.setNextState(toSetType);
					}
				}
				
				// deal with default
				else if (toSetType.equals("default")) {
					if (movers.size()!=0) {
						String race = movers.remove(0);
						toSet.setNextState(race);
						if (race.equals("X")) {
							toSet.setState(Color.RED);
						}
						else {
							toSet.setState(Color.BLUE);
						}
					}
				}

			}
		}
	}
}