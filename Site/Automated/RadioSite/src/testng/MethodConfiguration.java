package testng;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import utility.IMAPClient;


public class MethodConfiguration implements IRetryAnalyzer{

        protected WebDriver driver=null;
        private static long implicitWait = 450;
        
        private int count = 0; 
    	private int maxCount = 2;
    	
    	protected IMAPClient mailClient;
    	
    	@Override 
    	public boolean retry(ITestResult result) {
    	    return count++ < maxCount ?  true :  false; 
    	}
        
        @BeforeMethod()
        @Parameters({"capabilities.browser", "capabilities.version", "capabilities.platform",
                                              "url", "hub", "wait.implicit.seconds"})
        public void setUpMethod(ITestContext c, String browser, //firefox, chrome, internet explorer, opera
                              @Optional String version,
                              @Optional String platform,
                              String url,
                              String hub,
                              @Optional("100") String implicitWait) throws MalformedURLException, MessagingException {
        	
        	   count = 0;
        	   
        	   DesiredCapabilities capabilities = new DesiredCapabilities();
               
               capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
               capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); 
        	    
               capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
                                
               if (version!=null) //optional
                   capabilities.setCapability(CapabilityType.VERSION, version);
               if (platform!=null)
                   capabilities.setCapability(CapabilityType.PLATFORM, platform);
               
               driver = new RemoteWebDriver(new URL(hub), capabilities);
               
               MethodConfiguration.implicitWait = Long.parseLong(implicitWait);
              
               driver.manage().timeouts().implicitlyWait(
            		   MethodConfiguration.implicitWait, TimeUnit.SECONDS);
               driver.get(url);
               
               mailClient = new IMAPClient(c);
        }
        
        @AfterMethod(alwaysRun=true)
        public void tearDownMethod() throws MessagingException {
           	if (driver != null){
        		driver.quit();
        		driver = null;
        	}
           	
           	mailClient.close();
        }
        
        public static long getImplicitWaitValue(){
        	return implicitWait;
        }
}