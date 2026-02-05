package windows;

import store.Cashier;
import store.Shelf;
import store.Product;

import javax.swing.*;

import entities.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Product Manager Window
 * @author Daniel Salgueiro
 */
public class ProductManager extends JFrame {
	//Refresh-able panels
	private JPanel shelvesPanel;
	private JPanel productsPanel;

	/**
	 * Product Manager window object
	 */
	public ProductManager() {
		setTitle("Product Manager");
		setSize(800, 600);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((screenSize.getWidth() - this.getWidth()) / 2) + 50; 
		int y = (int) ((screenSize.getHeight() - this.getHeight()) / 2) + 50; 

		setLocation(x, y);   
		setLayout(new BorderLayout());

		//Toolbar
		JPanel toolBar = new JPanel();
		toolBar.setLayout(new GridLayout(1, 2));
		JButton addShelf = new JButton("New Shelf +");
		JButton addProduct = new JButton("New Product +");
		JButton findProduct= new JButton("Find...");

		//Shelves & Products
		shelvesPanel = new JPanel();
		shelvesPanel.setLayout(new BoxLayout(shelvesPanel, BoxLayout.Y_AXIS));
		JScrollPane shelves = new JScrollPane(shelvesPanel);

		productsPanel = new JPanel();
		productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
		JScrollPane products = new JScrollPane(productsPanel);

		JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		mainPanel.add(shelves);
		mainPanel.add(products);
		add(mainPanel, BorderLayout.CENTER);

		addShelf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField nameField = new JTextField();
				JTextField costField = new JTextField();
				JTextField descField = new JTextField();
				JTextField sizeField = new JTextField();
				Object[] fields = {
					"Name:", nameField,
					"Description", descField,
					"Cost:", costField,
					"Size:", sizeField
				};

				int option = JOptionPane.showConfirmDialog(null, fields, "New Shelf +", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					String name = nameField.getText();
					String desc = descField.getText();
					double cost = Double.parseDouble(costField.getText());
					int size = Integer.parseInt(sizeField.getText());

					Shelf shelf = new Shelf(name, cost, desc, size);
					Cashier.addShelf(shelf);
					Cashier.saveData();
					refresh();
					MainWindow.refresh();
				}
			}
		});      
		addProduct.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField nameField = new JTextField();
				JTextField descField = new JTextField();
				JTextField costField = new JTextField();
				JTextField priceField = new JTextField();
				JTextField stockField = new JTextField();
				Object[] fields = {
					"Name:", nameField,
					"Description", descField,
					"Cost:", costField,
					"Price:", priceField,
					"Stock:", stockField
				};

				int option = JOptionPane.showConfirmDialog(null, fields, "New Product +", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					String name = nameField.getText();
					String desc = descField.getText();
					double cost = Double.parseDouble(costField.getText());
					double price = Double.parseDouble(priceField.getText());
					int stock = Integer.parseInt(stockField.getText());

					Product product = new Product(name, cost, desc, price, stock);
					Cashier.addProduct(product);
					Cashier.saveData();
					refresh();
				}
			}
		});
		findProduct.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String searchName = JOptionPane.showInputDialog(ProductManager.this, "Enter a product to search:", "Find...", JOptionPane.PLAIN_MESSAGE);
				if (searchName!= null) {
					for (Product product : Cashier.getProducts()) {
						if (product.getName().equals(searchName)) {
							new ProductDisplay(product);
							return;
						}
					}
					for (Shelf shelf : Cashier.getShelves()) {
						if (shelf.getName().equals(searchName)) {
							new ShelfDisplay(shelf, ProductManager.this);
							return;
						}
					}
					JOptionPane.showMessageDialog(null, searchName + " was not found", searchName +" not found", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		//Footer
		JLabel footer = new JLabel("Virtual Market", JLabel.CENTER);
		add(footer, BorderLayout.SOUTH);

		toolBar.add(addShelf);
		toolBar.add(addProduct);
		toolBar.add(findProduct);
		add(toolBar, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);  
		refresh();
		setVisible(true);
	}

	/**
	 * Refreshes the window
	 */
	public void refresh() {
		shelvesPanel.removeAll();
		productsPanel.removeAll();

		//Shelves
		JLabel shelvesHeader = new JLabel("Shelves");
		shelvesHeader.setOpaque(true);
		shelvesHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		shelvesHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		shelvesHeader.setHorizontalAlignment(JLabel.CENTER);
		shelvesPanel.add(shelvesHeader);

		for (Shelf shelf : Cashier.getShelves()) {
			JButton shelfButton = new JButton(shelf.getName());
			shelfButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			shelfButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			shelfButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new ShelfDisplay(shelf, ProductManager.this);
				}
			});
			shelvesPanel.add(shelfButton);
		}

		//Products
		JLabel productsHeader = new JLabel("Products");
		productsHeader.setOpaque(true);
		productsHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		productsHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		productsHeader.setHorizontalAlignment(JLabel.CENTER);
		productsPanel.add(productsHeader);

		for (Product product : Cashier.getProducts()) {
			JButton productButton = new JButton(product.getName());
			productButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			productButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			productButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new ProductDisplay(product);
				}
			});
			productsPanel.add(productButton);
		}
		shelvesPanel.revalidate();
		shelvesPanel.repaint();

		productsPanel.revalidate();
		productsPanel.repaint();
	}
}
