package testng;

import java.util.Date;
import java.util.HashMap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.RecipientStringTerm;

import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import utility.DBConnection;
import utility.DriverUtil;
import utility.FaceBook;
import utility.RegisterUser;

import we7int.Activate;
import we7int.Application;
import we7int.Details;
import we7int.ForgottenPasswordSuccess;
import we7int.Header;
import we7int.Join;
import we7int.Join.Gender;
import we7int.LinkAccount;
import we7int.SignIn;
import we7int.DatabaseUser;
import we7int.Welcome;


public class JoinTest extends MethodConfiguration {
	
	@Test(description = "Create a new test user", retryAnalyzer = MethodConfiguration.class, enabled=false)
	public void createNewUser(ITestContext tc) throws MessagingException, InterruptedException {
		
		String userName = DriverUtil.getUniqueUserFromXML(tc);		
		String email = userName + "@we7.com";
		
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
		
		Details userDetails = application.getHeader().getAccountsPage().getDetailsTab();
		
		Assert.assertEquals(userDetails.getUserName(),userName);
		Assert.assertEquals(userDetails.getDOB(), "12:Mar:1980");
		Assert.assertEquals(userDetails.getEmail(),email);
		Assert.assertTrue(userDetails.isMaleSelected());
		Assert.assertFalse(userDetails.isFemaleSelected());
		
		SearchTerm term = new SubjectTerm("Email verification for we7 - Your Personal DJ");
		SearchTerm newerThen = new ReceivedDateTerm(ComparisonTerm.GT, new Date());
		term = new AndTerm(term, newerThen);
		BodyTerm bodyTerm = new BodyTerm(userName); //unique
		term = new AndTerm(term, bodyTerm);
		
		Assert.assertEquals(mailClient.waitForMailCount("Inbox", term, 3, 15000), 1);
	}
	
	@Test(description = "Attempt login with user that has not been activated",
										retryAnalyzer = MethodConfiguration.class, enabled=false)
	public void loginNoActivation(ITestContext tc) throws Exception {
		
		HashMap<String, String> user = RegisterUser.getAutoUser(tc, false);
		
		Application application = new Application(driver);
		
		Activate activationPage = application.getHeader().getSignInPage()
				.signInNoActivation(user.get("username"), user.get("password"));
		
		Assert.assertTrue(activationPage.getText().contains("Account not activated"));
	}
	
	@Test(description = "Activate a new user" , retryAnalyzer = MethodConfiguration.class, enabled=false)  
	public void activateUser(ITestContext tc) throws Exception {
		
		HashMap<String, String> user = RegisterUser.getAutoUser(tc, false);
		String userName = user.get(RegisterUser.USERNAME);
		String password = user.get(RegisterUser.PASSWORD);
			
		Application application = new Application(driver);
		application.activateUser(DBConnection.getConnectionFromXML(tc), user.get(RegisterUser.EMAIL), url);
		
		Header header = application.getHeader();
		Assert.assertEquals(header.getWelcomeName(), DriverUtil.formatDisplayName(userName));
		header.signOut();
			
		header.getSignInPage().signIn(userName, password);
		Assert.assertEquals(header.getWelcomeName(), DriverUtil.formatDisplayName(userName));
		header.signOut(); //sign out verified if we can see the sign in button
		header.getSignInPage().signIn(userName, "password"); 
		Assert.assertEquals(header.getWelcomeName(), DriverUtil.formatDisplayName(userName));
		header.signOut();
		
		SearchTerm term = new SubjectTerm("Account details for we7 - Your Personal DJ");
		SearchTerm newerThen = new ReceivedDateTerm(ComparisonTerm.GT, new Date());
		term = new AndTerm(term, newerThen);
		BodyTerm bodyTerm = new BodyTerm(userName); //unique
		term = new AndTerm(term, bodyTerm);
				
		Assert.assertEquals(mailClient.waitForMailCount("Inbox", term, 3, 15000), 1);
	}
	
		
	
	@Test(description = "Join with facebook", retryAnalyzer = MethodConfiguration.class,  enabled=false)
	public void joinWithFacebook(ITestContext tc) throws Exception {
		
		Application application = new Application(driver);
				
		//Create a facebook user
		
		String accessToken = FaceBook.getAccesstoken();
		JSONObject facebookUser = FaceBook.createUser(accessToken);
		Assert.assertTrue(FaceBook.changePassword(facebookUser.get("id").toString(), "passwe7", accessToken));
		String facebookEmail = facebookUser.get("email").toString();
				
		//Join with facebook
		
		application.getHeader().getSocialSignIn().joinWithFaceBook(facebookEmail, "passwe7");
		HashMap<String, Object> user = application.getUser(
						DBConnection.getConnectionFromXML(tc), facebookEmail, 3, 10000);
		String userName = (String) user.get(DatabaseUser.USER_NAME);
		
		Assert.assertEquals(application.getHeader().getWelcomeName(),DriverUtil.formatDisplayName(userName));
		
		Details userDetails = application.getHeader().getAccountsPage().getDetailsTab();
		Assert.assertEquals(userDetails.getEmail(),facebookEmail);
		
		application.getHeader().signOut();
		
		//test we can sign in again with new user
		application.activateUserDatabase(DBConnection.getConnectionFromXML(tc), facebookEmail);
		
		application.getHeader().getSocialSignIn().signInWithFaceBook();
		Assert.assertEquals(application.getHeader().getWelcomeName(),DriverUtil.formatDisplayName(userName));
		
		application.getHeader().signOut();
		
		if(FaceBook.deleteUser(facebookUser.get("id").toString(), accessToken)==false){
			Reporter.log("Failed to delete facebook user");
		}
	}
	
	
	@Test(description = "Link a facebook account to a standard account", 
										retryAnalyzer = MethodConfiguration.class,  enabled=false)
	public void linkAccountsFacebook(ITestContext tc) throws Exception {
		
		//create a facebook user
		
		String accessToken = FaceBook.getAccesstoken();
		JSONObject facebookUser = FaceBook.createUser(accessToken);
		Assert.assertTrue(FaceBook.changePassword(
				facebookUser.get("id").toString(), "passwe7", accessToken));
		Reporter.log("Facebook user created");
				 
		//get a unique username
		
		String userName = DriverUtil.getUniqueUserFromXML(tc);		
				
		//create a standard user with email address of facebook user
		
		String facebookEmail  = facebookUser.get("email").toString();
		
		Application application = new Application(driver);
		Join joinPage = application.getHeader().getJoinPage();
		joinPage.enterUserName(userName);
		joinPage.enterPassword("passwe7","passwe7");
		joinPage.enterEmail(facebookEmail);
		joinPage.selectMale();
		joinPage.checkAgree(true);
		joinPage.setDOB("12", "Mar", "1980");
		
		Welcome welcomePage = joinPage.createAccount();
		Assert.assertTrue(welcomePage.getPageText().contains("WELCOME"));
		welcomePage.startListining();
		Assert.assertEquals(application.getHeader().getWelcomeName(), DriverUtil.formatDisplayName(userName));
		application.getHeader().signOut();
		
		//Activate the user
			
		application.activateUserDatabase(DBConnection.getConnectionFromXML(tc), facebookEmail);
		
		//Attempt to join with Social Facebook that has existing email in database
		
		application.getHeader().getSocialSignIn().joinWithFaceBook(facebookEmail, "passwe7");
		
		Assert.assertEquals(application.getHeader().getWelcomeName(), DriverUtil.formatDisplayName(userName));
		
		application.getHeader().signOut();
		
		//sign in with the facebook account again and verify that we do not go to link account
		
		application.getHeader().getSocialSignIn().signInWithFaceBook();
				
		Assert.assertEquals(application.getHeader().getWelcomeName(), userName);
		application.getHeader().signOut();
		
		if(FaceBook.deleteUser(facebookUser.get("id").toString(), accessToken)==false){
			Reporter.log("Failed to delete facebook user");
		}
	}
	
	
	@DataProvider
	public Object[][] userTypes() {
		return new Object[][]{
				{ "testing-f", "we7rocks" , "free" },
				{ "testing-p", "we7rocks" , "premium"},
				{ "testing-pp", "we7rocks", "premium plus" }
		};
	}
	
	@Test(description = "Login with all user types", dataProvider = "userTypes", enabled=false)
	public void logInAllUserTypes(String userName, String pass, String description) {
		
		Application application = new Application(driver);
		application.getHeader().getSignInPage().signIn(userName, pass);
		
		Assert.assertEquals(application.getHeader().getWelcomeName(), userName);
		application.getHeader().signOut(); 
		
	}
	
	@DataProvider
	public Object[][] createNewUserValidationProvider() {
		
		return new Object[][] {
				
				{
										"Test: Passwords do not match",	
					
				/*USER*/ 				new String[]{DriverUtil.getUniqueUser("user"),""},
				/*PASSWORD*/ 			new String[]{"password","The password and confirm password entries do not match"},
				/*CONFIRM PASSWORD*/	new String[]{"NOMATCH",""},
				/*EMAIL*/ 				new String[]{DriverUtil.getUniqueUser("email")+"@we7.com",""},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:1980", ""},
				/*T&C*/ 				new Object[]{true,""}
				
				},
				
				{
					
										"Test: Password is too small",	
					
				/*USER*/ 				new String[]{DriverUtil.getUniqueUser("user"),""},
				/*PASSWORD*/ 			new String[]{"p","Enter a password of at least 4 characters."},
				/*CONFIRM PASSWORD*/	new String[]{"P",""},
				/*EMAIL*/ 				new String[]{DriverUtil.getUniqueUser("email")+"@we7.com",""},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:1980", ""},
				/*T&C*/ 				new Object[]{true,""}
				
				},
				
				{
					
										"Test: Invalid email address",	
				
				/*USER*/ 				new String[]{DriverUtil.getUniqueUser("user"),""},
				/*PASSWORD*/ 			new String[]{"password",""},
				/*CONFIRM PASSWORD*/	new String[]{"password",""},
				/*EMAIL*/ 				new String[]{"INVALIDEMAIL","Enter a valid email address."},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:1980", ""},
				/*T&C*/ 				new Object[]{true,""}
					
				},
				
				{
					
										"Test: All blank",	
				
				/*USER*/ 				new String[]{null,"Enter a valid username (6-30 characters)."},
				/*PASSWORD*/ 			new String[]{null,"Enter a password of at least 4 characters."},
				/*CONFIRM PASSWORD*/	new String[]{null,"The password and confirm password entries do not match"},
				/*EMAIL*/ 				new String[]{null,"Enter a valid email address."},
				/*GENDER*/ 				new Object[]{null,"Select your gender"},
				/*DOB*/ 				new String[]{null, "Select your date of birth."},
				/*T&C*/ 				new Object[]{null,"Tick the box to indicate your agreement."}
					
				},
				
				{
					
										"Test: User too young",	
				
				/*USER*/ 				new String[]{DriverUtil.getUniqueUser("user"),""},
				/*PASSWORD*/ 			new String[]{"password",""},
				/*CONFIRM PASSWORD*/	new String[]{"password",""},
				/*EMAIL*/ 				new String[]{DriverUtil.getUniqueUser("email")+"@we7.com",""},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:2010", "You must be 13 or over."},
				/*T&C*/ 				new Object[]{true,""}
					
				},
				
				{
					
										"Test: Username less than 6 chars",	
				
				/*USER*/ 				new String[]{"aa","Enter a valid username (6-30 characters)."},
				/*PASSWORD*/ 			new String[]{"password",""},
				/*CONFIRM PASSWORD*/	new String[]{"Password",""},
				/*EMAIL*/ 				new String[]{DriverUtil.getUniqueUser("email")+"@we7.com",""},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:1980", ""},
				/*T&C*/ 				new Object[]{true,""}
					
				},
				
				{
					
										"Test: User already exists",	
				
				/*USER*/ 				new String[]{"testing-f","Username already in use"},
				/*PASSWORD*/ 			new String[]{"password",""},
				/*CONFIRM PASSWORD*/	new String[]{"password",""},
				/*EMAIL*/ 				new String[]{DriverUtil.getUniqueUser("email")+"@we7.com",""},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:1980", ""},
				/*T&C*/ 				new Object[]{true,""}
					
				},
				
				{
				
										"Test: Email already in use",
										
				/*USER*/ 				new String[]{DriverUtil.getUniqueUser("user"),""},
				/*PASSWORD*/ 			new String[]{"password",""},
				/*CONFIRM PASSWORD*/	new String[]{"password",""},
				/*EMAIL*/ 				new String[]{"frank@we7.com","Email address is already in use."},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:1980", ""},
				/*T&C*/ 				new Object[]{true,""}
					
				}
		 };
	}
			
	@Test(description = "Test create new user validation", dataProvider = "createNewUserValidationProvider", enabled=false)
	public void createNewUserInvalid(
			
			String description,
			String[] user,
			String[] password,
			String[] confirmPassword,
			String[] email,
			Object[] gender,
			String[] dob,
			Object[] legal)
	{
		final int input = 0, msg = 1;
		
		Application application = new Application(driver);
		Join joinPage = application.getHeader().getJoinPage();
			
		Gender userGender = null;
		Boolean checkLegal = null;
		
		if(gender[input]!=null){userGender = (Gender) gender[input];}
		if(legal[input]!=null){checkLegal = (Boolean) legal[input];}
						
		joinPage.enterDetails(
				user[input],
				password[input],
				confirmPassword[input],
				email[input],
				userGender,
				dob[input],
				checkLegal
		);
		
		joinPage.createAccount();
		
		String pageText = joinPage.getPageText();
		
		if (user[msg] !=""){
			Assert.assertTrue(pageText.contains(user[msg]), formatErr(user[msg]));
		}
		if (password[msg]!=""){
			Assert.assertTrue(pageText.contains(password[msg]), formatErr(password[msg]));
		}
		if (confirmPassword[msg]!=""){
			Assert.assertTrue(pageText.contains(confirmPassword[msg]), formatErr(confirmPassword[msg]));
		}
		if (email[msg]!=""){
			Assert.assertTrue(pageText.contains(email[msg]), formatErr(email[msg]));
		}
		if (gender[msg]!=""){
			Assert.assertTrue(pageText.contains((String) gender[msg]),
					formatErr((String)gender[msg]));
		}
		if (dob[msg]!=""){
			Assert.assertTrue(pageText.contains(dob[msg]),formatErr(dob[msg]));
		}
		if (legal[msg]!=""){
			Assert.assertTrue(pageText.contains((String)legal[msg]),
					formatErr((String)legal[msg]));
		}
	}
	
	private String formatErr(String err){
		return "Validation message failed to display: " + err ;
		
	}
	
	@Test(description = "Test change password", retryAnalyzer = MethodConfiguration.class,  enabled=false)
	public void forgotPassword(ITestContext tc) throws Exception {
		
		HashMap<String, String> user = RegisterUser.getAutoUser(tc, true);
		String userName = user.get(RegisterUser.USERNAME);
		String email = user.get(RegisterUser.EMAIL);
				
		Application application = new Application(driver);
		
		ForgottenPasswordSuccess forgottenPasswordSuccessPage  = 
			application.getHeader().getSignInPage().getForgottenPasswordPage().sendReminder(email);
		
		Assert.assertTrue(forgottenPasswordSuccessPage.getPageText().contains("PASSWORD REMINDER SENT"));
												
		SearchTerm term = new SubjectTerm("New password for we7");
		SearchTerm newerThen = new ReceivedDateTerm(ComparisonTerm.GT, new Date());
		term = new AndTerm(term, newerThen);
		RecipientStringTerm recipientTerm = new RecipientStringTerm(RecipientType.TO, email);
		term = new AndTerm(term, recipientTerm);
		
		Message [] mail = mailClient.waitForMail("Inbox", term, 6, 20000);
		Assert.assertEquals(mail.length, 1, "Mail was not found"); //found mail
		
		String newPassword = DriverUtil.getPasswordFromMail(mail[0]);
		Assert.assertNotEquals(newPassword, "", "Password not found in mail"); //password found
		
		forgottenPasswordSuccessPage.getSignInPage().signIn(userName, newPassword);
		
		Assert.assertEquals(application.getHeader().getWelcomeName(), DriverUtil.formatDisplayName(userName));
	}
	
		
	@DataProvider
	public Object[][] signInValidationProvider() {
		return new Object[][]{
				{ "", "", "Enter a valid username (6-30 characters).", "Incorrect password entered."},
				{ "", "INVALID", "Enter a valid username (6-30 characters).", ""},
				{ "INVALID", "INVALID", "Incorrect username/password", "Incorrect username/password"},
				{ "testing-f", "INVALID", "Incorrect username/password", "Incorrect username/password"},
				{ "testing-p", "INVALID", "Incorrect username/password", "Incorrect username/password"},
				{ "testing-pp", "INVALID", "Incorrect username/password", "Incorrect username/password"},
				{ "testing-f", "1234567890123456789012345678901", "Incorrect username/password", "Incorrect username/password"},
				{ "testing-p", "1", "Incorrect username/password", "Incorrect username/password"},
				{ "testing-pp", "!£$%^&*()_+", "Incorrect username/password", "Incorrect username/password"},
				{ "' OR 1=1--", "ANYTHING", "Incorrect username/password", "Incorrect username/password"},
				{ "\"", "ANYTHING", "Incorrect username/password", "Incorrect username/password"},
				{ "testing-pp", "\"", "Incorrect username/password", "Incorrect username/password"},
		};
	}
	
	
		
	@Test(description = "Login with invalid crendentials", 
					dataProvider = "signInValidationProvider", enabled=false)
	public void logInValidation(String userName, String pass, String msg1, String msg2) {
		
		SignIn signInPage = new Application(driver).getHeader().getSignInPage();
		signInPage.signIn(userName, pass);
									
		if(msg1 !=""){ Assert.assertTrue(signInPage.getPageText().contains(msg1));}
				
		if(msg2 !=""){Assert.assertTrue(signInPage.getPageText().contains(msg2));}
		
	}
	
	@Test(description = "Join with twitter", retryAnalyzer = MethodConfiguration.class, enabled=true)
	public void joinWithTwitter(ITestContext tc) throws Exception {
		
		String email = tc.getCurrentXmlTest().getParameter("twitter.email.name");
		String password = tc.getCurrentXmlTest().getParameter("twitter.email.password");
						
		Application application = new Application(driver);
		Header header = application.getHeader(); 
		application.deleteUser(DBConnection.getConnectionFromXML(tc), email);  /*attempt to delete the user*/
		
		Join joinPage = header.getSocialSignIn().joinWithTwitter(email, password);
		
		joinPage.enterEmail(email);
		joinPage.selectMale();
		joinPage.setDOB("12", "Mar", "1980");
		
		Welcome welcomePage = joinPage.createAccount();
		Assert.assertTrue(welcomePage.getPageText().contains("WELCOME"));
		welcomePage.startListining();
		
		HashMap<String, Object> user = application.getUser(DBConnection.getConnectionFromXML(tc), email, 3, 20000);
		String userName = (String) user.get(DatabaseUser.USER_NAME);
				
		Assert.assertEquals(header.getWelcomeName(), DriverUtil.formatDisplayName(userName));
		application.getHeader().signOut();
		
		application.activateUserDatabase(DBConnection.getConnectionFromXML(tc), email);
		header.getSocialSignIn().signInWithTwitter();
		
		Assert.assertEquals(header.getWelcomeName(), DriverUtil.formatDisplayName(userName));
	}
	
	@Test(description = "Link accounts Twitter", retryAnalyzer = MethodConfiguration.class, enabled=false)
	public void joinWithTwitterLinkAccounts(ITestContext tc) throws Exception {
		
		HashMap<String, String> user = RegisterUser.getAutoUser(tc, true);
		String standardUserName = user.get(RegisterUser.USERNAME);
		
		String twitterEmail = tc.getCurrentXmlTest().getParameter("twitter.email.name");
		String twitterPassword = tc.getCurrentXmlTest().getParameter("twitter.email.password");
						
		Application application = new Application(driver);
		application.deleteUser(DBConnection.getConnectionFromXML(tc), twitterEmail);  /*attempt to delete the user*/
		
		/*Link standard account and social account*/
		
		Header header = application.getHeader();
		Join socialJoinPage = header.getSocialSignIn().joinWithTwitter(twitterEmail, twitterPassword);
		
		socialJoinPage.enterEmail(twitterEmail);
		socialJoinPage.selectMale();
		socialJoinPage.setDOB("12", "Mar", "1980");
		
		LinkAccount linkAccountPage = socialJoinPage.createAccountWithExistingEmail();
		
		Assert.assertTrue(linkAccountPage.getPageText().contains("sign in and link"));
		linkAccountPage.enterPassword("passwe7").signIn();
		
		Assert.assertEquals(header.getWelcomeName(), DriverUtil.formatDisplayName(standardUserName));
		application.getHeader().signOut();
					
		header.getSocialSignIn().signInWithTwitter();
		
		Assert.assertEquals(header.getWelcomeName(), DriverUtil.formatDisplayName(standardUserName));
		application.getHeader().signOut();
		
	}
	
	@DataProvider(name = "Social join user validation")
	public Object[][] createNewSocialUserValidationProvider() {
		
		return new Object[][] {
				
				{
										"Test: Blank email",
										
				/*EMAIL*/ 				new String[]{null,"Enter a valid email address."},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:1980", ""},
				/*T&C*/ 				new Object[]{true,""}
				
				},
				
				{
										"Test: Invalid email",
									
				/*EMAIL*/ 				new String[]{"INVALIDEMAIL","Enter a valid email address."},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:1980", ""},
				/*T&C*/ 				new Object[]{true,""}
				
				},
				
				{
										"Test: Invalid email - SQL",
								
				/*EMAIL*/ 				new String[]{"' or 1=1","Enter a valid email address."},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:1980", ""},
				/*T&C*/ 				new Object[]{true,""}
				
				},

				{						"Test: Min Age",
					
				/*EMAIL*/ 				new String[]{"frank-tw@we7.com",""},
				/*GENDER*/ 				new Object[]{Gender.MALE,""},
				/*DOB*/ 				new String[]{"1:Jan:2010", "You must be 13 or over."},
				/*T&C*/ 				new Object[]{true,""}
				
				},
				
				{						"Test: All blank",	
				
				/*EMAIL*/ 				new String[]{null,"Enter a valid email address."},
				/*GENDER*/ 				new String[]{null,"Select your gender"},
				/*DOB*/ 				new String[]{"Day:Month:Year", "Select your date of birth."},
				/*T&C*/ 				new Object[]{false,"Tick the box to indicate your agreement."}
					
				},
				
		};
	}	
		
	
	@Test(description = "Join with provider validation", 
					dataProvider="createNewSocialUserValidationProvider", enabled=false)
	public void joinWithTwitterInvalid(
			ITestContext tc,
			String description,
			String[] email,
			Object[] gender,
			String[] dob,
			Object[] legal) throws Exception {
		
			final int input = 0, msg = 1;
			
			String twitterEmail = tc.getCurrentXmlTest().getParameter("twitter.email.name");
			String twitterPassword = tc.getCurrentXmlTest().getParameter("twitter.email.password");
						
			Application application = new Application(driver);
			application.deleteUser(DBConnection.getConnectionFromXML(tc), twitterEmail);  /*attempt to delete the user*/
			
			Join joinPage = application.getHeader().getSocialSignIn()
								.joinWithTwitter(twitterEmail, twitterPassword);
			
			Gender userGender = null;
			Boolean checkLegal = null;
			
			if(gender[input]!=null){userGender = (Gender) gender[input];}
			if(legal[input]!=null){checkLegal = (Boolean) legal[input];}
							
			joinPage.enterDetails(
					null,
					null,
					null,
					email[input],
					userGender,
					dob[input],
					checkLegal
			);
			
			joinPage.createAccount();
			
			String pageText = joinPage.getPageText();
				
			if (email[msg]!=""){
				Assert.assertTrue(pageText.contains(email[msg]), formatErr(email[msg]));
			}
			if (gender[msg]!=""){
				Assert.assertTrue(pageText.contains((String) gender[msg]),
						formatErr((String)gender[msg]));
			}
			if (dob[msg]!=""){
				Assert.assertTrue(pageText.contains(dob[msg]),formatErr(dob[msg]));
			}
			if (legal[msg]!=""){
				Assert.assertTrue(pageText.contains((String)legal[msg]),
						formatErr((String)legal[msg]));
			}
	}

}
