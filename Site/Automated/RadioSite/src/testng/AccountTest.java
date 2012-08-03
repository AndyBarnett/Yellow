package testng;

import java.sql.SQLException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utility.DBConnection;
import utility.DriverUtil;
import we7int.Application;
import we7int.Details;
import we7int.Details.SuccessVeil;
import we7int.Join.Gender;
import we7int.Join;
import we7int.Welcome;

public class AccountTest extends MethodConfiguration{
	
	@Test(description = "Change user settings", retryAnalyzer = MethodConfiguration.class, enabled=true)
	public void changeUserSettings(ITestContext tc) throws ClassNotFoundException, SQLException, InterruptedException {
		
		String userName = DriverUtil.getUniqueUser("user");		
		String email = "frank-" + userName + "@we7.com";
		
		Application application = new Application(driver);
		Join joinPage = application.getHeader().getJoinPage();
		
		joinPage.enterUserName(userName);
		joinPage.enterPassword("password", "password");
		joinPage.enterEmail(email);
		joinPage.selectMale();
		joinPage.checkAgree(true);
		joinPage.setDOB("12", "Mar", "1980");
		
		Welcome welcomePage = joinPage.createAccount();
		Assert.assertTrue(welcomePage.getPageText().contains("WELCOME"));
		welcomePage.startListining();
		Assert.assertEquals(application.getHeader().getWelcomeName(), DriverUtil.formatDisplayName(userName));
				
		application.activateUserDatabase(DBConnection.getConnectionFromXML(tc), email);
		
		Details userDetails = application.getHeader().getAccountsPage().getDetailsTab();
		
		Assert.assertEquals(userDetails.getUserName(),userName);
		Assert.assertEquals(userDetails.getDOB(), "12:Mar:1980");
		Assert.assertEquals(userDetails.getEmail(),email);
		Assert.assertTrue(userDetails.isMaleSelected());
		Assert.assertFalse(userDetails.isFemaleSelected());
		
		String newEmail = "frank-" + DriverUtil.getUniqueUser("email") + "@we7.com";
		userDetails.setEmail(newEmail);
		userDetails.setNewPassword("password2");
		userDetails.setConfirmNewPassword("password2");
		userDetails.setDOB("10", "Jan", "1970");
		userDetails.selectFemale();
		
		SuccessVeil veil = userDetails.saveChanges().confirmPassword("password");
		Assert.assertTrue(veil.getText().contains("Success"));
		veil.close();
		
		application.getHeader().signOut();
		application.getHeader().getSignInPage().signIn(userName, "password2");
		
		Details newUserDetails = application.getHeader().getAccountsPage().getDetailsTab();
		Assert.assertEquals(newUserDetails.getUserName(),userName);
		Assert.assertEquals(newUserDetails.getDOB(), "10:Jan:1970");
		Assert.assertEquals(newUserDetails.getEmail(),newEmail);
		Assert.assertTrue(newUserDetails.isFemaleSelected());
		Assert.assertFalse(newUserDetails.isMaleSelected());
		
		application.getHeader().signOut();
			
	}
		
	@DataProvider(name = "Save details validation provider")
	public Object[][] saveDetailsValidationProvider() {
		
		return new Object[][] {
				
				{
										"Test: Passwords don't match",
										
				/*PASSWORD*/ 			new String[]{"password" ,"The password and confirm password entries do not match"},
				/*CONFIRM PASSWORD*/	new String[]{"NOMATCH" ,""},
				/*Gender*/				new Object[]{null,""},
				/*Email*/				new String[]{null ,""},						
				/*DOB*/ 				new String[]{null, ""},
					
				},
				
				{
										"Test: Passwords are too small",
									
				/*PASSWORD*/ 			new String[]{"p" ,"Enter a password of at least 4 characters."},
				/*CONFIRM PASSWORD*/	new String[]{null ,""},
				/*Gender*/				new Object[]{null,""},
				/*Email*/				new String[]{null ,""},						
				/*DOB*/ 				new String[]{null, ""},

				},
				
				{
										"Test: Email is blank",
				
				/*PASSWORD*/ 			new String[]{null ,""},
				/*CONFIRM PASSWORD*/	new String[]{null ,""},
				/*Gender*/				new Object[]{null,""},
				/*Email*/				new String[]{"" ,"Enter a valid email address."},						
				/*DOB*/ 				new String[]{null, ""},
				
				},
				
				{
										"Test: Email is not valid",

				/*PASSWORD*/ 			new String[]{null ,""},
				/*CONFIRM PASSWORD*/	new String[]{null ,""},
				/*Gender*/				new Object[]{null,""},
				/*Email*/				new String[]{"NOTVALID" ,"Enter a valid email address."},						
				/*DOB*/ 				new String[]{null, ""},

				},
	
		};
	}	
	
	@Test(description = "User details validation test", dataProvider="saveDetailsValidationProvider", enabled=true)
		public void changeUserSettingsValidation(
				
			ITestContext tc,
			String description,
			String[] password,
			String[] confirmPassword,
			Object[] gender,
			String[] email,
			String[] dob) throws Exception {
		
			final int input = 0, msg = 1;
								
			Application application = new Application(driver);
						
			application.getHeader().getSignInPage().signIn("testing-f", "we7rocks");
			
			Details userDetails = application.getHeader().getAccountsPage().getDetailsTab();
			
			Gender userGender = null;
			if(gender[input]!=null){userGender = (Gender) gender[input];}
					
			userDetails.enterDetails( password[input],
									  confirmPassword[input],
									  email[input],
									  userGender,
									  dob[input]
			);
			
			userDetails.saveChanges();
						
			if (password[msg] !=""){
				Assert.assertTrue(userDetails.getPageText().contains(password[msg]));
			}
			
			if (confirmPassword[msg] !=""){
				Assert.assertTrue(userDetails.getPageText().contains(confirmPassword[msg]));
			}
			
			if (gender[msg]!=""){
				Assert.assertTrue(userDetails.getPageText().contains(password[msg]));
			}
			if (dob[msg]!=""){
				Assert.assertTrue(userDetails.getPageText().contains(dob[msg]));
			}
	}
}