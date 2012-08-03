package we7int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Chugger {
	
	@FindBy(xpath = "//*[contains(@class,'chugger-current')]//*[@class='artist']")
	private WebElement songName;
	
	@FindBy(xpath = "//*[contains(@class,'chugger-current')]//*[@class='song']")
	private WebElement artistName;
	
	@FindBy(id = "played-bar")
	private WebElement playedBar;
	
	@FindBy(id = "loader-bar")
	private WebElement loaderBar;
	
	@FindBy(className = "play-next")
	private WebElement skip;
	
	private float getWidth(String atttribute){
			
		float width=0;
		Pattern p = Pattern.compile("\\d+\\.?\\d+");
		Matcher m = p.matcher(atttribute);
		
		if (m.find()){
			try{	
				width =  Float.parseFloat(m.group().toString());
			}
			catch (NumberFormatException e){
				width = 0;
			}
		}else{
			width =  0;
		}
		
		return width;
	}
	
	public String getCurrentSong(){
		return songName.getAttribute("title");
	}
	
	public String getCurrentArtist(){
		return artistName.getAttribute("title");
	}
	
	public void skip(){
		skip.click();
	}
	
	private float getPlayerwidth(){
		return getWidth(playedBar.getAttribute("style"));
	}
	
	private float getLoaderwidth(){
		return getWidth(loaderBar.getAttribute("style"));
	}
	
	public boolean isPlaying(int milliseconds, int interval) throws InterruptedException{
		
		float startWidth = getPlayerwidth();
				
		for (int i=0 ; i < interval ; i++){
			if (getPlayerwidth() > startWidth){
				return true;
			}
			Thread.sleep(milliseconds);
		}
		return false;
	}
	
	private boolean isLoading(int milliseconds, int interval) throws InterruptedException{
		
		float startWidth = getLoaderwidth();
		
		for (int i=0 ; i < interval ; i++){
			if (getLoaderwidth()> startWidth){
				return true;
			}
			Thread.sleep(milliseconds);
		}
		return false;
	}
	
}
