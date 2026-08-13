package example.webapp;

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



	@Column(name = "user_id")
	private Long user_id;
	
	@Column(name = "food_id")
	private Long food_id;
	
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public Long getFood_id() {
		return food_id;
	}
	public void setFood_id(Long food_id) {
		this.food_id = food_id;
	}
	
	public UserFood(Long user_id, Long food_id, int volumn, int unit) {
		this.volumn = volumn;
		this.unit = unit;
		this.user_id = user_id;
		this.food_id = food_id;
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
