package utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ChartInfo {
	
	private static JSONObject parseData(URL url) throws Exception{
		
		URLConnection connection = url.openConnection();
		JSONParser parser=new JSONParser();
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		return (JSONObject) parser.parse(br);
	}	
	
	public static JSONObject getGenres() throws MalformedURLException, Exception{
		return parseData(new URL(
				"http://api.we7.com/api/0.1/chartInfo?apiKey=test&appVersion=1&chartNames=int-genre"));
	}
	
	public static JSONObject getDecades() throws MalformedURLException, Exception{
		return parseData(new URL(
				"http://api.we7.com/api/0.1/chartInfo?apiKey=test&appVersion=1&chartNames=int-decades"));
	}
}
