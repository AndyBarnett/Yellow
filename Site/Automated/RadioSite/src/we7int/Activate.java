package we7int;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Activate {
	
	private WebDriver driver = null;
	   
	public Activate(WebDriver driver){
         this.driver = driver;
    }
	
	@FindBy(className = "popup-close-button")
	private WebElement closePopup;
		
	@FindBy(id = "signin-unactivated-resend-button")
	private WebElement resendButton;
	
	public String getText(){
		return driver.findElement(By.xpath("//body")).getText();
	}

	public SignIn closeActivationPopup(){
		closePopup.click();
		return PageFactory.initElements(driver, SignIn.class);
	}
}
