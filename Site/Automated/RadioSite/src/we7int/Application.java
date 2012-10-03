package we7int;

import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Document;
import utility.DBConnection;
import utility.DriverUtil;


public class Application {
	
	private WebDriver driver;
	
	public Application(WebDriver driver){
		 this.driver = driver;
	}
	
	public final Chugger getChugger(){
		return PageFactory.initElements(driver, Chugger.class);	
	}
	
	public final Header getHeader(){
		return PageFactory.initElements(driver, Header.class);	
	}
	
	public final Footer getFooter(){
		return PageFactory.initElements(driver, Footer.class);	
	}
	
	public final Home getHomePage(){
		return PageFactory.initElements(driver, Home.class);	
	}
	
	public final void activateUserDatabase(Connection c, String email) throws ClassNotFoundException, SQLException, InterruptedException{
					
		assert (DBConnection.update(c, "UPDATE users SET enabled= 't' " +
									   "WHERE email= '" + email + "'" ) ==  1);
	}
	
	public final void openPage(String url){
		driver.get(url);	
	}
	
	public Home activateUser(Connection c, String email, String domain) 
							throws ClassNotFoundException, SQLException, InterruptedException{
		
		HashMap<String,Object> user = getUser(c, email,3,10000);
		
		String activationCode = (String) user.get(DatabaseUser.ACTIVATE_CODE);
		String activationUrl;
		
		if (activationCode!=""){
			activationUrl = String.format("%s/signin/confirmRegistration?u=%s&a=%s&loginAction=cr",
					domain.replace("http", "https"),
					user.get("username").toString(),
					activationCode
			);
			
		Reporter.log("Opening activation url: " + activationUrl);
		
		driver.get(activationUrl);
			
		}else{
			Assert.fail("Could not find usr: " 
							+ user + "in the database!");
		}
			 
		return getHomePage();
	}
	
		
	public HashMap<String,Object> getUser(Connection c, String email) /* allows multiple tries*/
							throws ClassNotFoundException, SQLException, InterruptedException{
		return getUser(c, email, 1, 0);
	}
	
	public static Iterator <Object[]> getGenres(Connection c, int limit) 
									throws ClassNotFoundException, SQLException, InterruptedException{
		
		return DBConnection.getData(
				c,
				String.format(
				"SELECT name, url_name  " +
				"FROM genres " +
				"LIMIT %d",limit)
		);
	}
	
	public static Iterator <Object[]> getStations(Connection c) 
									throws ClassNotFoundException, SQLException, InterruptedException{
		
		return DBConnection.getData(
				c,
				"SELECT radio_station_id, name " +
				"FROM radio_stations"
		);
	}
		
	public static Iterator <Object[]> getStations(Connection c, int limit) 
									throws ClassNotFoundException, SQLException, InterruptedException{
		
		return DBConnection.getData(			
				c, 
				String.format(
				"SELECT radio_station_id, name " +
				"FROM radio_stations " +
				"WHERE searchable=TRUE " +
					"LIMIT %d",limit)
			);
		
	}
	
	public static Iterator <Object[]> getPopularPlaylists(Connection c, int limit) 
									throws ClassNotFoundException, SQLException, InterruptedException{

		return DBConnection.getData(			
				c, 
				String.format(
				"SELECT playlist_id, name, description, popularity " +
				"FROM playlists " +
				"ORDER BY popularity DESC "  +
				"LIMIT %d",limit)
		);
	}
	
	public static Iterator <Object[]> getFeaturedPlaylists(Connection c, int limit) 
									throws ClassNotFoundException, SQLException, InterruptedException{

		return DBConnection.getData(			
				c, 
				String.format(
				"SELECT playlists.playlist_id, name, description, popularity " +
				"FROM playlists, playlists_featured " +
				"WHERE playlists.playlist_id = playlists_featured.playlist_id "  +
				"LIMIT %d",limit)
		);
}

	
	public static Iterator <Object[]> getPopularAlbums(Connection c, int limit) 
									throws ClassNotFoundException, SQLException, InterruptedException{
		return DBConnection.getData(
				c, 
				String.format(
				"SELECT albums.album_id, title, url_name " +
				"FROM albums, album_popularities " +
				"WHERE albums.album_id = album_popularities.album_id " +
				"ORDER BY album_popularities.popularity DESC " +
				"LIMIT %d",limit)
		 );

	}
	
	public static Iterator <Object[]> getPopularArtists(Connection c, int limit) 
						throws ClassNotFoundException, SQLException, InterruptedException{
				
		return DBConnection.getData(
				c, 		
				String.format(
				"SELECT artists.artist_id, name, url_name " +
				"FROM artists, artist_popularities  " +
				"WHERE artists.artist_id = artist_popularities.artist_id " +
				"AND artists.has_active_track=TRUE " +
				"ORDER BY artist_popularities.popularity DESC " +
				"LIMIT %d",limit)
		);
	}
	
	public static Iterator <Object[]> getPopularTracks(Connection c, int limit) 
									throws ClassNotFoundException, SQLException, InterruptedException{
		return DBConnection.getData(
				c,
				String.format(
				"SELECT tracks.track_id, title, url_name, popularity " +
				"FROM tracks, track_popularities  " +
				"WHERE tracks.track_id = track_popularities.track_id " +
				"AND publicly_streamable=TRUE " +
				"ORDER BY popularity DESC " +
				"LIMIT %d",limit)
		);
	}
	
	public static Iterator <Object[]> getArtists(Connection c) 
					throws ClassNotFoundException, SQLException, InterruptedException{
			
		return DBConnection.getData(
				c,
				String.format(
				"SELECT artist_id, name, url_name " +
				"FROM from artists " +
				"WHERE has_active_track=TRUE")
		);
			
	}
			
	public HashMap<String,Object> getUser(Connection c, String email, int trys, int interval)
							throws ClassNotFoundException, SQLException, InterruptedException{
		
		ResultSet resultset = null;
		PreparedStatement pstmt = null;
		HashMap<String,Object> user;
		
		try{
			
			pstmt =  c.prepareStatement("SELECT * FROM users WHERE email =?");
			pstmt.setString(1, email);
			resultset = DBConnection.pollDatabase(pstmt, trys, interval);
			
			assert resultset != null; 
			
			ResultSetMetaData md = resultset.getMetaData();
			
			int columns = md.getColumnCount();
			user = new HashMap<String,Object>(columns);
			
		    for(int i=1; i<=columns; ++i){            
		    	user.put(md.getColumnName(i),resultset.getObject(i));
		    }
		    
		}finally{
			
			if (pstmt!=null){
				pstmt.close();
			}
						
			if (resultset!=null){
				resultset.close();
			}
			
			if (c!=null){
				c.close();
			}
		}
				
		return user;	
	}
	
	
	
	
	
	
	public String makeGigyaDeleteURL(int uid){
				
		return String.format("https://socialize-api.gigya.com/deleteAccount?apiKey=%s&uid=%d&secret=%s",
				SocialSignIn.apiKey,
				uid,
				SocialSignIn.apiSecretKey
				);
	}
	
	public int deleteUserFromGigya(int uid) throws Exception{
		
		/*delete account*/
		
		URL gigyaDeleteAccountURL = new URL(makeGigyaDeleteURL(uid));
		URLConnection urlConnection = (URLConnection) gigyaDeleteAccountURL.openConnection();
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		   DocumentBuilder db = factory.newDocumentBuilder();
		    
		Document response = db.parse(urlConnection.getInputStream());
			 			    	    
		String errorCode = response.getElementsByTagName("errorCode").item(0)
			.getChildNodes().item(0).getNodeValue();
		
		switch (Integer.parseInt(errorCode)) {
		
	       	case 0:  System.out.println("User deleted from Gigya database");
	    			break;
	        case 403005:  System.out.println("The user id not found. Already deleted?");
	                 break;
	        case 400092:  System.out.println("This method requires a 'ApiKey' parameter which was not passed");
	                 break;
	        case 400002: System.out.println("The method requires some parameters.");
	                 break;
	        case 403010:  System.out.println("The request has an invalid secret key");
	                 break;
	        default: System.err.println("Could not delete user");
	                 break;
		}
		
		DriverUtil.serialize(response, System.out); //Print the response
		
		return (Integer.parseInt(errorCode));
	}
	
	public void deleteUser(Connection c, String email) throws Exception{
		
		int uid = deleteUserFromDatabase(c, email);
		
		if (uid != -1){ //found user
			int response = deleteUserFromGigya(uid);
			assert response == 0 || response == 403005 ; //success || already deleted
		}
	}	

	public static int deleteUserFromDatabase(Connection c, String email) throws ClassNotFoundException, SQLException, InterruptedException{
		
	
		ResultSet resultSet = null;
		int userId = -1;
				
		PreparedStatement deleteUserPropStatement = null,
				deleteUserRegStatement = null, deleteUserStatement = null;
		
		try {
			 
			PreparedStatement selectUserStatement = c.prepareStatement("select * from users where email =?");
			selectUserStatement.setString(1, email);
		    resultSet = selectUserStatement.executeQuery();
											
			if (resultSet.next()){
					
					userId = Integer.parseInt(resultSet.getString("user_id"));
									
					c.setAutoCommit(false);
					
					deleteUserPropStatement = 
						c.prepareStatement("delete from user_properties where user_id =?");
					deleteUserPropStatement.setInt(1, userId);
					deleteUserPropStatement.execute();
					deleteUserRegStatement = 
						c.prepareStatement("delete from user_registration WHERE user_id =?");
					deleteUserRegStatement.setInt(1, userId);
					deleteUserRegStatement.executeUpdate();
					deleteUserStatement = 
						c.prepareStatement("delete from users WHERE user_id =?");
					deleteUserStatement.setInt(1, userId);
					deleteUserStatement.executeUpdate();
							
					c.commit();
					
					System.out.println("User " + userId +  " deleted from We7 database");
								
			 }else{
				 System.out.println("User does not exist - no delete necessary");
				 userId=-1;
			 }
		   } catch (SQLException e ) {
			   
			   	e.printStackTrace();
			   	
		        if (c != null) {
		            try {
		                System.err.print("Transaction is being rolled back");
		                c.rollback();
		            } catch(SQLException excep) {
		            	System.err.print("Connection could not be rolled back");
		            }
		        }
		    } finally {
		    	     		       
		       if (deleteUserRegStatement != null) {
		    	   deleteUserRegStatement.close();
		       }
		       
		       if (deleteUserPropStatement != null) {
		    	   deleteUserPropStatement.close();
		       }
		       
		       if (deleteUserStatement != null) {
		            deleteUserStatement.close();
		       }
		       
		       if (resultSet != null) {
		    	   resultSet.close();
		       }
		       
		       if (c != null) {
		    	   c.close();
		       }
		    }
		    
		    return userId;
	}
			
	public String getPageText(){
			return driver.findElement(
					By.xpath("//body")).getText();
		}
}
