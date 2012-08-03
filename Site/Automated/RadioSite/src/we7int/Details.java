package we7int;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import utility.DriverUtil;
import we7int.Join.Gender;


public class Details {
	
	private WebDriver driver;
	
	@FindBy(id = "newPassword")
    private WebElement newPassword;
	
	@FindBy(id = "password2")
    private WebElement confirmNewPassword;
	
	@FindBy(id = "old-password-entry-save-button")
    private WebElement savePasswordButton;
		
	@FindBy(id = "email")
    private WebElement email;
	
	@FindBy(id = "form-male")
    private WebElement maleRadio;
	
	@FindBy(id = "form-female")
    private WebElement femaleRadio;
	
	@FindBy(id="dob.day")
    private WebElement dobDay;
    
    @FindBy(id="dob.month")
    private WebElement dobMonth;
    
    @FindBy(id="dob.year")
    private WebElement dobYear;
    
    @FindBy(className="your-username")
    private WebElement userName;
    
    @FindBy(id="old-password")
    private WebElement oldPassword;
        
    @FindBy(css = "button[type='submit']")
	private WebElement submit;
    
    @FindBy(id="veil-box")
    private WebElement veilBox;
                
	
    public Details(WebDriver driver){
		 this.driver = driver;
	 }
    
    public void setNewPassword(String password){
		 
		 newPassword.clear();
		 newPassword.sendKeys(password);
	}
    
    public void setConfirmNewPassword(String password){
    	
    	confirmNewPassword.clear();
    	confirmNewPassword.sendKeys(password);
    }
    
    public void selectMale(){
       	this.maleRadio.click();
    }
    
    public void selectFemale(){
      	this.femaleRadio.click();
    }

    public boolean isMaleSelected(){
		return maleRadio.isSelected();
	}
	
	public boolean isFemaleSelected(){
		return femaleRadio.isSelected();
	}
	
		
	public String getDOB (){
				
		return  new Select(dobDay).getFirstSelectedOption().getText() + ":"
			  + new Select(dobMonth).getFirstSelectedOption().getText() + ":"
			  + new Select(dobYear).getFirstSelectedOption().getText();
	}
	
	public void setDOB (String day, String month, String year){
		 
		new Select(dobDay).selectByVisibleText(day);
		new Select(dobMonth).selectByVisibleText(month);
		new Select(dobYear).selectByVisibleText(year);
	}
    
    public String getEmail(){
       	return email.getAttribute("value");
    }
    
    public void setEmail(String email){
       	 this.email.clear();
       	 this.email.sendKeys(email);
    }
    
    public class PasswordDlg{
       	public SuccessVeil confirmPassword(String password){
       		
       		oldPassword.sendKeys(password);
    		savePasswordButton.click();
    		
      		return new SuccessVeil(); 
    	}
    }
    
    public class SuccessVeil{
    	
    	public String getText(){
   			return veilBox.getText();
    	}
    	
    	 public void close(){
    	  	//this closes itself after 3 secs
    	   	DriverUtil.pauseTest(5000);   	  
    	 }
   	}
    
    public PasswordDlg saveChanges(){
           	submit.submit();
        	return new PasswordDlg();
    }
    
    public String getUserName(){
    	return userName.getText();
    }
  
    public String getPageText(){
		return driver.findElement(
				By.xpath("//body")).getText();
	}
    
    
    public void enterDetails(String password, String confirmPassword, 
	 				String email, Gender gender, String dob ){

		if (password != null){setNewPassword(password);}
		if (confirmPassword != null){setConfirmNewPassword(confirmPassword);}
		if (email != null){setEmail(email);}
		
		if (gender!=null){
			if (gender == Gender.MALE){
				selectMale();
			}else if ((gender == Gender.FEMALE)){
				selectFemale();
			}
		}
					
		if (dob!=null){
			String [] dayMonthYear = dob.split(":");
			setDOB(dayMonthYear[0],dayMonthYear[1], dayMonthYear[2]);
		}
    }
}
