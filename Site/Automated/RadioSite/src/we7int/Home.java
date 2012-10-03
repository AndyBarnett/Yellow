package we7int;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Home {
			
	 private WebDriver driver;
 
	 public Home(WebDriver driver){
		 this.driver = driver;
	 }
	 
	 public void getSubURL(String url){
		driver.get(url);
	 }
	 
	 public List<String> getTitles(String tabName){
		 
		 List<String> titles = new ArrayList<String>();
		 List<WebElement> list = driver.findElements(By.xpath(
			String.format("//*[contains(@id,'%sStations')]//p[@class='title']",tabName)));
		 
		 for(WebElement e : list){
			 titles.add(e.getText());
		 }	 
		return titles;
	 }
	 
	 
	 public void getNextPage(){
			driver.findElement(By.cssSelector("span.micro-pagination-link.next")).click();
	}

}	 
