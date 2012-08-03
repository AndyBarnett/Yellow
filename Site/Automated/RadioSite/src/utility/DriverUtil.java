package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.w3c.dom.Document;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.thoughtworks.selenium.Wait;

import testng.MethodConfiguration;

public class DriverUtil {

	public static final String ELLIPSIS = "\u2026"
	
	public static String inputStreamAsString(InputStream stream)throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}

		br.close();
		return sb.toString();
	}
	
	
	public static String formatDisplayName(String name){
		
		if (name.length() > 19){
			return name.substring(0, 19) + ELLIPSIS;
		}
		
		return name;
	}
	
	public static String getPasswordFromMail(Message msg) throws IOException, MessagingException{
		
		BufferedReader msgReader = new BufferedReader ( new InputStreamReader(msg.getInputStream()));
		String line, password = "";
		
		while ((line = msgReader.readLine()) != null)   {
			
			  if (line.contains("Your new password is:")){
			  	password = line.split(":")[1].trim(); 
			  }
		}
		
		System.out.println("password is " + password);
		
		return password;
		
	}
	
	public static void serialize(Document doc, OutputStream out) throws TransformerException 
	{  
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer serializer;
		
			
			serializer = tfactory.newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		
			DOMSource xmlSource = new DOMSource(doc);
			StreamResult outputTarget = new StreamResult(out);
			serializer.transform(xmlSource, outputTarget);
	}

	
	 public static boolean isElementPresent(final WebDriver driver, final By by, int seconds){
		   
		   boolean found = true;
		   
		   driver.manage().timeouts().implicitlyWait(seconds,TimeUnit.SECONDS);
		   
		   try {
			   driver.findElement(by);
		   } catch (NoSuchElementException e) {
			   System.out.println("NoSuchElementException");
			   found = false;
		   } finally {
			   driver.manage().timeouts().implicitlyWait(
				  MethodConfiguration.getImplicitWaitValue(),TimeUnit.SECONDS);
		   }
		   
		   return found;
	 }
	 
	 public static boolean isElementPresent(final WebDriver driver, final By by){
		 return isElementPresent(driver,by,0);
	 }
	 
	
	 public static String getUniqueUser(String userName){
		 return "user" + System.currentTimeMillis() 
					+ "-" + Thread.currentThread().getId();
	 }
	 
	 
	 public static void WaitUntilElementNotPresent(final WebDriver driver, final By by, final String msg){
		   
		   driver.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS);
		   
		   try {
			   new Wait(msg){
				   public boolean until() {
					   try {
						   driver.findElement(by);
					   } catch (NoSuchElementException e) {
						   return true;
					   }
					   return false;
				   }
			   };
		   }
		   finally {
		  	  driver.manage().timeouts().implicitlyWait(
					MethodConfiguration.getImplicitWaitValue(), TimeUnit.SECONDS);
		  }
	 }
	 
	
	 public static void waitforURL(final WebDriver driver, final String url){
		 new Wait("Wait for url change to: " + url) {
		    	public boolean until() {
		    		return driver.getCurrentUrl().contains(url);
		    	}
		 };
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
     
     public int getWindowCount(final WebDriver driver){
		return driver.getWindowHandles().size();
     }
     
     
     public void waitForWindowCount(final WebDriver driver,final int count){
    	 
    	 new Wait("Window count was not reached") {
 	    	public boolean until() {
     		  return driver.getWindowHandles().size() == count;
 	    	}
 	     };
     }
     
     public static void pauseTest(long millis){
    	try {
			Thread.sleep(millis);
		} catch (InterruptedException ignore) {
			// TODO Auto-generated catch block
		}
     }
     
     public String getUniqueName(String name){
     	return name + System.currentTimeMillis();
      }
}
