package entities;

import store.Product;
import store.Shelf;
import store.Cashier;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class to represent a client
 * @author Daniel Salgueiro
 */
public class Client implements Entity{
	//Name of the client
	private String name;
	//Code of the client
	private int code;
	//client's store promotional points
	private int points;
	//Entity's shopping cart
	private ArrayList<Product> basket = new ArrayList<Product>();
	//Shopping cart's capacity
	private static final int BASKET_FULL = 100;
	//Promotional points discount ratio
	public static final int POINT_RATE = 100;

	/**
	 * Constructs a client object
	 * @param name - Client's name
	 * @param code - Client's code
	 */
	public Client(String name, int code) {
		//Name parameter
		if (name == null || name.length() == 0 ) { throw new IllegalArgumentException("The name needs at least one character"); }
		this.name = name.trim();
		//Code parameter
		if (code < 0 && code != -1) {throw new IllegalArgumentException("The code can't be negative."); } //-1 as exception
		this.code = code;
		//Basket parameter
		this.basket = new ArrayList<Product>();
		//Promotional points parameter
		this.points = 0;
	}

	/**
	 * Returns the client's name
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the client's code
	 * @return code
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Returns client's promotional points
	 * @return points
	 */
	public int getPoints() {
		return this.points;
	}

	/**
	 * Gives the received promotional points to this client
	 * @param points - Points to give to client
	 */
	public void givePoints(int points) {
		this.points += points;
	}

	/**
	 * Returns the shopping cart
	 * @return basket
	 */
	public ArrayList<Product> getBasket() {
		return this.basket;
	}

	/**
	 * Sets the received basket as the client's current basket.
	 * Allows to not lose the basket between hiring/firing
	 * @param basket - Basket to set
	 */
	public void setBasket(ArrayList<Product> basket) {
		this.basket = basket;
	}

	/**
	 * Replaces the current basket with an empty one. Only to be used if the articles aren't returning to the shop's stock.
	 */
	public void emptyBasket() {
		this.basket = new ArrayList<Product>();
	}

	/**
	 * Removes a product out of the shopping cart, if possible to return
	 * @param product - Product to remove
	 */
	public void remProduct(Product product) {
		for (Product prod : this.basket) {
			if (product.equals(prod)) {
				for (Shelf shelf : Cashier.getShelves()) {
					for (Product pd : shelf.getShelf() ) {
						if (product.equals(pd)) {
							this.basket.remove(prod);
							shelf.putBack(prod);
							return;
						}
					}
				}
				System.out.println("No shelf found. Throwing product away.");
			}
		}
		System.out.println(product.getName() + " is not in the basket.");
	}

	/**
	 * Adds a product to the shopping cart
	 * @param product - Product to add
	 * @param amount - Ammount to add
	 */
	public void addProduct(Product product, int amount) {
		if (this.basket.size() >= BASKET_FULL) {
			System.out.println("Basket is full.");
			return;
		} else {
			for (Product prod : this.basket) {
				if (prod.equals(product)) {
					prod.restock(amount);
					product.sale(amount);
					return;
				}
			}
			Product onBasket = new Product(product.getName(), product.getCost(), product.getDesc(), product.getPrice(), amount);
			product.sale(amount);
			this.basket.add(onBasket);
		}
	}

	/**
	 * Adds the product to the shopping cart without affecting shelves.
	 * Used to rebuild client elements from XMLl database
	 * @param product - Product to add
	 */
	public void setProduct(Product product) {
		this.basket.add(product);
	}

	/**
	 * Client checkout, shows the final receipt, processes promotional points and empties the basket
	 * Returns the total discounts from the initial receipt
	 * @param usePoints - If the points are to be used on checkout
	 */
	public void checkOut(boolean usePoints) {
		double receipt = 0;
		double discounts = 0;
		for (Product product : this.basket) {
			receipt += product.getPrice()*product.getStock();
		}
		emptyBasket();
		if (usePoints) {
			if (this.points/POINT_RATE > receipt) {
				this.points -= receipt*POINT_RATE;
				discounts += receipt;
				System.out.println("The receipt was paid in points. Current points: " + this.points + ".");
				Cashier.discount(discounts);
			} else {
				receipt -= this.points/POINT_RATE;
				discounts += this.points/POINT_RATE;
				this.points = 0;
			}
		}
		if (this instanceof Employee) {
			Employee employee = (Employee) this;
			receipt = receipt - (receipt * employee.getPosition().discount()/100);
			discounts += receipt * employee.getPosition().discount()/100;
		}
		//Giving promotional points :p
		this.givePoints((int)Math.floor(receipt));
		System.out.println("PRICE: " + receipt + "€");
		System.out.println("Current Points: " + this.points + ".");
		Cashier.discount(discounts);
	}

	/**
	 * Returns a string with the client's info
	 * @return String
	 */
	public String toString() {
		return getName() + ", " + getCode() + ", Points: " + this.points;
	}

	/**
	 * Prints the client's info with the received prefix.
	 * If the basket isn't empty it will print the products.
	 * NOTE: In this case,the storage is the amount in the basket
	 * @param prefix - Prefix to add
	 */
	public void print(String prefix) {
		if (this.basket.size() > 0) {
			System.out.println(prefix + toString() + ". Current basket:");
			this.basket.forEach( (n) -> n.print(" -"));
		} else {
			System.out.println(prefix + toString());
		}
	}
	/**
	 * Returns if the received client is equal to this
	 * @param client - Client to compare
	 * @return isEqual?
	 */
	public boolean equals(Client client) {
		return this.code == client.getCode();
	}

	/**
	 * Hires a client. This method creates an employee from a client object
	 * @param pos - Position of the Employee
	 * @return employee
	 */
	public Employee hire(Positions pos) {
		if (Client.this instanceof Employee) {
			System.out.println("Can't hire a worker.");
			return null;
		} else {
			Employee employee = new Employee(getName(), getCode(), pos);
			employee.setBasket(getBasket());
			employee.givePoints(this.getPoints());
			return employee;
		}
	}

	/**
	 * Builds a client object from the received node
	 * @param nNode - Client node
	 * @return client
	 */
	public static Client build(Node nNode) {
		if (nNode.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		} else {
			Element eElement = (Element) nNode;
			String name = eElement.getElementsByTagName("name").item(0).getTextContent();
			int code = Integer.parseInt(eElement.getElementsByTagName("code").item(0).getTextContent());
			int points = Integer.parseInt(eElement.getElementsByTagName("points").item(0).getTextContent());

			Client client = new Client(name, code);
			client.givePoints(points);

			NodeList nProducts = eElement.getElementsByTagName("products").item(0).getChildNodes();
			for (int i = 0; i < nProducts.getLength(); i++) {
				Node nProduct = nProducts.item(i);
				if (nProduct.getNodeType() == Node.ELEMENT_NODE) {
					Product product = Product.build(nProduct);
					client.setProduct(product);
				}
			}
			return client;
		}
	}

	/**
	 * Returns an element that contains the relevant client data
	 * @param doc - XML document
	 * @return Element
	 */
	public Element createElement(Document doc) {
		Element eClient = doc.createElement("client");
		Element eProducts = doc.createElement("products");

		Element eName = doc.createElement("name");
		eName.appendChild(doc.createTextNode(getName()));

		Element eCode = doc.createElement("code");
		eCode.appendChild(doc.createTextNode(Integer.toString(getCode())));

		Element ePoints = doc.createElement("points");
		ePoints.appendChild(doc.createTextNode(Integer.toString(getPoints())));

		//Setting up client's basket data
		for (Product product : this.basket) {
			Element eProduct = product.createElement(doc);
			eProducts.appendChild(eProduct);
		}

		eClient.appendChild(eName);
		eClient.appendChild(eCode);
		eClient.appendChild(ePoints);
		eClient.appendChild(eProducts);

		return eClient;		
	}
}
