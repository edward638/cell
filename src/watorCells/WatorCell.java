package watorCells;

import cells.Cell;
import javafx.scene.paint.Color;
/**
 * Controller subclass of wator simulation
 * @author jeffreyli
 *
 */
public class WatorCell extends Cell{
	private Animal animal;
	
	public WatorCell(String state, Animal object) {
		super(state);
		animal = object;
		updateColor();
	}
	
	public String getAnimalType() {
		return animal.getType();
	}
	
	@Override
	public String getState() {
		return animal.getType();
	}
	public Animal getAnimal() {
		return animal;
	}
	
	/**
	 * sets cell to water
	 */
	public void setToWater() { 
		animal = new Water();
		colour = Color.CORNFLOWERBLUE;
	}
	
	public void updateColor() {
		if (animal.getType().equals("shark")){
			colour = Color.RED;
		}
		else if (animal.getType().equals("fish")){
			colour = Color.GREENYELLOW;
		}
		else if (animal.getType().equals("water")){
			colour = Color.CORNFLOWERBLUE;
		}
	}
	
	/**
	 * 
	 * @param newAnimal = animal object of new cell state
	 * @param newState = string of new cell state
	 */
	public void setNewAnimal(Animal newAnimal, String newState) {
		animal = newAnimal;
		currentState = newState;
		updateColor();
	}

	public void decrementAnimalHealth() {
		animal.decrementHealth();
	}
	
}
