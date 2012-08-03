package we7int;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utility.DriverUtil;

import com.thoughtworks.selenium.Wait;

public class SocialSignIn {
	
	 private WebDriver driver;
	 private String mainWindow;
	 
	 public static final String apiKey = "2_Rwfxsxx7o84IpdtHamLYZ5IK2T8qzvPnYSCS_AXxEYDQzXnDZ3Je7sEGOIDk6mJR";
	 public static final String apiSecretKey = "eOKsYeX5TcUJsv60N6h9M1dEhl4nyZpo70hHKipWzLU=";
	 
	 //Facebook
	 
	 @FindBy(className = "facebook-small")
	 private WebElement facebookLogo;
	 @FindBy(id= "email")
	 private WebElement facebookEmail;
	 @FindBy(id = "pass")
	 private WebElement facebookPass;
	 //Login
	 @FindBy(xpath = "//input[@type='submit']")
 	 private WebElement facebookSubmit;
	 
	//Install app
	 @FindBy(xpath = "//input[@type='submit']")
 	 private WebElement facebookGrantApp;
	 
	 
	 //Twitter
	 
	 @FindBy(className = "twitter-small")
	 private WebElement twitterLogo;
	 @FindBy(id = "username_or_email")
	 private WebElement twitterUsernameOrEmail;
	 @FindBy(id = "password")
	 private WebElement twitterPass;
	 @FindBy(id = "allow")
	 private WebElement twitterSubmit;
	  
		 
	 private void enterFaceBookJoinDetails (String email, String password){
		 
		 facebookLogo.click();
		 waitForWindowTitle("Facebook");
		 		 
		 facebookEmail.clear();
		 facebookEmail.sendKeys(email);
		 facebookPass.clear();
		 facebookPass.sendKeys(password);
		 facebookSubmit.submit(); //login
		
		 DriverUtil.pauseTest(5000); //facebook pages resizes wjen loaded
	 }
	 
	 public Home joinWithFaceBook(String email, String password){
		 
		 enterFaceBookJoinDetails(email,password);
		
		 facebookGrantApp.click();
				 
		 waitForWindowCount(1);
		 switchToMainWindow();
		 
		 return PageFactory.initElements(driver, Home.class);	
	 }
	 
	 public void signInWithFaceBook(String email, String password){
		  
		 enterFaceBookJoinDetails(email,password);
		 waitForWindowCount(1);
		 switchToMainWindow();
	 }
	 
	 public void signInWithFaceBook(){
			 facebookLogo.click();
			 waitForWindowCount(2);
			 waitForWindowCount(1);
			 switchToMainWindow();
	 }
	 	 
	 public void enterTwitterDetails(String usernameOrEmail, String password){
		 
		 twitterLogo.click();
		 waitForWindowTitle("Twitter");
		 twitterUsernameOrEmail.clear();
		 twitterUsernameOrEmail.sendKeys(usernameOrEmail);
		 twitterPass.clear();
		 twitterPass.sendKeys(password);
		 twitterSubmit.submit();
	 }
	 
	 
	 public Join joinWithTwitter(String usernameOrEmail, String password){
		 
		 signInWithTwitter(usernameOrEmail,password);
		 return PageFactory.initElements(driver, Join.class);
		 
	 }
	 
	 public Home signInWithTwitter(String usernameOrEmail, String password){
		 
		 enterTwitterDetails(usernameOrEmail,password);
		 waitForWindowCount(1);
		 switchToMainWindow();
		
		 return PageFactory.initElements(driver, Home.class);
	 }
		 
	 public Home signInWithTwitter(){
		 
		 twitterLogo.click();
		 waitForWindowCount(2);
		 waitForWindowCount(1);
		 switchToMainWindow();
		 		 
		 return PageFactory.initElements(driver, Home.class);
		 
	 }
		 
	 public SocialSignIn(WebDriver driver){
		 
		 this.driver = driver;
		 mainWindow = driver.getWindowHandle();
	 }
	 
	 public void setformat(int format){
		 
	 }
	 
	 private void switchToMainWindow(){
		 driver.switchTo().window(mainWindow);
	 }
	 
	 private boolean waitForWindowTitle(final String windowTitle){
		 
		 final String currentWindow = driver.getWindowHandle();
		 
		 new Wait("Window was not found: " +  windowTitle) {
	    	public boolean until() {
    		 for (String handle : driver.getWindowHandles()) { 
		 		 if(handle.equals(currentWindow) == false &&
		 			driver.switchTo().window(handle).getTitle().contains(windowTitle)){
		 			return true;
		 		 }
	 		  }
	 		  return false;
	    	}
	     };
		     
		 return true;
	}
     
     public int getWindowCount(){
		return driver.getWindowHandles().size();
     }
     
     public void waitForWindowCount(final int count){
    	 
    	 new Wait("Window count was not reached") {
 	    	public boolean until() {
     		  return driver.getWindowHandles().size() == count;
 	    	}
 	     };
     }
 }
