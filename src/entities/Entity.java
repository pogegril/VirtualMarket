package entities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

import store.Product;

/**
 * Entity interface. Any entity (despite being a client or employee) must 
 * have these basic processes available to use the store as a client
 * @author Daniel Salgueiro
 */
public interface Entity {
	//Getters
	String getName();
	int getCode();
	int getPoints();
	void givePoints(int points);
	ArrayList<Product> getBasket();

	//Setters	
	void setBasket(ArrayList<Product> basket);
	void emptyBasket();

	//Basket handling
	void remProduct(Product product);
	void addProduct(Product product, int amount);
	void checkOut(boolean usePoints);

	//Object display
	String toString();
	void print(String prefix);

	//Object comparison & management
	boolean equals(Client client);
	Employee hire(Positions pos);

	//Element conversion
	Element createElement(Document doc);
}
