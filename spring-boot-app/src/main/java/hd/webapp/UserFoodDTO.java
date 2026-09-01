package hd.webapp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserFoodDTO {
	
	private final Long id;
	private final Long userId;
	private final Long foodId;
	private final String userName;
	private final String foodName;
	private final int energy;
	private final double protein;
	private final double fiber;
	private final double calcium;
    
    public UserFoodDTO(Long id, Long userId, Long foodId, String userName, String foodName, int energy, double protein, double fiber, double calcium) {
    	this.id = id;
    	this.userId = userId;
    	this.foodId = foodId;
        this.userName = userName;
        this.foodName = foodName;
        this.energy = energy;
        this.protein = protein;
        this.fiber = fiber;
        this.calcium = calcium;
    }



}