package windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import store.Cashier;
import store.Product;
import store.Shelf;
import entities.Employee;
import entities.Positions;
import entities.Client;

/**
 * Entity Manager Window
 * @author Daniel Salgueiro
 */
public  class EntityManager extends JFrame {
	//Refresh-able panels
	private JPanel employeesPanel;
	private JPanel clientsPanel;

	/**
	 * Entity Manager window object
	 */
	public EntityManager() {
		setTitle("Entity Manager");
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
		JButton addEmployee = new JButton("New Employee +");
		JButton addClient = new JButton("New Client +");
		JButton findEntity = new JButton("Find...");

		addEmployee.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField nameField = new JTextField();
				JComboBox<Positions> positionPicker = new JComboBox<>(Positions.values());

				Object[] fields = {
					"Name:", nameField,
					"Position", positionPicker,
				};    
				int option = JOptionPane.showConfirmDialog(null, fields, "New Employee +", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					String name = nameField.getText();
					Positions pos = (Positions) positionPicker.getSelectedItem();

					Employee employee = new Employee(name, Cashier.generateCode(), pos);
					Cashier.addEntity(employee);
					Cashier.saveData();
					refresh();
				}
			}
		});
		addClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(EntityManager.this, "Client's name:", "New Client", JOptionPane.PLAIN_MESSAGE);
				if (name != null) {
					Client client = new Client(name.trim(), Cashier.generateCode());
					Cashier.addEntity(client);
					Cashier.saveData();
					refresh();
				}

			}
		});
		findEntity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String searchCode = JOptionPane.showInputDialog(EntityManager.this, "Enter a code to search:", "Find...", JOptionPane.PLAIN_MESSAGE);
				if (searchCode != null) {
					int code = Integer.parseInt(searchCode);
					for (Client client : Cashier.getEntities()) {
						if (client.getCode() == code) {
							if (client instanceof Employee) {
								new EmployeeDisplay(client, EntityManager.this);
								return;
							} else {
								new ClientDisplay(client, EntityManager.this);
								return;
							}
						}
					}
					JOptionPane.showMessageDialog(null, "No entity with the code " + searchCode + " was found.", "Entity not found", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		//Employees & Clients
		employeesPanel = new JPanel();
		employeesPanel.setLayout(new BoxLayout(employeesPanel, BoxLayout.Y_AXIS));
		JScrollPane employees = new JScrollPane(employeesPanel);

		clientsPanel = new JPanel();
		clientsPanel.setLayout(new BoxLayout(clientsPanel, BoxLayout.Y_AXIS));
		JScrollPane clients = new JScrollPane(clientsPanel);
		refresh();

		//Main Panel
		JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		mainPanel.add(employees);
		mainPanel.add(clients);
		add(mainPanel, BorderLayout.CENTER);

		//Footer
		JLabel footer = new JLabel("Virtual Market", JLabel.CENTER);
		add(footer, BorderLayout.SOUTH);

		toolBar.add(addEmployee);
		toolBar.add(addClient);
		toolBar.add(findEntity);
		add(toolBar, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		setVisible(true);
	}

	/**
	 * Refreshes the window
	 */
	public void refresh() {
		employeesPanel.removeAll();
		clientsPanel.removeAll();

		//Employees
		JLabel employeesHeader = new JLabel("Employees");
		employeesHeader.setOpaque(true);
		employeesHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		employeesHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		employeesHeader.setHorizontalAlignment(JLabel.CENTER);
		employeesPanel.add(employeesHeader);

		//Clients
		JLabel clientsHeader = new JLabel("Clients");
		clientsHeader.setOpaque(true);
		clientsHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		clientsHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		clientsHeader.setHorizontalAlignment(JLabel.CENTER);
		clientsPanel.add(clientsHeader);

		for (Client client: Cashier.getEntities()) {
			JButton clientButton = new JButton(client.getName() + " (" + client.getCode() +")");
			clientButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			clientButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			clientButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (client instanceof Employee) {
						new EmployeeDisplay(client, EntityManager.this);
					} else {
						new ClientDisplay(client, EntityManager.this);
					}
				}
			});
			if (client instanceof Employee) {	
				employeesPanel.add(clientButton);
			} else {
				clientsPanel.add(clientButton);
			}
		}        
		employeesPanel.revalidate();
		employeesPanel.repaint();
		clientsPanel.revalidate();
		clientsPanel.repaint();
	}
}
