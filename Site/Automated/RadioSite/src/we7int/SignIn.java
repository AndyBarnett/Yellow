package we7int;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignIn {
	
	private WebDriver driver;
	 
	@FindBy(id = "username")
	private WebElement userName;
	
	@FindBy(id = "password")
	private WebElement password;
	
	@FindBy(linkText = "Forgotten password?")
	private WebElement forgottenPassword;
	
	@FindBy(css = "button[type='submit']")
	private WebElement submit;
	
	
	public SignIn(final WebDriver driver){
		
		 this.driver = driver;
	}
	
	public void enterUserName(String userName){
		
		this.userName.clear();
		this.userName.sendKeys(userName);
	}
	
	public void enterPassword(String userName){
		this.password.clear();
		this.password.sendKeys(userName);
	}
	
	public SignIn enterDetails(String userName, String password){
		enterUserName(userName);
		enterPassword(password);
		return this;
	}
	
	public Home submit(){
		submit.submit();
		return PageFactory.initElements(driver, Home.class);
	}
	
	public Activate signInNoActivation(String userName, String password){
		enterDetails(userName,password);
		submit();
		return PageFactory.initElements(driver, Activate.class);
	}
	
	public String getPageText(){
		return driver.findElement(
				By.xpath("//body")).getText();
	}
		
	public Home signIn(String userName, String password){
		enterDetails(userName,password);
		submit();
		return PageFactory.initElements(driver, Home.class);
	}
	
	public ForgottenPassword getForgottenPasswordPage(){
		forgottenPassword.click();
		return PageFactory.initElements(driver, ForgottenPassword.class);
	}
}
