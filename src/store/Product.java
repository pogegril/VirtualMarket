package store;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class to represent a product for sale
 * @author Daniel Salgueiro
 */
public class Product extends Item { 
	//Price of the product
	private double price;
	//Stock of the product
	private int stock;
	//Balance of costs and profits of this product
	private double balance;
	//Shelf of the product
	private Shelf shelf;

	/**
	 * Constructs a product object
	 * @param name - Product's name
	 * @param cost - Product's cost
	 * @param description - Product's optional description
	 * @param price - Products sell price
	 * @param stock - Product's stock
	 */
	public Product(String name, double cost, String description, double price, int stock) {
		super(name, cost, description);
		if (price < cost) { throw new IllegalArgumentException("The sell price can't be lower than the cost."); }
		this.price = price;
		//Stock parameter
		if (stock < 0) { throw new IllegalArgumentException("The stock can't be negative."); } 
		this.stock = stock;
		//Balance parameter
		this.balance = -1*cost*stock;
		this.shelf = null;
	}

	/**
	 * Returns the price of this product
	 * @return price
	 */
	public double getPrice() {
		return this.price;
	}

	/**
	 * Changes the price of this product
	 * @param newPrice - Designated price
	 */
	public void setPrice(double newPrice) {
		this.price = newPrice;
	}

	/**
	 * Applies a discount of the received percentage
	 * @param percentage - Discont's percentage
	 */
	public void discount(double percentage) {
		if (percentage > 100 || percentage < 0) { 
			System.out.println("The received percentage is not valid.");
		} else {
			setPrice(price - price*(percentage/100.0));
		}
	}

	/**
	 * Returns the current stock of this product
	 * @return stock
	 */
	public int getStock() {
		return this.stock;
	}

	/**
	 * Clears the stock of the product by the received ammount
	 * @param toClear - Amount to remove
	 */
	public void clearStock(int toClear) {
		this.stock = (toClear >= this.stock) ? 0 : this.stock - toClear;
	}

	/**
	 * Restocks the product by the received amount
	 * @param restock
	 */
	public void restock(int restock) {
		this.stock += restock;
		this.balance -= this.getCost()*restock;
	}

	/**
	 * Returns a product to the shelf;
	 * Reverts the product overall balance
	 * @param amount - Amount to return
	 */
	public void putBack(int amount) {
		this.stock += amount;
		this.balance -= this.price*amount;
	}

	/**
	 * Returns the balance of profits/expenses of this item
	 * @return balance
	 */
	public double getBalance() {
		return this.balance;
	}

	/**
	 * Sets the product's balance as the received value.
	 * Allows to reassign a previous balance from a reloaded node
	 * @param balance - Balance to set
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	/**
	 * Registers a sale by updating the stock and balance
	 * @param amount - Amount to buy
	 */
	public void sale(int amount) {
		if (amount > this.stock) {
			System.out.println("Invalid sale method usage: It should only be invoked by the sale method in the Shelf object to avoid oversale errors.");
			return;
		}
		this.stock -= amount;
		this.balance += this.price * amount;
		//System.out.println(amount + " " + this.getName() + "(s) added to the shopping cart.");
	}

	/**
	 * Sets the product's shelf
	 * @param shelf - Shelf to assign
	 */
	public void setShelf(Shelf shelf) {
		this.shelf = shelf;
	}

	/**
	 * Returns the product's shelf
	 * @return shelf
	 */
	public Shelf getShelf() {
		return this.shelf;
	}

	/**
	 * Returns a string with the product's info
	 * @return string
	 */
	public String toString() {
		return super.toString() + ", Preço: " + getPrice() + ", " + getStock() + ", Balance: " + getBalance() + ", " + getDesc();
	}

	/**
	 * Returns if the received product is equal to this
	 * @param product - Product to compare
	 * @return isEqual?
	 */
	public boolean equals(Product product) {return super.equals(product) && this instanceof Product;}

	/**
	 * Builds a product object from the received node
	 * @param nNode - Product node
	 * @return product
	 */
	public static Product build(Node nNode) {
		if (nNode.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		} else {
			Element eElement = (Element) nNode;
			String name = eElement.getElementsByTagName("name").item(0).getTextContent();
			double cost = Double.parseDouble(eElement.getElementsByTagName("cost").item(0).getTextContent());
			String description = eElement.getElementsByTagName("description").item(0).getTextContent();
			double price = Double.parseDouble(eElement.getElementsByTagName("price").item(0).getTextContent());
			int stock = Integer.parseInt(eElement.getElementsByTagName("stock").item(0).getTextContent());
			double balance = Double.parseDouble(eElement.getElementsByTagName("balance").item(0).getTextContent());

			Product product = new Product(name, cost, description, price, stock);
			product.setBalance(balance);
			return product;
		}
	}

	/**
	 * Returns an element that contains the relevant product data
	 * @param doc - XML document
	 * @return element
	 */
	public Element createElement(Document doc) {
		Element eProduct = doc.createElement("product");

		Element eName = doc.createElement("name");
		eName.appendChild(doc.createTextNode(getName()));

		Element eCost = doc.createElement("cost");
		eCost.appendChild(doc.createTextNode(Double.toString(getCost())));

		Element eDesc = doc.createElement("description");
		eDesc.appendChild(doc.createTextNode(getDesc()));

		Element ePrice = doc.createElement("price");
		ePrice.appendChild(doc.createTextNode(Double.toString(getPrice())));

		Element eStock = doc.createElement("stock");
		eStock.appendChild(doc.createTextNode(Integer.toString(getStock())));

		Element eBalance = doc.createElement("balance");
		eBalance.appendChild(doc.createTextNode(Double.toString(getBalance())));

		eProduct.appendChild(eName);
		eProduct.appendChild(eCost);
		eProduct.appendChild(eDesc);
		eProduct.appendChild(ePrice);
		eProduct.appendChild(eStock);
		eProduct.appendChild(eBalance);

		return eProduct;		
	}
}
