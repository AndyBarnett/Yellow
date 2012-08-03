package we7int;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class Join {
	
	 private WebDriver driver;
	 
	 @FindBy(id = "username")
	 private WebElement userName;
	 @FindBy(id = "password")
	 private WebElement password;
	 @FindBy(id = "confirmPassword")
	 private WebElement confirmPassword;
		 
	 @FindBy(id = "email")
	 private WebElement email;
	 @FindBy(id = "form-male")
	 private WebElement male;
	 @FindBy(id = "form-female")
	 private WebElement female;
	 
	 @FindBy(id = "dob.day")
	 private WebElement dobDay;
	 @FindBy(id = "dob.month")
	 private WebElement dobMonth;
	 @FindBy(id = "dob.year")
	 private WebElement dobYear;
	 
		 
	 @FindBy(className = "terms-and-conditions")
	 private WebElement terms;
	 
	 @FindBy(id = "agreedToTerms1")
	 private WebElement agreedToTerms;
	 
	 @FindBy(css = "button[type='submit']")
	 private WebElement submit;
	
	
	 public Join(WebDriver driver){
		 this.driver = driver;
	 }
	 
	 public void enterEmail(String email){
		 this.email.clear();
		 this.email.sendKeys(email);
	 }
	 
	 public String getEmail(String email){
		 return this.email.getText();
	 }
	 
	 public void enterUserName(String userName){
		 this.userName.clear();
		 this.userName.sendKeys(userName);
	 }
	 
	 public void enterPassword(String password, String confirmPassword){
		 
		 this.password.clear();
		 this.password.sendKeys(password);
		 this.confirmPassword.clear();
		 this.confirmPassword.sendKeys(confirmPassword);
		 
	 }
	 
	 public void enterPassword(String password){
		 
		 System.out.println("password: " + password );
		 
		 this.password.clear();
		 this.password.sendKeys(password);
	 }
	 
	 public void enterConfirmPassword(String confirmPassword){
					 
		System.out.println("confirmPassword: " + confirmPassword );
		 
		this.confirmPassword.clear();
		this.confirmPassword.sendKeys(confirmPassword);
	 }
	 
	public void setDOB (String day, String month, String year){
		 
		new Select(dobDay).selectByVisibleText(day);
		new Select(dobMonth).selectByVisibleText(month);
		new Select(dobYear).selectByVisibleText(year);
		
	 }
	 
	 public String getDOB (){
		 
		 return   new Select(dobDay).getFirstSelectedOption().getText() + ":"
		  		+ new Select(dobMonth).getFirstSelectedOption().getText() + ":"
		  		+ new Select(dobYear).getFirstSelectedOption().getText();
	 
	 }
	 
	 public void selectMale (){
		 male.click();
	 }
	 
	 public void selectFemale (){
		 female.click();
	 }
	 
	 public boolean isMaleSelected(){
		return male.isSelected();
	 }
	 
	 public boolean isFemaleSelected(){
		return female.isSelected();
		 
	 }
	 
	 public void checkAgree (boolean agree){
		 if (agree && !agreedToTerms.isSelected()){
			 agreedToTerms.click();
		 }else if (!agree && agreedToTerms.isSelected()){
			 agreedToTerms.click();
		 }
	 }
	 
	 public boolean isAgreeChecked(){
		return false;
	 }
	 
	 public Legal openTermsAndConditions(){
		 terms.click();
		 return PageFactory.initElements(driver, Legal.class);
	 }
	 
	 public String getPageText(){
			return driver.findElement(
					By.xpath("//body")).getText();
		}
	 
	 public Welcome createAccount(){
		 
		 submit.submit();
		 return PageFactory.initElements(driver, Welcome.class);
	 }
	 
	 public LinkAccount createAccountWithExistingEmail(){
		 submit.submit();
		 return PageFactory.initElements(driver, LinkAccount.class);
	 }
		 
	 public enum Gender{MALE,FEMALE};
	 	 
	 public void enterDetails(String userName, String password, String confirmPassword, 
			 		String email, Gender gender, String dob, Boolean legal ){
		
		if (userName != null){enterUserName(userName);}
		if (password != null){enterPassword(password);}
		if (confirmPassword != null){enterConfirmPassword(confirmPassword);}
		if (email != null){enterEmail(email);}
		
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
		
		if (legal!=null){
			if(legal == true){
				checkAgree(true);
			}
			else{
				checkAgree(false);
			}
		}
	 }
}
