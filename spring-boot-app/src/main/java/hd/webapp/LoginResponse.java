package hd.webapp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private boolean  success;
    private String token;
    private String name; 
	private String redirectUrl;

    public LoginResponse(boolean success, String token, String redirectUrl, String name) {
        this.success = success;
        this.token = token;
        this.redirectUrl = redirectUrl;
        this.name = name;
    }
}
