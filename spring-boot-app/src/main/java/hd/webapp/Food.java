package hd.webapp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}
