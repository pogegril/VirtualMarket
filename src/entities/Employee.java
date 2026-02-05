package entities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import store.Product;

/**
 * Class that represents an employee
 * @author Daniel Salgueiro
 */
public class Employee extends Client {  
	//Current job position
	private Positions position;	   
	//Salary
	private double salary;

	/**
	 * Constructs an employee object
	 * @param name - Employee's name
	 * @param code - Employee's code
	 * @param position - Employee's position
	 */
	public Employee(String name, int code, Positions position) {
		super(name, code);
		this.position = position;
		this.salary = position.getSalary();
	}
	/**
	 * Returns the current job position
	 * @return position
	 */
	public Positions getPosition() {
		return this.position;
	}

	/**
	 * Sets the received position as the current job position.
	 * @param pos - Employee's designated position
	 */
	public void setPosition(Positions pos) {
		this.position = pos;
		if (this.salary < this.position.getSalary()) {
			this.salary = this.position.getSalary();
		}
	}

	/**
	 * Returns the employee's salary
	 * @return salary
	 */
	public double getSalary() {
		return this.salary;
	}

	/**
	 * Sets employee's salary the received double
	 * @param salary - Employee's designated salary
	 */
	public void setSalary(double salary) {
		this.salary = salary;
	}

	/**
	 * Fires an employee. This method creates a client from an employee object
	 * @return client
	 */
	public Client fire() {
		Client client = new Client(this.getName(), this.getCode());
		client.setBasket(this.getBasket());
		client.givePoints(this.getPoints());
		return client;
	}

	/**
	 * Raises the employee's salary by the received percentage
	 * @param percentage - Raise's percentage
	 */
	public void raise(double percentage) {
		if (percentage > 100 || percentage < -100) {
			System.out.println("The received percentage is not valid.");
		} else {
			this.salary += this.salary*percentage/100;
		}
	}

	/**
	 * Returns a string with the employee's info
	 * @return string
	 */
	public String toString() {
		return super.toString() + ", " + getPosition() + " : " + getSalary();
	}

	/**
	 * Builds an employee object from the received node
	 * @param nNode - Employee node
	 * @return employee
	 */
	public static Employee build(Node nNode) {
		if (nNode.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		} else {
			Element eElement = (Element) nNode;
			String name = eElement.getElementsByTagName("name").item(0).getTextContent();
			int code = Integer.parseInt(eElement.getElementsByTagName("code").item(0).getTextContent());
			int points = Integer.parseInt(eElement.getElementsByTagName("points").item(0).getTextContent());
			Positions position = Positions.valueOf(eElement.getElementsByTagName("position").item(0).getTextContent());
			double salary = Double.parseDouble(eElement.getElementsByTagName("salary").item(0).getTextContent());

			Employee employee = new Employee(name, code, position);
			employee.givePoints(points);
			employee.setSalary(salary);

			//Setting up employee object
			NodeList nProducts = eElement.getElementsByTagName("products").item(0).getChildNodes();
			for (int i = 0; i < nProducts.getLength(); i++) {
				Node nProduct = nProducts.item(i);
				if (nProduct.getNodeType() == Node.ELEMENT_NODE) {
					Product product = Product.build(nProduct);
					employee.addProduct(product, product.getStock()); //Note: The stock in products in clients's baskets refers to the amount in the basket.
				}
			}
			return employee;
		}
	}

	/**
	 * Returns an element that contains the relevant employee data
	 * @param doc - XML document
	 * @return Element
	 */
	public Element createElement(Document doc) {
		Element eEmployee = doc.createElement("employee");
		Element eProducts = doc.createElement("products");

		Element eName = doc.createElement("name");
		eName.appendChild(doc.createTextNode(getName()));

		Element eCode = doc.createElement("code");
		eCode.appendChild(doc.createTextNode(Integer.toString(getCode())));

		Element ePoints = doc.createElement("points");
		ePoints.appendChild(doc.createTextNode(Integer.toString(getPoints())));

		Element ePosition = doc.createElement("position");
		ePosition.appendChild(doc.createTextNode(getPosition().toString()));

		Element eSalary = doc.createElement("salary");
		eSalary.appendChild(doc.createTextNode(Double.toString(getSalary())));

		//Setting up client's basket data
		for (Product product : this.getBasket()) {
			Element eProduct = product.createElement(doc);
			eProducts.appendChild(eProduct);
		}

		eEmployee.appendChild(eName);
		eEmployee.appendChild(eCode);
		eEmployee.appendChild(ePoints);
		eEmployee.appendChild(ePosition);
		eEmployee.appendChild(eProducts);
		eEmployee.appendChild(eSalary);

		return eEmployee;		
	}
}
