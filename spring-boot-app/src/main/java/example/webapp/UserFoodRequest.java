package example.webapp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class UserFoodRequest {

    @NotNull
    private String userName;
    
    @NotNull
    private String foodName;
    
    @PositiveOrZero
    private int unit;
    
	@NotNull
	@Positive
	private int volumn;

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}
     
    public int getUnit() {
		return unit;
	}
  
	public void setUnit(int unit) {
		this.unit = unit;
	}
	
	public int getVolumn() {
		return volumn;
	}

	public void setVolumn(int volumn) {
		this.volumn = volumn;
	}

}