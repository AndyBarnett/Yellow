package we7int;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class About {
	
	@FindBy(className = "about-privacy-policy")
	private WebElement privacyLink;
	
	@FindBy(className = "about-terms")
	private WebElement termsLink;
	
	@FindBy(id = "contact-tab")
	private WebElement contactTab;
	
	@FindBy(id = "about-tab")
	private WebElement aboutTab;
	
	@FindBy(className = "about-content")
	private WebElement aboutContent;
	
	public String getText(){
		return aboutContent.getText();
	}
	
	private WebDriver driver = null;
		   
	public About(WebDriver driver){
         this.driver = driver;
    }
	
	public Legal getPrivacy(){
		privacyLink.click();
		return PageFactory.initElements(driver, Legal.class);
		
	}
	
	public Legal getTerms(){
		termsLink.click();
		return PageFactory.initElements(driver, Legal.class);
		
	}
	
	public void selectAboutTab(){
		aboutTab.click();
	}
	
	public void selectContactUsTab(){
		contactTab.click();
	}
}
