package hd.webapp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserFoodDTO {
	
	private final Long id;
	private final Long user_id;
	private final String name;
	private final int energy;
	private final double protein;
	private final double fiber;
	private final double calcium;
	private String date;
	private int unit;
	private int volumn;
	
    
    public UserFoodDTO(Long id, Long user_id, String name, int energy, double protein, double fiber, double calcium, String date, int unit, int volumn) {
    	this.id = id;
        this.name = name;
        this.energy = energy;
        this.protein = protein;
        this.fiber = fiber;
        this.calcium = calcium;
        this.user_id = user_id;
        this.date = date;
        this.unit = unit;
        this.volumn = volumn;
    }
}