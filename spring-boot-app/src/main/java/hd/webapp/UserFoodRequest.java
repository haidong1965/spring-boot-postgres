package hd.webapp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class UserFoodRequest {

    @NotNull
    private String userName;
    
    @NotNull
    private String foodName;
    

    
	@NotNull
	@Positive
	private int volumn;
	
    @PositiveOrZero
    private int unit;
	
	
	public UserFoodRequest(String userName, String foodName, int volumn, int unit) {
		this.userName = userName;
		this.foodName = foodName;
		this.volumn = volumn;
		this.unit = unit;
	}
	
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