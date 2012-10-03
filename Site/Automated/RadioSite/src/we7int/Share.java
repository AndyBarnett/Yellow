package we7int;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.thoughtworks.selenium.Wait;

public class Share {
	 
	private WebDriver driver;
		
	@FindBy(css= "li.like-button-item div.like-button span.like-button-text")
	private WebElement share;
	
	@FindBy(css = "div.popup-close-button")
	private WebElement veilClose;
	
	
	//Email stuff
	@FindBy(id = "shared-panel-email-content_tbFriendsEmail")
	private WebElement toBox;
	
	@FindBy(id = "shared-panel-email-content_tbYourEmail")
	private WebElement fromBox;
	
	@FindBy(id = "shared-panel-email-content_tbMessage")
	private WebElement messageBox;
	
	@FindBy(css = "tr.root-row-3 td div.button span.button-logo")
	private WebElement sendButton;

	//Social stuff
	@FindBy(xpath = "//*[@class='icon facebook']")
	private WebElement facebookIcon;
	
	@FindBy(xpath = "//*[@class='icon twitter']")
	private WebElement twitterIcon;
	
	//Link stuff
	@FindBy(id = "veil-share-link")
	private WebElement linkBox;
	
	
	public Share(WebDriver driver){
		this.driver = driver;
	}
	
	public void clickShare(){
		share.click();
	}
	
	public void clickFacebook(){
		facebookIcon.click();
	}
	
	public void clickTwitter(){
		twitterIcon.click();
	}
	
	public void getTab(String name) {
		driver.findElement(By.xpath(String.format("//*[@class='title' and contains(text(),'%s')]", name))).click();
	}
	
	public String getLinkBox(){
		return linkBox.getAttribute("value");
	}
	//Social stuff
	
	public boolean clickIcons(int count) throws InterruptedException{
		List<WebElement> icons = driver.findElements(By.cssSelector("div.icon-container div.icon"));
		
		for(WebElement e : icons){
			e.click();
			
		}
		return (driver.getWindowHandles().size() == count);
	}

			public static boolean windowOpenedFromLink(final WebDriver driver, final String windowTitle){
				 
					final String currentWindow = driver.getWindowHandle();
					 
					new Wait("Window was not found: " +  windowTitle) {
				    	public boolean until() {
			    		 for (String handle : driver.getWindowHandles()) { 
					 		 if(handle.equals(currentWindow) == false &&
					 			driver.switchTo().window(handle).getTitle().contains(windowTitle)){
					 			driver.close();
					 			driver.switchTo().window(currentWindow);
					 			return true;
					 		 }
				 		  }
				 		  return false;
				    	}
				     };
				     
					 return true;
				 }		
	
			
	//END OF Social Stuff
	//Email stuff
	public void setEmailTo(String toAddress){
		toBox.sendKeys(toAddress);
	}
	
	public void setEmailFrom(String fromAddress){
		fromBox.sendKeys(fromAddress);
	}
	
	public void setMessage(String message){
		messageBox.sendKeys(message);
	}
	
	public void clickSendButton(){
		sendButton.click();
	}
	
	//END OF Email Stuff
	public Share getCloseButton(){
		veilClose.click();
		return null;
		}
	 
	public String getPageText(){
		return driver.findElement(
				By.xpath("//body")).getText();
		}
}
