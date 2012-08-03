package testng;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;


import we7int.Application;

public class FooterTest extends MethodConfiguration{
	
	String privacyPolicy = "Welcome to we7, the next generation music service where users can get";
	String terms = "Welcome to the we7 website the next generation music download site where users can access";

	//About
	@Test(description = "Check Track is still playing after 'About' click", enabled=false, retryAnalyzer = MethodConfiguration.class)
	public void stillPlayingAbout() throws InterruptedException {
		Application application = new Application(driver);
		
		application.openPage("http://www.development.we7.com/listen/i/l125120273/testing-f/radio");
		application.getFooter().getAbout();
		Assert.assertEquals((application.getChugger().isPlaying(10000, 2)), true);
	}
	
	@Test(description = "Check About Container", enabled=false, retryAnalyzer = MethodConfiguration.class)
	public void aboutContainer() {
		Application application = new Application(driver);
		application.getFooter().getAbout();
		
		WebElement ele = driver.findElement(By.className("main-content"));
		Assert.assertEquals(ele.getAttribute("className"),"main-content");
	}
	
	
	
	//Mobile
	@Test(description = "Check Track is still playing after 'Mobile' click", enabled=false, retryAnalyzer = MethodConfiguration.class)
	public void stillPlayingMobile() throws InterruptedException {
		Application application = new Application(driver);
		
		application.openPage("http://www.development.we7.com/listen/i/l125120273/testing-f/radio");
		application.getFooter().getMobile();
		Assert.assertEquals((application.getChugger().isPlaying(10000, 2)), true);
	}
	
	@Test(description = "Check Mobile Container", enabled=false, retryAnalyzer = MethodConfiguration.class)
	public void mobileContainer() {
		Application application = new Application(driver);
		application.getFooter().getMobile();
		
		WebElement ele = driver.findElement(By.className("features-container"));
		Assert.assertEquals(ele.getAttribute("className"),"features-container");
	}
	
	@Test(description = "Check Mobile App Button", enabled=false, retryAnalyzer = MethodConfiguration.class)
	public void appTest() {
		Application application = new Application(driver);
		application.getFooter().getSubscribe();

		//Click the Preview button
		application.getFooter().selectPreview();
		Assert.assertTrue(driver.getCurrentUrl().contains("http://subscription.development.we7.com/"));
	}
	
	
	
	
	//Subscribe	
	@Test(description = "Check Track is still playing after 'Subscribe' click", enabled=false, retryAnalyzer = MethodConfiguration.class)
	public void stillPlayingSubscribe() throws InterruptedException {
		Application application = new Application(driver);
		
		application.openPage("http://www.development.we7.com/listen/i/l125120273/testing-f/radio");
		application.getFooter().getSubscribe();
		Assert.assertEquals((application.getChugger().isPlaying(10000, 2)), true);
		
	}
	
	@Test(description = "Check Features Container", enabled=false, retryAnalyzer = MethodConfiguration.class)
	public void featuresContainer() {
		Application application = new Application(driver);
		application.getFooter().getSubscribe();
		
		WebElement ele = driver.findElement(By.className("features-container"));
		Assert.assertEquals(ele.getAttribute("className"),"features-container");
	}
	
	
	@Test(description = "Check Preview/Subscribe Button", enabled=false, retryAnalyzer = MethodConfiguration.class)
	public void subscribeTest() {
		Application application = new Application(driver);
		application.getFooter().getSubscribe();

		//Click the Preview button
		application.getFooter().selectPreview();
		Assert.assertTrue(driver.getCurrentUrl().contains("http://subscription.development.we7.com/"));
	}
	
	@Test(description = "Check Buy Button", enabled=false, retryAnalyzer = MethodConfiguration.class)
	public void buyTest() {
		Application application = new Application(driver);
		
		//Click the Buy button
		application.getFooter().getSubscribe();
		application.getFooter().selectBuy();
		//Two .contains() asserts because sometimes the url has a '#' before '/join'
		Assert.assertTrue(driver.getCurrentUrl().contains("http://subscription.development.we7.com/"));
		Assert.assertTrue(driver.getCurrentUrl().contains("/join"));
	}
	
	
	
	
	
	//Legal
	@Test(description = "Check Legal Veil", enabled=true, retryAnalyzer = MethodConfiguration.class)
	public void legalTest() throws InterruptedException {
		Application application = new Application(driver);
		application.openPage("http://www.development.we7.com/listen/i/l125120273/testing-f/radio");
		application.getFooter().getLegal();
		application.getLegal().selectPrivacyPolicy();
		Assert.assertTrue(application.getLegal().getPrivacyText().contains(privacyPolicy));

		application.getLegal().selectTerms();
		Assert.assertTrue(application.getLegal().getTermsText().contains(terms));

		application.getLegal().close();

	}
	
}