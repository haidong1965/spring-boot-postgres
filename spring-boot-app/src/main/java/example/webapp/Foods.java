package example.webapp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FOODS") // Explicitly names the table 'users'
public class Foods {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing primary key
	private Long id;

	// Maps to 'name' column
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String enerfy;
	
	@Column(nullable = false)
	private String protein;
	
	@Column(nullable = false)
	private String dietaryFiber;
	
	@Column(nullable = false)
	private String calcium;
	
	@Column(nullable = false)
	private int foodType;

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

	public String getEnerfy() {
		return enerfy;
	}

	public void setEnerfy(String enerfy) {
		this.enerfy = enerfy;
	}

	public String getProtein() {
		return protein;
	}

	public void setProtein(String protein) {
		this.protein = protein;
	}

	public String getDietaryFiber() {
		return dietaryFiber;
	}

	public void setDietaryFiber(String dietaryFiber) {
		this.dietaryFiber = dietaryFiber;
	}

	public String getCalcium() {
		return calcium;
	}

	public void setCalcium(String calcium) {
		this.calcium = calcium;
	}

	public int getFoodType() {
		return foodType;
	}

	public void setFoodType(int foodType) {
		this.foodType = foodType;
	}

}
