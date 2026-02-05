package entities;

/**
 * Enum with available job positions and it's default data
 * @author Daniel Salgueiro
 */
public enum Positions {

	CASHIER(10, 890.00),
	STOCKER(12, 950.00),
	WORKER(15, 1100.00), 
	MANAGER(15, 4250.00);

	private final double discount;

	private final double salary;

	/**
	 * Constructs a job position entry
	 * @param discount - Position's specific discount
	 * @param salary - Position's starter salary
	 */
	Positions(double discount, double salary) {
		if (discount < 0 || discount > 20) { throw new IllegalArgumentException("The valid employee discounts range from 0% to 20%."); } 
		this.discount = discount;
		if (salary < 0) { throw new IllegalArgumentException("The employee's salary can't be negative."); } 
		this.salary = salary;
	}

	/**
	 * Returns all of the job positions available
	 * @return positions
	 */
	public Positions[] getPositions() {
		return Positions.values();
	}

	/**
	 * Returns this job's discount
	 * @return discount
	 */
	public double discount() {
		return this.discount;
	}

	/**
	 * Returns this job's initial salary
	 * @return salary
	 */
	public double getSalary() {
		return this.salary;
	}
}
