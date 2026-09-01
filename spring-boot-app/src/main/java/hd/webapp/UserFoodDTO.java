package hd.webapp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserFoodDTO {
	
	private final Long id;
	private final String name;
	private final int energy;
	private final double protein;
	private final double fiber;
	private final double calcium;
    
    public UserFoodDTO(Long id, String name, int energy, double protein, double fiber, double calcium) {
    	this.id = id;
        this.name = name;
        this.energy = energy;
        this.protein = protein;
        this.fiber = fiber;
        this.calcium = calcium;
    }
}