package we7int;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utility.DriverUtil;

public class Footer {
	
	 private WebDriver driver;
	
	 public Footer(WebDriver driver){
		 this.driver = driver;
	 }
	 
	 //language independent
	 
	 @FindBy(linkText = "About")
	 private WebElement about;
	 
	 @FindBy(linkText = "Subscribe")
	 private WebElement subscribe;
	 
	 @FindBy(linkText = "Mobile")
	 private WebElement mobile;
	 
	 @FindBy(linkText = "Legal")
	 private WebElement legal;
	 
	 
	 public About getAbout(){
		 about.click();
		 return PageFactory.initElements(driver, About.class);
		 //DriverUtil.waitforURL(driver, "about");
	 }
	 
	 public void getSubscribe(){
		 subscribe.click();
		 DriverUtil.waitforURL(driver, "upgrade");
	 }
	 
	 public void getMobile(){
		 mobile.click();
		 DriverUtil.waitforURL(driver, "mobile");
	 }
	 
	 public Legal getLegal(){
		 legal.click();
		 return PageFactory.initElements(driver, Legal.class);
		 
	 }
}
