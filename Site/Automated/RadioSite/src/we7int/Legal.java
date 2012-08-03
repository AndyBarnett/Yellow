package we7int;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utility.DriverUtil;

public class Legal {
	
	 //language independent
	
		  
	 @FindBy(className = "popup-header")
	 private WebElement header;
	 
	 @FindBy(className = "popup-close-button")
	 private WebElement close;
	 	 
	// tabs
	 
	 @FindBy(xpath = "//span[@data-show='privacy']")
	 private WebElement privacyLink;
	 
	 @FindBy(className = "privacy-policy-text")
	 private WebElement privacyText;
	 
	 @FindBy(xpath = "//span[@data-show='terms']")
	 private WebElement termsLink;
	 
	 @FindBy(className = "terms-and-conditions-text")
	 private WebElement termsText;
	 	 
	 private WebDriver driver = null;
	 
	 public Legal(WebDriver driver){
	         this.driver = driver;
	 }
	 
	 //language independent
	 
	 public void selectTerms(){
		 DriverUtil.pauseTest(3000); //remove later
		 termsLink.click();
	 }
	 
	 public void selectPrivacyPolicy(){
		 DriverUtil.pauseTest(3000); //remove later
		 privacyLink.click();
	 }
	 
	 public String getTermsText(){
		 return termsText.getText();
	 }
	 
	 //language independent
	 
	 public String getPrivacyText(){
		return privacyText.getText();
	 }
	 
	 public void close(){
	
		close.click();
	 	DriverUtil.WaitUntilElementNotPresent(
			driver, By.id("veil-box"), "Could not close Legal veil");
	 }
	 
	 public String getTitle(){
		 return header.getText();
	 }
	 
	 public boolean isVisible(){
		return false;
		 
	 }
	 
}
