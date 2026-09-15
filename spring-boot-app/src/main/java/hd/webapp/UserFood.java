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
@Table(name = "user_food")
public class UserFood {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "food_id")
	private Long foodId;

	public UserFood() {

	}

	public UserFood(Long user_id, Long food_id, int volumn, int unit) {
		this.volumn = volumn;
		this.unit = unit;
		this.userId = user_id;
		this.foodId = food_id;
	}

	@Column(name = "unit")
	private int unit;

	@Column(name = "volumn")
	private int volumn;

	@Column(name = "date")
	private String date;

}
