package we7int;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Welcome {
	
	private WebDriver driver = null;
	
	@FindBy(linkText = "Start listening")
	private WebElement startListeningButton;
	
	@FindBy(className = "join-success-page")
	private WebElement pageTextDiv;
	
	public String getPageText(){
		return pageTextDiv.getText();
	}
			   
	public Welcome(WebDriver driver){
         this.driver = driver;
    }
	
	public Home startListining(){
		startListeningButton.click();
		return PageFactory.initElements(driver, Home.class);
	}
}