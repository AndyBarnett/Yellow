package utility;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.ITestContext;
import com.google.common.collect.Lists;

public class RegisterUser {
	
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";
	public static final String GENDER = "gender";
	public static final String DOB = "dob";
	public static final String POST_CODE = "postcode";
	public static final String EMAILABLE = "emailable";
	public static final String ALLOW_WE7_MARKETING = "allowWe7Marketing"; 
	public static final String AGREED_TO_TERMS = "agreedToTerms";
	public static final String EMAIL_CONFIRMED = "emailConfirmed";
	public static final String LOGIN = "login";
		
	public static String createSignature(final Map<String, String> params, final String sigParam, final String secretKey) throws Exception {
		 
		final StringBuilder builder = new StringBuilder();

		// Order the keys alphabetically
		final List<String> keys = Lists.newLinkedList(params.keySet());
		Collections.sort(keys);

		for (final String key : keys) {
		   if (!sigParam.equals(key)) {
		        builder.append(key);
		        builder.append('=');
		        builder.append(params.get(key));
		    }
		}

		builder.append(secretKey);
		
		return md5utf8hash(builder.toString());
		    
	}
	
	 public static String getHexString(final byte[] b) {
		 
		StringBuffer result = new StringBuffer();
		
		int n;
		for (int i = 0; i < b.length; i++) {
		     n = (b[i] & 255);
		     if (n < 0x10) {
		       result.append(0);
		     }
		     result.append(Integer.toString(n, 16));
		   }
		   return result.toString();
	 }
	
	 public static String md5utf8hash(final String input) {
		 
	    MessageDigest md = null;
	   
	    try {
	    	
		     md = MessageDigest.getInstance("MD5");
		     md.reset();
		     md.update(input.getBytes("UTF-8"));
		     
		     return getHexString(md.digest());
		     
	    } catch (Exception e) {
		   return null;
	    }
	}
	
	public static HashMap<String, String> getAutoUser(final ITestContext tc, boolean activated) throws Exception{
		
		String userName = DriverUtil.getUniqueUserFromXML(tc);
						
		HashMap<String, String> user = new HashMap<String, String>();
		user.put(RegisterUser.USERNAME, userName);
		user.put(RegisterUser.PASSWORD, "password");
		user.put(RegisterUser.EMAIL, userName + "@we7.com");
		user.put(RegisterUser.GENDER, "M");
		user.put(RegisterUser.DOB, "1980-12-12");
		user.put(RegisterUser.POST_CODE, "OX4 2HN");
		user.put(RegisterUser.EMAILABLE, "false");
		user.put(RegisterUser.ALLOW_WE7_MARKETING, "false");
		user.put(RegisterUser.AGREED_TO_TERMS, "true");
		user.put(RegisterUser.EMAIL_CONFIRMED, activated? "true" : "false");
		user.put(RegisterUser.LOGIN, "false");
		
		String signature = createSignature(user, "signature", tc.getCurrentXmlTest().getParameter("api.secret"));
		
		user.put("signature", signature);
		user.put("apiKey", tc.getCurrentXmlTest().getParameter("api.key"));
		user.put("appVersion", tc.getCurrentXmlTest().getParameter("api.app.version"));
		
		String response = postURL(tc.getCurrentXmlTest().getParameter("api.register.url"), user);
		Assert.assertEquals(response, "\"Account successfully registered\"");	
			
		return user;
	}
			
	public static String postURL(final String targetURL, final Map<String, String> params) throws Exception{
				 		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(targetURL);
	
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String responseText="";
	
		try {
    	
	      	for (final String key : params.keySet()) {
	      		
	    		nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
      	
	      	HttpResponse response = client.execute(post);
	      	
    	   	responseText = DriverUtil.inputStreamAsString(response.getEntity().getContent());
    	
	    } catch (IOException e) {
			e.printStackTrace();
		}
    
	    return responseText.trim();
	}	
}
