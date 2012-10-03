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
	
//NEW
	@FindBy(css= "div#now-playing-like-bar ul.like-bar li.like-button-item div.like-button span.like-button-text")
	private WebElement share;
	
	@FindBy(className = "main-action")
	private WebElement playPause;

	public Chugger(WebDriver driver){
        this.driver = driver;
   	}
//NEW
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
//NEW
	public void playPause(){
		System.out.println("clicking play/pause!");
		playPause.click();
	}
//NEW
	private float getPlayerwidth(){
		return getWidth(playedBar.getAttribute("style"));
	}
	
	private float getLoaderwidth(){
		return getWidth(loaderBar.getAttribute("style"));
	}
//NEW (The Pagefactory is needed for ln37 of ShareTest)
	public Share getShareVeil(){
		share.click();
		return PageFactory.initElements(driver, Share.class);
	}
//NEW
	
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
