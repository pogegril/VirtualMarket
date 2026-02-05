package store;

/**
 * Object that represents a store's item
 * @author Daniel Salgueiro
 */
public abstract class Item {
	//Name of the item
	private String name;
	//Cost of the item
	private double cost;
	//Succinct description of the item
	private String desc;

	/**
	 * Constructs an item object
	 * @param name - Item's name
	 * @param cost - Item's cost
	 * @param description - Item's optional description
	 */
	public Item(String name, double cost, String description) {
		//Name parameter
		if (name == null || name.length() == 0 ) { throw new IllegalArgumentException("The name needs at least one character"); }
		this.name = name.trim();
		//Price parameter
		if (cost < 0) {throw new IllegalArgumentException("The cost can't be negative."); }
		this.cost = cost;
		//Description parameter
		this.desc = (description == null || description.length() == 0) ? "No description" : description;
	}

	/**
	 * Returns name of the item
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns cost of the item
	 * @return cost
	 */
	public double getCost() {
		return this.cost;
	}

	/**
	 * Changes the cost of the item
	 * @param newCost - Item's new price
	 */
	public void setCost (double newCost) {
		this.cost = (cost < 0) ? this.cost : newCost;
	}

	/**
	 * Returns description of the item
	 * @return description
	 */
	public String getDesc() {
		return this.desc;
	}
	
	/**
	 * Changes the description of the item.
	 * It can be used to remove the description by using an empty string as input.
	 * @param description - new Description
	 */
	public void setDesc(String description) {
		this.desc = (description == null || description.length() == 0) ? "No description" : description;
	}

	/**
	 * Uses the changeDesc() method to remove the description.
	 */
	public void remDesc() {
		setDesc("");
	}

	/**
	 * Returns a string with the item's info
	 * @return string
	 */
	public String toString() {
		return getName() + ", Cost: " + getCost();
	}

	/**
	 * Prints the item's info with the received prefix
	 * @param prefix - Prefix to add
	 */
	public void print(String prefix) {
		System.out.println(prefix + this.toString());
	}

	/**
	 * Returns if the received item is equal to this
	 * @param item - Item to compare
	 * @return isEqual?
	 */
	public boolean equals(Item item) {
		return this.getName().equals(item.getName());
	}

	/**
	 * Applies a discount to a product or to a shelf's products
	 * @param percentage - Discount's percentage
	 */
	public abstract void discount(double percentage);
}
