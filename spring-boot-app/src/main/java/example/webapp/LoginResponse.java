package example.webapp;

public class LoginResponse {
    private boolean  success;
    private String token;
    private String name; 
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	private String redirectUrl; // Where the frontend should go next

    // Constructor (Recommended for easy instantiation)
    public LoginResponse(boolean success, String token, String redirectUrl, String name) {
        this.success = success;
        this.token = token;
        this.redirectUrl = redirectUrl;
        this.name = name;
    }

    public boolean isSuccess() { return success; }
    public String getToken() { return token; }
    public String getRedirectUrl() { return redirectUrl; }
}
