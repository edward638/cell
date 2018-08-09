package front_end;

import java.io.File;
import java.util.*;

import cell_controllers.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.util.Duration;
import xml.ParserXML;
import xml.WriterException;

/**
 * Manages the simulation steps
 * @author julialong
 */
public class Simulator {


    private static final String CONWAYS = "life";
    private static final String SPREADINGFIRE = "fire";
    private static final String SEGREGATION = "segregation";
    private static final String WATOR = "wator";
    private static final String RPS = "rps";

    private static final int FRAMES_PER_SECOND = 30;
    private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    private static final int START_STEP = 5;

    private int[] dimensions;
    private Map<String, int[][]> cellTypes;
    private String simulationType;
    private Map<String, Double> parameters;
    private Boolean isRandom;

    private CellController control;

    private Boolean simulationState = false;
    private int stepTime;
    private int stepNum = 0;

    private Grid myGrid;
    private Graph myGraph;

    /**
     * Sets up the beginning configuration for the simulation
     *
     * @param stage the JavaFX stage in GUI
     * @param root  the JavaFX Group root in GUI
     */
    public void startSimulation(Stage stage, Group root) {
        stepTime = START_STEP;
        setupCellController();
        myGraph = new Graph();
        myGraph.updateGraph(root);
        setupGrid(root);
        stage.show();
        startAnimation(root);
        stepNum = 0;
    }

    /**
     * @return current dimensions of the grid
     */
    public int getDimensions() {
    	return dimensions[0];
    }

    /**
     *
     * @param newDimensions is the new dimension of the gris
     * @param root is the JavaFX Group
     */
    public void resize(int newDimensions, Group root) {
    	control.resize(newDimensions);
    	dimensions[0] = newDimensions;
    	dimensions[1] = newDimensions;
    	setupGrid(root);
    }
    /**
     * Steps through the simulation
     *
     * @param root the JavaFX Group root in GUI
     */
    private void step(Group root) {
        if (!simulationState) {
            return;
        }
        updateCells(root);
    }

    /**
     * Updates cells to the next state and fills in the grid
     * @param root the JavaFX Group root in GUI
     */
    private void updateCells(Group root) {
        stepNum++;
        control.setNextStates();
        control.updateCells();
//        updateGraph(root);
        myGrid.updateGridColors(root, control);
    }

    /**
     * Sets up the simulation
     * @param root the JavaFX Group root in GUI
     */
    private void startAnimation(Group root) {
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(root));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    /**
     * Sets up the cell controller based on the simulation type from the XML file
     */
    private void setupCellController() {
        if (simulationType.equals(CONWAYS)) {
            control = new LifeCellController(dimensions, cellTypes, parameters, isRandom);
        }
        else if (simulationType.equals(SPREADINGFIRE)) {
            control = new FireController(dimensions, cellTypes, parameters, isRandom);
        }
        else if (simulationType.equals(SEGREGATION)) {
            control = new SegregationController(dimensions, cellTypes, parameters, isRandom);
        }
        else if (simulationType.equals(WATOR)) {
            control = new WatorController(dimensions, cellTypes, parameters, isRandom);
        }
        else if (simulationType.equals(RPS)) {
            control = new RPSController(dimensions, cellTypes, parameters, isRandom);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Sets up the grid based on the number of cells so that the size of the grid itself will
     * be constant
     *
     * @param root the JavaFX Group root in GUI
     */
    private void setupGrid(Group root) {
        int gridWidth = GUI.XSIZE / dimensions[0];
        int gridHeight = GUI.YSIZE / dimensions[1];
        Double edges = chooseShape();
        myGrid = new Grid(dimensions, gridWidth, gridHeight, edges);
        myGrid.updateGridColors(root, control);
    }


    /**
     * Updates the graph with the correct colors
     * @param root the JavaFX Group root in GUI
     */
    private void updateGraph(Group root) {
        Map<String, Map<Color, Integer>> data = control.getData();
        for (String thisType : data.keySet()) {
            for (Color thisColor : data.get(thisType).keySet()) {
                myGraph.addPoint(thisType, stepNum, data.get(thisType).get(thisColor), thisColor);
            }
        }
        myGraph.updateGraph(root);
    }

    /**
     * Steps the simulation manually
     * @param root the JavaFX Group root in GUI
     */
    public void manualStep(Group root) {
        this.updateCells(root);
    }

    /**
     * Calls the XML parser to read the file from the GUI to get information for the simulaton
     * including simulation type, dimensions of grid, and types of cells
     * @param file is the configuration file to be read
     */
    public void setFile(File file) throws IllegalArgumentException {
        ParserXML parser;
        try {
            parser = new ParserXML(file);
        }
        catch(Exception e) {
            throw new IllegalArgumentException();
        }
        simulationType = parser.getSimulationType();
        dimensions = parser.getDimensions();
        cellTypes = parser.getAllCells();
        parameters = parser.getParameters();
        isRandom = parser.isRandom();
    }

    /**
     * Turns on the simulation so that it changes with each step
     */
    public void turnOn() {
        this.simulationState = true;
    }

    /**
     * Turns off the simulation so that it does not change with each step
     */
    public void turnOff() {
        this.simulationState = false;
    }

    /**
     * Decreases the delay time for the simulation so it moves more quickly
     */
    public void speedUp() {
        if (this.stepTime > 0) {
            this.stepTime--;
        }
    }

    /**
     * Increases the delay time for the simulation so it moves more slowly
     */
    public void speedDown() {
        this.stepTime++;
    }

    /**
     * Toggles the cell controller's toroidal state
     */
    public void switchToroidal() {
        control.switchTorroidal();
    }

    /**
     * Creates a new file with the current configuration
     * @param filename the filename given by the user
     */
    public void toXML(String filename) throws WriterException {
        try {
            control.writeToXML(filename);
        }
        catch (Exception e){
            throw new WriterException();
        }
    }

    private Double chooseShape() {
        return parameters.get("edgenumber");
    }

}