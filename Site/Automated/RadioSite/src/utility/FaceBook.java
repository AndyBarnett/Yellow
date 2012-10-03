package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;
import org.testng.Reporter;

public class FaceBook {
	
	private static String appID = "247378835287174";
	private static String appSecret = "64ef0065772d4ff68bf7145301eda0b8";
		
	public static JSONObject createUser(String accessToken) throws Exception{
		
		URL facebook = new URL(
				"https://graph.facebook.com/" + appID + "/accounts/test-users?"
						  +"installed=true"
						  +"&permissions=read_stream,publish_stream"
						  +"&method=post"
						  +"&access_token=" + accessToken
		);
		
		JSONObject response = parseData(facebook);

		Reporter.log("id=" + (String) response.get("id"));
		Reporter.log("access_token=" + (String) response.get("access_token"));
		Reporter.log("login_url=" + (String) response.get("login_url"));
		Reporter.log("email=" + (String) response.get("email"));
		Reporter.log("password=" + (String) response.get("password"));
		
		return response;
		
	}
	
	public static String getAccesstoken() throws IOException{
	
		/*Get auth token*/
		
		URL facebookAuth = new URL(
				"https://graph.facebook.com/oauth/access_token?client_id="+ appID +"&client_secret="
							+appSecret +"&grant_type=client_credentials");
	
		URLConnection fbac = facebookAuth.openConnection();
				
		String response = DriverUtil.inputStreamAsString(fbac.getInputStream());
				
		if (response.equals("")){
	    	Reporter.log("Could not get access token");
	    	Assert.fail();
	    }
		
		return response.split("=")[1];
	}
	
	public static boolean changePassword(String userId, String pass, String accessToken) throws IOException{
		
		URL facebookChangePassword = new URL(
				"https://graph.facebook.com/" + (String) userId + "?"
					  +"&password=" + pass
					  +"&email=frank-test0989@we7.com"
					  +"&method=post"
					  +"&access_token=" + accessToken
			);
			
			URLConnection fbu = facebookChangePassword.openConnection();
			String updateResponse = DriverUtil.inputStreamAsString(fbu.getInputStream());
			
			if ("true\n".equals(updateResponse))
				return true;
					
			return false;
	}
	
	public static boolean deleteUser(String userId, String accessToken) throws IOException{
		
		URL deleteUser = new URL(
				"https://graph.facebook.com/" + (String)  userId + "?"
					+"&method=delete"
					+"&access_token=" +  accessToken
		);
		
		URLConnection fbd = deleteUser.openConnection();
		String deleteResponse = DriverUtil.inputStreamAsString(fbd.getInputStream());
		
		if ("true\n".equals(deleteResponse))
			return true;
		
		return false;
	}
	
	
	public static JSONObject getUserDetails(String userId, String accessToken) throws Exception{
		
		URL facebookUserDetails = new URL(
				"https://graph.facebook.com/" + (String)  userId + "?"
					+"&access_token=" +  accessToken
		);
		
		JSONObject response = parseData(facebookUserDetails);
		
		Reporter.log("id=" + (String) response.get("id"));
		Reporter.log("name=" + (String) response.get("name"));
		Reporter.log("first_name=" + (String) response.get("first_name"));
		Reporter.log("last_name=" + (String) response.get("last_name"));
		Reporter.log("username=" + (String) response.get("username"));
	
		return response;

	}
	
	
	private static JSONObject parseData(URL url) throws Exception{
		
		URLConnection connection = url.openConnection();
		JSONParser parser=new JSONParser();
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		return (JSONObject) parser.parse(br);
	}
	
	
	public static JSONArray getLikes(String userId, String accessToken) throws Exception{
		
		URL facebookLikes = new URL(
				"https://graph.facebook.com/" + (String)  userId + "likes?"
					+"&access_token=" +  accessToken
		);
		
		JSONObject response = parseData(facebookLikes);
		JSONArray array=(JSONArray)response.get("data");

		return array;
	}
}


