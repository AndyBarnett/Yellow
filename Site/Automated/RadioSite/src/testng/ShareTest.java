package testng;

import java.io.IOException;
import java.util.Date;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import utility.DriverUtil;
import we7int.Application;
import we7int.Share;

public class ShareTest extends MethodConfiguration{
	
	@Test(description = "Get the Share veil up", enabled=true, retryAnalyzer = MethodConfiguration.class)
	public void shareTest(ITestContext tc) throws ClassNotFoundException, InterruptedException, MessagingException, IOException {
		Application application = new Application(driver);

		String messageText = ("This is the message");
		String toEmail = (DriverUtil.getUniqueUser("andrewb")+"@we7.com");
		String messageSuccess = "Message sent successfully";
		String urlContains = "www.development.we7.com/listen";

		//Sign in and get share veil
		application.getHeader().getSignInPage().signIn("testing-f", "we7rocks");
		application.openPage("http://www.development.we7.com/listen/i/l125120273/testing-f/radio");
		Share shareVeil = application.getChugger().getShareVeil();

		//Link Tab
				System.out.println("Looking for \"Link\"");
					shareVeil.getTab("Link");
					Assert.assertTrue(shareVeil.getPageText().contains(
						"Copy and paste this link into an email or instant message"), "Link page doesn't contain the correct text");				
				String linkBoxURL = shareVeil.getLinkBox();
				Assert.assertTrue(linkBoxURL.contains(urlContains));				
				shareVeil.getCloseButton();				
				driver.get(linkBoxURL);				
				Assert.assertTrue((application.getChugger().isPlaying(30000, 2)));
		
				
		//Social Tab
				Share shareVeil2 = application.getChugger().getShareVeil();
				System.out.println("Looking for \"Social\"");
				shareVeil2.getTab("Social");
				
				shareVeil2.clickFacebook();
				Assert.assertTrue(Share.windowOpenedFromLink(driver, "Facebook"));
				
				shareVeil2.clickTwitter();
				Assert.assertTrue(Share.windowOpenedFromLink(driver, "Twitter"));
				
				Assert.assertTrue(shareVeil2.clickIcons(25));
				//Assert.assertTrue(shareVeil2.assertIcons(driver, 25));
				Thread.sleep(5000);
		
				
		//Email Tab
				System.out.println("Looking for \"Email\"");
				shareVeil2.getTab("Email");
				
				shareVeil2.setEmailTo(toEmail);
				shareVeil2.setMessage(messageText);
				shareVeil2.clickSendButton();
				
				
				//Assert.assertTrue(shareVeil.getPageText().contains(messageSuccess));
				
				
				SearchTerm term = new SubjectTerm("Radio - we7.com");
				SearchTerm newerThen = new ReceivedDateTerm(ComparisonTerm.GT, new Date());
				term = new AndTerm(term, newerThen);
				BodyTerm bodyTerm = new BodyTerm(messageText);
				term = new AndTerm(term, bodyTerm);
				RecipientStringTerm recipientTerm = new RecipientStringTerm(RecipientType.TO, toEmail);;
				term = new AndTerm(term, recipientTerm);
								
				Assert.assertEquals(mailClient.waitForMailCount("Testing junk", term, 3, 15000), 1);



		shareVeil2.getCloseButton();
			
	}
}