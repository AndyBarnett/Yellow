package we7int;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ForgottenPassword {
	
	@FindBy(id = "email")
	private WebElement emailField;
	
	@FindBy(id = "forgot-button")
	private WebElement forgotButton;
	
	
	public ForgottenPassword(final WebDriver driver){
		 this.driver = driver;
	}
	
	private WebDriver driver;
	
	public void setEmail(String email){
		emailField.clear();
		emailField.sendKeys(email);
	}
	
	public String getEmail(){
		return emailField.getAttribute("value");
	}
	
	public void sendReminder(){
		forgotButton.submit();
	}
	
	public ForgottenPasswordSuccess sendReminder(String email){
		setEmail(email);
		forgotButton.submit();
		return PageFactory.initElements(driver, ForgottenPasswordSuccess.class);
	}
}
