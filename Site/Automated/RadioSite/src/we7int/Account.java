package we7int;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Account {
	
	 @FindBy(linkText = "Details")
		private WebElement detailsLink;
	 
	 @FindBy(linkText = "Settings")
		private WebElement settingsLink;
	 
	 @FindBy(linkText = "Contact")
		private WebElement contactLink;
	 
	 @FindBy(linkText = "Promotions")
		private WebElement promotionsLink;
		
	private WebDriver driver = null;
	
	public Account(WebDriver driver){
        this.driver = driver;
   }
	
	public Details getDetailsTab(){
		detailsLink.click();
		return PageFactory.initElements(driver, Details.class);
	}
	
	public Contact getContactTab(){
		contactLink.click();
		return PageFactory.initElements(driver, Contact.class);
	}
//NEW
	public Promotions getPromotionsTab(){
		promotionsLink.click();
		return PageFactory.initElements(driver, Promotions.class);
	}
//NEW
}
