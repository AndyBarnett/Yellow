package we7int;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Contact {
	
	@FindBy(id = "name")
    private WebElement nameField;
	
	@FindBy(id = "email")
    private WebElement emailField;
	
	@FindBy(id = "enquiry")
    private WebElement enquiryField;
	
	@FindBy(id = "contact-button")
    private WebElement sendButton;
		
    public void setName(String name){
		nameField.clear();
		nameField.sendKeys(name);
	}
	
	public String getName(){
		return nameField.getAttribute("value");
	}
	
	public void setEmail(String email){
		emailField.clear();
		emailField.sendKeys(email);
	}
	
	public String getEmail(){
		return emailField.getAttribute("value");
	}
	
	public void setEnquiry(String text){
		enquiryField.clear();
		enquiryField.sendKeys(text);
	}
		
	public void send(){
		sendButton.submit();
	}
}
