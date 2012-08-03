package we7int;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LinkAccount {
		
	@FindBy(id = "email")
	private WebElement textEmail;
	
	@FindBy(id = "password")
	private WebElement textPassword;
	
	@FindBy(id = "signin-button")
	private WebElement buttonSignIn;
	
	public String getPageText(){
		return driver.findElement(
				By.xpath("//body")).getText();
	}
	
	private WebDriver driver = null;
		   
	public LinkAccount(WebDriver driver){
         this.driver = driver;
    }
	
	public LinkAccount enterEmail(String email){
		textEmail.sendKeys(email);
		return this;
	}
	
	public LinkAccount enterPassword(String password){
		textPassword.sendKeys(password);
		return this;
	}
	
	public String getEmail(){
		return textEmail.getText();
	}
	
	public String getpassword(){
		return textPassword.getText();
	}
			
	public Home signIn(){
		buttonSignIn.submit();
		return PageFactory.initElements(driver, Home.class);	
	}
	
	public Home signIn(String email, String password){
		enterEmail(email).enterPassword(password);
		return signIn();
	}
}
