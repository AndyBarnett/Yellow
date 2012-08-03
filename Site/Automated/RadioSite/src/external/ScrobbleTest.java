package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import testng.MethodConfiguration;
import we7int.Application;

public class ScrobbleTest extends MethodConfiguration{

	@Test(description = "Check Latest scrobbled", enabled=true, retryAnalyzer = MethodConfiguration.class)
	public void latest(ITestContext tc) throws Exception {
		method(20, 5, 10000);
	}
		
		
		public void method(int noOfTracks, int trys, int interval) throws Exception {
		Application application = new Application(driver);
		application.getHeader().getSignInPage().signIn("autotest-scrobble", "we7rocks");
		
		String URL = "http://www.development.we7.com/listen/i/s1363/Todays-Hits/radio";
		
		
			application.openPage(URL);
			
			Assert.assertTrue(WaitforScrobble(application.getChugger().getCurrentSong(), trys, interval));

			JSONObject recent = (JSONObject) getLatest().get("recenttracks");
			JSONArray track = (JSONArray) recent.get("track");
			
			
			((JSONObject)((JSONObject)track.get(0)).get("@attr")).get("name");

		}
		
	private boolean WaitforScrobble(String trackName, int trys, int interval ) throws Exception{
		
		for (int j=0; j< trys; j++){
			System.out.println("Waiting for Nowplaying");
			JSONObject recent = (JSONObject) getLatest().get("recenttracks");
			JSONArray track = (JSONArray) recent.get("track");
			
			for (Object o: track ){
			
				JSONObject attr = (JSONObject) ((JSONObject)o).get("@attr");
			
				if (attr!= null){
					String nowPlaying = (String) attr.get("nowplaying");
					//Printing for Debugging
					/*
					System.out.println("LastFM playing = "+((String) ((JSONObject)o).get("name")));
					System.out.println("Chugger playing = "+ trackName);
					System.out.println("");
					*/
					//END Printing for Debugging
					
					if(nowPlaying.equals("true") && ((String) ((JSONObject)o).get("name")).equals(trackName)){		
						return true;
					}
				}
			}
			Thread.sleep(interval);
		}
		return false;
	}
	

	private static JSONObject parseData(URL urlJSON) throws Exception{

		URLConnection connJson = urlJSON.openConnection();
		JSONParser parser=new JSONParser();
		BufferedReader br = new BufferedReader(new InputStreamReader(connJson.getInputStream()));
			
			return (JSONObject) parser.parse(br);
		}

	
	public static JSONObject getLatest() throws MalformedURLException, Exception{
		return parseData(new URL(
				"http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=autowe7&format=json&api_key=b25b959554ed76058ac220b7b2e0a026"));
		}
}