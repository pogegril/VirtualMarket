package store;

//Java imports
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import windows.MainWindow;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//Local imports
import entities.Client;
import entities.Employee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Program's main class
 * @author Daniel Salgueiro
 */
public class Cashier {
	//Saves existent user codes
	private static ArrayList<Integer> usedCodes = new ArrayList<>();
	//Store's clients
	private static ArrayList<Client> entities = new ArrayList<>();
	//Store's products
	private static ArrayList<Product> products = new ArrayList<>();
	//Store's shelves
	private static ArrayList<Shelf> shelves = new ArrayList<>();
	//Stores the value in discounts given to users
	//Intended usage:   discountBalance += 
	public static double discountBalance = 0;

	/**
	 * Runs the VirtualMarket program
	 * @param args - Java arguments
	 */
	public static void main(String[] args) {   	
		try {
			File inputFile = new File("XML/Database.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "/store/*";
			NodeList nStore = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

			Node nShelves = nStore.item(0);
			Node nClients = nStore.item(1);
			Node nEmployees = nStore.item(2);

			NodeList nClientList = nClients.getChildNodes();
			for (int i = 0; i < nClientList.getLength(); i++) {
				Node nClient = nClientList.item(i);
				if (nClient.getNodeType() == Node.ELEMENT_NODE) {
					Element eClient = (Element) nClient;
					Client client = Client.build(nClient);
					entities.add(client);
					int code = Integer.parseInt(eClient.getElementsByTagName("code").item(0).getTextContent());
					if (!usedCodes.contains(code)) {
						usedCodes.add(code);
					}
				}
			}

			NodeList nEmployeeList = nEmployees.getChildNodes();
			for (int i = 0; i < nEmployeeList.getLength(); i++) {
				Node nEmployee = nEmployeeList.item(i);
				if (nEmployee.getNodeType() == Node.ELEMENT_NODE) {
					Element eEmployee= (Element) nEmployee;
					Employee employee = Employee.build(nEmployee);
					entities.add(employee);
					int code = Integer.parseInt(eEmployee.getElementsByTagName("code").item(0).getTextContent());
					if (!usedCodes.contains(code)) {
						usedCodes.add(code);
					}
				}
			}

			NodeList nShelvesList = nShelves.getChildNodes();
			for (int i = 0; i < nShelvesList.getLength(); i++) {
				Node nShelf = nShelvesList.item(i);
				if (nShelf == null || nShelf.getNodeType() == Node.ELEMENT_NODE) {
					Shelf shelf = Shelf.build(nShelf);
					shelves.add(shelf);
					for (Product product : shelf.getShelf()) {
						products.add(product);
					}
				}
			}
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					MainWindow mainWindow = new MainWindow();
				}
			});
		} catch (Exception e) {e.printStackTrace();}
	}

	/**
	 * Method that ensures all entities have a unique identification code
	 * @return code
	 */
	public static int generateCode() {
		int upperLimit = 10000000;

		// Check each number in the range until finding an unused code
		for (int i = 1; i < upperLimit; i++) {
			if (!usedCodes.contains(i)) {
				usedCodes.add(i);
				return i;
			}
		}
		throw new RuntimeException("No available codes in the given limit.");
	}
	/**
	 * Saves the output stream in the doc
	 * @param doc - XML document 
	 * @param output - OutputStream 
	 */
	private static void writeXml(Document doc, OutputStream output) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(output);

		transformer.transform(source, result);
	}

	/**
	 * Handles the discounts balance
	 * @param discounts - Discounts to add
	 */
	public static void discount(double discounts) {
		discountBalance += discounts;
	}

	/**
	 * Returns the final balance
	 * @return balance
	 */
	public static double getBalance() {
		double balance = -1*discountBalance;
		for (Shelf shelf : shelves) {
			balance += shelf.getBalance();
		}

		//If the employee's salary is to be considered in the balance, uncomment this
		//for (Client client : entities) {
		//	if (client instanceof Employee) {
		//		Employee employee = (Employee) client;
		//		balance -= employee.getSalary();
		//	}
		//}
		return balance;
	}
	/**
	 * Adds a product to the store
	 * @param product
	 */
	public static void addProduct(Product product) {
		for (Product prod : products) {
			if (product.equals(prod)) {
				System.out.println(product.getName() + " is already registered.");
				return;
			}
		}
		products.add(product);
	}
	/**
	 * Returns the store's products
	 * @return products
	 */
	public static ArrayList<Product> getProducts() {
		return products;
	}
	/**
	 * Adds a shelf to the store
	 * @param shelf
	 */
	public static void addShelf(Shelf shelf) {
		shelves.add(shelf);
	}
	/**
	 * Returns store's shelves
	 * @return shelves
	 */
	public static ArrayList<Shelf> getShelves() {
		return shelves;
	}
	/**
	 * Removes shelf from store's database
	 * @param shelf
	 */
	public static void remShelf(Shelf shelf) {
		shelves.remove(shelf);
	}
	/**
	 * Adds an entity to the store if not already present
	 * @param client
	 */
	public static void addEntity(Client client) {
		for (Client cl : entities) {
			if (cl.equals(client)) {
				System.out.println(client.getName() + " is already registered.");
				return;
			}
		}
		entities.add(client);
	}
	/**
	 * Returns store's registered entities
	 * @return entities
	 */
	public static ArrayList<Client> getEntities() {
		return entities;
	}
	/**
	 * Removes an entity from the store's database
	 * @param client
	 */
	public static void remEntity(Client client) {
		entities.remove(client);
	}
	/**
	 * Saves the updated database in the xml file
	 */
	public static void saveData() {
		try { 	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();


			Element rootElement = doc.createElement("store");
			doc.appendChild(rootElement);

			Element eShelves = doc.createElement("shelves");
			Element eClients = doc.createElement("clients");
			Element eEmployees = doc.createElement("employees");

			rootElement.appendChild(eShelves);
			rootElement.appendChild(eClients);
			rootElement.appendChild(eEmployees);

			for (Shelf shelf : shelves) {
				eShelves.appendChild(shelf.createElement(doc));
			}

			for (Client client : entities) {
				if (client instanceof Employee) {
					Employee employee = (Employee) client;
					eEmployees.appendChild(employee.createElement(doc));
				} else {
					eClients.appendChild(client.createElement(doc));
				}
			}

			FileOutputStream output = new FileOutputStream("XML/Database.xml");
			writeXml(doc, output);
			System.out.println("Saved Successfully");
		} catch (Exception e) {e.printStackTrace();}
	}

}
