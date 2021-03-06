package watorCells;

public class Shark extends Animal {
	private int health;
	private static final int SHARKHEALTH = 10;
	private static final int HEALTHGAIN = 5;

	public Shark() {
		health = SHARKHEALTH;
	}
	
	public boolean isDead() {
		return health == 0;
	} 	
	@Override
	public String getType() {
		return "shark";
	}

	@Override
	public void decrementHealth() {
		health--;
	}

	@Override
	public void replenishHealth() {
		health += HEALTHGAIN;
	};
}
