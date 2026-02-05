package windows;

import store.Cashier;
import store.Shelf;
import store.Product;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Product Display Window
 * @author Daniel Salgueiro
 */
public class ProductDisplay extends JFrame{
	private Product product;
	//Refresh-able panels
	protected JPanel productControls;
	protected JButton moveTo;
	private JLabel productDesc;
	private JLabel footer;

	/**
	 * Product Display Window
	 * @param product - Product to display
	 */
	public ProductDisplay(Product product) {
		this.product = product;
		setTitle(product.getName());
		setSize(300, 400);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((screenSize.getWidth() - this.getWidth()) / 2) + 70; 
		int y = (int) ((screenSize.getHeight() - this.getHeight()) / 2) + 60; 
		setLocation(x, y);   
		setLayout(new BorderLayout());

		//Header & Image handling
		JPanel header = new JPanel(new BorderLayout());
		ImageIcon icon = new ImageIcon(new ImageIcon("img/product.png").getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH));
		JLabel JIcon = new JLabel(icon);
		JIcon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));

		JLabel productTitle = new JLabel(product.getName() + "    Stock: " + product.getStock());
		productTitle.setHorizontalAlignment(SwingConstants.LEFT);
		JLabel productInfo = new JLabel("Cost: " + product.getCost() + "€  Price: " + EntityDisplay.round(product.getPrice(),  2) + "€");
		productInfo.setHorizontalAlignment(SwingConstants.LEFT);
		productDesc = new JLabel(product.getDesc());
		productDesc.setHorizontalAlignment(SwingConstants.LEFT);
		JPanel productPanel = new JPanel(new GridLayout(0, 1));
		productPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		productPanel.add(productTitle);
		productPanel.add(productInfo);
		productPanel.add(productDesc);
		header.add(productPanel, BorderLayout.CENTER);
		header.add(JIcon, BorderLayout.WEST);

		//Product commands
		JButton changeDesc = new JButton("Change Description");
		JButton setPrice = new JButton("Change Price");
		JButton restock = new JButton("Restock");
		JButton clearStock = new JButton("Clear Stock"); 
		moveTo = new JButton("Move to...");
		productControls = new JPanel(new GridLayout(0,1));
		productControls.add(changeDesc);
		productControls.add(setPrice);
		productControls.add(restock);
		productControls.add(clearStock);
		productControls.add(moveTo);

		changeDesc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String desc = JOptionPane.showInputDialog(ProductDisplay.this, "Enter the new description:", "Change Description", JOptionPane.PLAIN_MESSAGE);
				if (desc != null && !desc.equals("")) {
					product.setDesc(desc);
				} else if (desc.equals("")){
					product.remDesc();
				}
				Cashier.saveData();
				refresh();
			}
		});
		setPrice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String price = JOptionPane.showInputDialog(ProductDisplay.this, "Enter the new price:", "Change Price", JOptionPane.PLAIN_MESSAGE);
				if (price != null) {
					try {
						double newPrice = Double.parseDouble(price);
						product.setPrice(newPrice);
						productInfo.setText("Cost: " + product.getCost() + "€  Price: " + newPrice + "€");
						Cashier.saveData();
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(ProductDisplay.this, "Invalid price received.", "Error", JOptionPane.ERROR_MESSAGE);
					}}
			}
		});
		restock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String restock = JOptionPane.showInputDialog(ProductDisplay.this, "Amount: ", "Restock", JOptionPane.PLAIN_MESSAGE);
				if (restock != null) {
					try {
						int reStock = Integer.parseInt(restock);
						product.restock(reStock);
						productTitle.setText(product.getName() + "    Stock: " + product.getStock());
						Cashier.saveData();
						refresh();
						MainWindow.refresh();
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(ProductDisplay.this, "Invalid amount received.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		clearStock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String toClear = JOptionPane.showInputDialog(ProductDisplay.this, "Amount: ", "Clear Stock", JOptionPane.PLAIN_MESSAGE);
				if (toClear != null) {
					try {
						int clear = Integer.parseInt(toClear);
						product.clearStock(clear);
						productTitle.setText(product.getName() + "    Stock: " + product.getStock());
						Cashier.saveData();
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(ProductDisplay.this, "Invalid amount received.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		moveTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog(ProductDisplay.this, "Select Shelf", true);
				dialog.setSize(300, 400);
				dialog.setLocation(x + 50, y + 40);
				dialog.setLayout(new BorderLayout());

				JPanel shelfPanel = new JPanel();
				shelfPanel.setLayout(new GridLayout(0, 1));

				for (Shelf shelf : Cashier.getShelves()) {
					JButton shelfButton = new JButton(shelf.getName());
					shelfButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (product.getShelf() != null) { product.getShelf().remProduct(product);}
							product.setShelf(shelf);
							shelf.addProduct(product);
							Cashier.saveData();
							dialog.dispose();
						}
					});
					shelfPanel.add(shelfButton);
				}
				JScrollPane scrollPane = new JScrollPane(shelfPanel);
				dialog.add(scrollPane, BorderLayout.CENTER);
				dialog.setVisible(true);
			}
		});
		add(productControls, BorderLayout.CENTER);
		footer = new JLabel("Balance: " + EntityDisplay.round(product.getBalance(), 2) + "€", JLabel.CENTER);
		add(footer, BorderLayout.SOUTH);
		add(header, BorderLayout.NORTH);
		setVisible(true);
	}

	/**
	 * Refreshes the window
	 */
	private void refresh() {
		footer.setText("Balance: " + product.getBalance() + "€");
		productDesc.setText(product.getDesc());
	}
}
