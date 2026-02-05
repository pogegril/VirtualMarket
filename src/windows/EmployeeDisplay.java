package windows;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import entities.Client;
import entities.Employee;
import entities.Positions;
import store.Cashier;

/**
 * Employee Display Window
 * @author Daniel Salgueiro
 */
public class EmployeeDisplay extends EntityDisplay {
	//Refresh-able panel
	private Employee employee;

	/**
	 * Employee Display window object
	 * @param client - Employee's client object
	 * @param entityManager - Entity manager window
	 */
	public EmployeeDisplay(Client client, EntityManager entityManager) {
		super(client, entityManager);
		this.employee = (Employee) client;

		//Image handling & Employee info
		ImageIcon icon = new ImageIcon(new ImageIcon("img/employee.png").getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH));
		JLabel JIcon = new JLabel(icon);
		JIcon.setBorder(BorderFactory.createEmptyBorder(15, 25, 5, 10));
		JLabel employeeInfo = new JLabel("Position: " + employee.getPosition()  + "    Salary: " + employee.getSalary());

		//Employee specific commands
		JButton raiseButton = new JButton("Raise");
		JButton positionButton = new JButton("Position");
		JButton fireButton = new JButton("Fire");

		raiseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String raiseInput = JOptionPane.showInputDialog(EmployeeDisplay.this, "Enter the raise percentage", "Raise", JOptionPane.PLAIN_MESSAGE);
				if (raiseInput != null) {
					double raise = Double.parseDouble(raiseInput);
					employee.raise(raise);
					Cashier.saveData();
					employeeInfo.setText("Position: " + employee.getPosition()  + "    Salary: " + employee.getSalary());
					refresh();
				} 
			}
		}) ; 
		positionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {  		
				JComboBox<Positions> positionPicker = new JComboBox<>(Positions.values());

				Object[] fields = {
					"Position", positionPicker,
				};    
				int option = JOptionPane.showConfirmDialog(null, fields, "Select a job position:", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					Positions pos = (Positions) positionPicker.getSelectedItem();
					employee.setPosition(pos);
					Cashier.saveData();
					employeeInfo.setText("Position: " + employee.getPosition()  + "    Salary: " + employee.getSalary());
					refresh();
				}
			}
		});
		fireButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client client = new Client(employee.getName(), employee.getCode());
				client.givePoints(employee.getPoints());
				client.setBasket(employee.getBasket());
				Cashier.remEntity(employee);
				Cashier.addEntity(client);
				Cashier.saveData();
				refresh();
				entityManager.refresh();
				new ClientDisplay(client, entityManager);
				dispose();
			}
		});
		entityPanel.add(employeeInfo);
		header.add(JIcon, BorderLayout.WEST);
		this.entityControls.add(raiseButton);
		this.entityControls.add(positionButton);
		this.entityControls.add(fireButton);       
		setVisible(true);
	}
}
