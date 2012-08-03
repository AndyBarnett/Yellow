package testng;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import utility.DBConnection;
import utility.DriverUtil;
import we7int.Application;
import we7int.Contact;
import we7int.ForgottenPasswordSuccess;
import we7int.Join;
import we7int.Welcome;

public class Regression extends MethodConfiguration{
	
	@Test(description = "Test the contact form", enabled=false)
	public void sendCustomerEnquiry(ITestContext tc) throws MessagingException, InterruptedException {
		
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
		
		Contact contact = application.getHeader().getAccountsPage().getContactTab();
		Assert.assertEquals(contact.getName(),userName);
		Assert.assertEquals(contact.getEmail(),email);
		
		String customerQuery = "This test message should be present...";
		contact.setEnquiry(customerQuery);
		contact.send();
						
		SearchTerm term = new SubjectTerm("User query (international)");
		SearchTerm newerThen = new ReceivedDateTerm(ComparisonTerm.GT, new Date());
		term = new AndTerm(term, newerThen);
		BodyTerm bodyTerm = new BodyTerm(customerQuery);
		term = new AndTerm(term, bodyTerm);
		FromStringTerm fromStringTerm = new FromStringTerm(email);
		term = new AndTerm(term, fromStringTerm);
						
		Assert.assertEquals(mailClient.waitForMailCount("Inbox", term, 3, 15000), 1);
		
		SearchTerm thankYouTerm = new SubjectTerm("Thanks for your we7 query");
		thankYouTerm = new AndTerm(thankYouTerm, newerThen);
		BodyTerm thankYouBodyTerm = new BodyTerm(customerQuery);
		thankYouTerm = new AndTerm(thankYouTerm, thankYouBodyTerm);
		RecipientStringTerm recipientTerm = new RecipientStringTerm(RecipientType.TO, email);
		thankYouTerm = new AndTerm(thankYouTerm, recipientTerm);
		
		Assert.assertEquals(mailClient.waitForMailCount("Inbox", thankYouTerm, 3, 15000), 1);
	}
	
	@Test(description = "Test change password", enabled=true)
	public void forgotPassword(ITestContext tc) 
			throws MessagingException, InterruptedException, ClassNotFoundException, SQLException, IOException {
		
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
		
		application.getHeader().signOut();
		application.activateUserDatabase(DBConnection.getConnectionFromXML(tc), email);
		
		ForgottenPasswordSuccess forgottenPasswordSuccessPage  = 
			application.getHeader().getSignInPage().getForgottenPasswordPage().sendReminder(email);
		
		Assert.assertTrue(forgottenPasswordSuccessPage.getPageText().contains("PASSWORD REMINDER SENT"));
												
		SearchTerm term = new SubjectTerm("New password for we7");
		SearchTerm newerThen = new ReceivedDateTerm(ComparisonTerm.GT, new Date());
		term = new AndTerm(term, newerThen);
		RecipientStringTerm recipientTerm = new RecipientStringTerm(RecipientType.TO, email);
		term = new AndTerm(term, recipientTerm);
		
		Message [] mail = mailClient.waitForMail("Inbox", term, 3, 20000);
		Assert.assertEquals(mail.length, 1); //found mail
		String newPassword = DriverUtil.getPasswordFromMail(mail[0]);
		Assert.assertNotEquals(newPassword, ""); //password found
		
		forgottenPasswordSuccessPage.getSignInPage().signIn(userName, newPassword);
		
		Assert.assertEquals(application.getHeader().getWelcomeName(), userName);
	}
}
