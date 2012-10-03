package we7int;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ForgottenPasswordSuccess {
	
	@FindBy(linkText = "Sign in")
	private WebElement signInButton;
	
	public ForgottenPasswordSuccess(final WebDriver driver){
		 this.driver = driver;
	}
	
	private WebDriver driver;
	
	public SignIn getSignInPage(){
		signInButton.click();
		return PageFactory.initElements(driver, SignIn.class);
	}
	
	public String getPageText(){
			return driver.findElement(
					By.xpath("//body")).getText();
	}
}
