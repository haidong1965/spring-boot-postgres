package hd.webapp;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_food")
public class UserFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	public Long getId() {
		return id;
	}
	
	@Column(name = "name")
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "energy")
	private int energy;
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
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "protein")
	private double protein;
	@Column(name = "fiber")
	private double fiber;
	@Column(name = "calcium")
	private double calcium;

	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "food_id")
	private Long foodId;
	
	public Long getUserId() {
		return userId;
	}
	public void setUser_id(Long userId) {
		this.userId = userId;
	}
	public Long getFoodId() {
		return foodId;
	}
	public void setFoodId(Long foodId) {
		this.foodId = foodId;
	}
	
	public  UserFood() {
		
	}
	
	public UserFood(Long user_id, Long food_id, int volumn, int unit) {
		this.volumn = volumn;
		this.unit = unit;
		this.userId = user_id;
		this.foodId = food_id;
	}
    
	@Column(name = "unit")
    private int unit;
	
    public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	
	@Column(name = "volumn")
	public int getVolumn() {
		return volumn;
	}
	public void setVolumn(int volumn) {
		this.volumn = volumn;
	}
	private int volumn;
    
	@Column(name = "date")
	private LocalDate date;
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}

}
