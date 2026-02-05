package store;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class to represent a shelf that contains products
 * @author Daniel Salgueiro
 *
 */
public class Shelf extends Item {
	//Size of the shelf
	private int size;
	//Current storage of the shelf
	private int storage;
	//Shelf to store products
	private ArrayList<Product> shelf;

	/**
	 * Constructs a shelf object
	 * @param name - Shelf's name
	 * @param cost - Shelf's cost
	 * @param description - Shelf's optional description
	 * @param size - Shelf's max product limit
	 */
	public Shelf(String name, double cost, String description, int size) {
		super(name, cost, description);
		//Size parameter
		if (size < 0) { throw new IllegalArgumentException("The stock can't be negative."); } 
		this.size = size;
		//Shelf parameter
		this.shelf = new ArrayList<Product>();
		//Storage of the shelf
		this.storage = 0;
	}

	/**
	 * Returns the max size of the shelf
	 * @return size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Returns current storage of the shelf
	 * @return storage
	 */
	public int getStorage() {
		return this.storage;
	}

	/**
	 * Returns the balance of the profit and expenses balance of all products in this shelf
	 * @return balance
	 */
	public double getBalance() {
		double balance = -1*this.getCost();
		for (Product product : this.shelf) {
			balance += product.getBalance();
		}
		return balance;
	}

	/**
	 * Returns the shelf's product list
	 * @return shelf
	 */
	public ArrayList<Product> getShelf() {
		return this.shelf;
	}

	/**
	 * Adds a product to the shelf. 
	 * @param product - Product to shelf
	 */
	public void addProduct(Product product) {
		if (this.storage >= this.size) {
			System.out.println(this.getName() + " is full.");
		} else {
			for (Product prod : this.shelf) {
				if (product.equals(prod)) {
					System.out.println(product.getName() + " is already in the shelf. Restock the existant product.");
					return;
				}
			}
			product.setShelf(this);
			this.shelf.add(product);
			this.storage = this.shelf.size();
		}
	}

	/**
	 * Removes a product from the shelf
	 * @param product - Product to remove
	 */
	public void remProduct(Product product) {
		product.setShelf(null);
		this.shelf.remove(product);
		this.storage = this.shelf.size();
	}

	/**
	 * Returns a product to the shelf
	 * @param product - Product to return
	 * @return ifFound?
	 */
	public boolean putBack(Product product) {
		for (Product prod : this.shelf) {
			if (prod.equals(product)) {
				prod.putBack(product.getStock());
				System.out.println(product.getName() + " was returned to it's shelf.");
				return true;
			}
		}
		System.out.println(product.getName() + " was not found in this shelf.");
		return false;
	}

	/**
	 * Restocks the product if found
	 * @param product - Product to restock
	 * @param restock - Amount to restock
	 */
	public void restock(Product product, int restock) {
		for (Product prod : this.shelf) {
			if (prod.equals(product)) {
				prod.restock(restock);
				System.out.println(product.getName() + " was restocked.");
				return;
			}
		}
		System.out.println(product.getName() + " was not found.");
	}

	/**
	 * Registers a sale of a product in this shelf
	 * @param product - Product sold
	 * @param amount - Amount sold
	 * @return sold?
	 */
	public boolean sale(Product product, int amount) {
		for (Product prod : this.shelf) {
			if (prod.equals(product)) {
				if (prod.getStock() > amount) {
					prod.sale(amount);
					return true;
				} else {
					System.out.println("There's not enough stock of " + prod.getName() + ".");
					return false;
				}
			}
		}
		System.out.println(product.getName() + " was not found.");
		return false;
	}

	/**
	 * Applies a discount to all of the shelf's products
	 * @param percentage - Discount's percentage
	 */
	public void discount(double percentage) {
		this.shelf.forEach( (n) -> n.discount(percentage));
	}

	/**
	 * Returns a string with this shelf's info
	 * @return string
	 */
	public String toString() {
		return super.toString() + ", " + Integer.toString(storage) + "/" + Integer.toString(size) + " products." + ", " + getDesc();
	}

	/**
	 * Prints the shelf's info. 
	 * Can print the short version or all present products.
	 * @param bool - If it should list all products in the shelf
	 */
	public void print(boolean bool) {
		super.print("");
		if (bool) {
			this.shelf.forEach( (n) -> n.print(" -"));
		}
	}

	/**
	 * Builds a shelf object from the received node
	 * @param nNode - Shelf node
	 * @return shelf
	 */
	public static Shelf build(Node nNode) {
		if (nNode == null || nNode.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		} else {
			Element eElement = (Element) nNode;
			String name = eElement.getElementsByTagName("name").item(0).getTextContent();
			double cost = Double.parseDouble(eElement.getElementsByTagName("cost").item(0).getTextContent());
			String description = eElement.getElementsByTagName("description").item(0).getTextContent();
			int size = Integer.parseInt(eElement.getElementsByTagName("size").item(0).getTextContent());

			Shelf shelf = new Shelf(name, cost, description, size);

			//Setting up shelf
			NodeList nProducts = eElement.getElementsByTagName("products").item(0).getChildNodes();
			for (int i = 0; i < nProducts.getLength(); i++) {
				Node nProduct = nProducts.item(i);
				if (nProduct.getNodeType() == Node.ELEMENT_NODE) {
					Product product = Product.build(nProduct);
					shelf.addProduct(product);
				}
			}
			return shelf;
		}
	}

	/**
	 * Returns an element that contains the relevant shelf data
	 * @param doc - XML document
	 * @return Element
	 */
	public Element createElement(Document doc) {
		Element eShelf = doc.createElement("shelf");
		Element eProducts = doc.createElement("products");

		Element eName = doc.createElement("name");
		eName.appendChild(doc.createTextNode(getName()));

		Element eCost = doc.createElement("cost");
		eCost.appendChild(doc.createTextNode(Double.toString(getCost())));

		Element eDesc = doc.createElement("description");
		eDesc.appendChild(doc.createTextNode(getDesc()));

		Element eSize = doc.createElement("size");
		eSize.appendChild(doc.createTextNode(Integer.toString(getSize())));

		for (Product product : this.shelf) {
			Element eProduct = product.createElement(doc);
			eProducts.appendChild(eProduct);
		}

		eShelf.appendChild(eName);
		eShelf.appendChild(eCost);
		eShelf.appendChild(eDesc);
		eShelf.appendChild(eSize);
		eShelf.appendChild(eProducts);

		return eShelf;		
	}
}
