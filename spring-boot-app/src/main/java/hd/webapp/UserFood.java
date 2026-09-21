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

	public UserFood(Long userId, Long foodId, int volume, int unit) {
		this.volume = volume;
		this.unit = unit;
		this.userId = userId;
		this.foodId = foodId;
	}

	@Column(name = "unit")
	private int unit;

	@Column(name = "volume")
	private int volume;

	@Column(name = "date")
	private String date;

}
