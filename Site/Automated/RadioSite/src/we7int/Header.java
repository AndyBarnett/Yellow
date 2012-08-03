package we7int;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Header {

	private WebDriver driver = null;
		      
    @FindBy(linkText = "Sign in")
	private WebElement signInLink;
    
    @FindBy(linkText = "Join")
	private WebElement joinWe7;
    
    @FindBy(id = "user-name-welcome")
	private WebElement signInDisplayName;
    
    @FindBy(linkText = "Sign out")
	private WebElement signOutLink;
	
	public Header(WebDriver driver){
         this.driver = driver;
    }
    
    public SocialSignIn getSocialSignIn(){
		return PageFactory.initElements(driver, SocialSignIn.class);
    }
    
    public void signOut(){
        signOutLink.click();
    }
    
    public String getWelcomeName(){
    	
    	/*prevent scenario where element is present but has no value*/
    	
    	int i=0;
    	while(signInDisplayName.getText().equals("") && i < 20){
    		try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
    	}
    	
    	return signInDisplayName.getText();
    }
    
        
    public SignIn getSignInPage(){
    	
    	signInLink.click();
    	return PageFactory.initElements(driver, SignIn.class);
    }
    
     public String getTitle(){
       	return driver.getTitle();	
    }
 	 
	 public Join getJoinPage(){
		 
		 joinWe7.click();
		 return PageFactory.initElements(driver, Join.class);
		 
	 }
	 
	 public Account getAccountsPage(){
		 
		 signInDisplayName.click();
		 return PageFactory.initElements(driver, Account.class);
		 
	 }
}


