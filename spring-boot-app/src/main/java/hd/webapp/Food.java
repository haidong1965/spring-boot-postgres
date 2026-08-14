package hd.webapp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "foods") // Explicitly names the table 'users'
public class Food {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing primary key
	private Long id;

	// Maps to 'name' column
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private int energy;
	
	@Column(nullable = false)
	private double protein;
	
	@Column(nullable = false)
	private double fiber;
	
	@Column(nullable = false)
	private double calcium;
	
	@Column(nullable = false)
	private int type;
	
	public Food() {
		
	}
		
	public Food(String name, int energy, double protein, double fiber, double calcium, int type) {
		this.name = name;
		this.energy = energy;
		this.protein = protein;
		this.fiber = fiber;
		this.calcium = calcium;
		this.type = type;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public double getProtein() {
		return protein;
	}

	public void setProtein(double protein) {
		this.protein = protein;
	}

	public double getFiber() {
		return fiber;
	}

	public void setFiber(double fiber) {
		this.fiber = fiber;
	}

	public double getCalcium() {
		return calcium;
	}

	public void setCalcium(double calcium) {
		this.calcium = calcium;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	


}
