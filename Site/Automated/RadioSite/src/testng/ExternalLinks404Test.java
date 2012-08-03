package testng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Iterator;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utility.DBConnection;
import we7int.Application;

public class ExternalLinks404Test{
	
	@DataProvider
	public Iterator<Object[]> getGenresProvider(ITestContext c) 
						throws ClassNotFoundException, SQLException, InterruptedException {
		
		return Application.getGenres(DBConnection.getConnectionFromXML(c));
	}
	
	@DataProvider
	public Iterator<Object[]> getAlbumsProvider(ITestContext c)
						throws ClassNotFoundException, SQLException, InterruptedException {
		
		return Application.getPopularAlbums(DBConnection.getConnectionFromXML(c), 100);
	}
	
	@DataProvider
	public Iterator<Object[]> getTracksProvider(ITestContext c) 
						throws ClassNotFoundException, SQLException, InterruptedException {
		
		return Application.getPopularTracks(DBConnection.getConnectionFromXML(c), 100);
	}
	
	@DataProvider
	public Iterator<Object[]> getStationsProvider(ITestContext c) 
						throws ClassNotFoundException, SQLException, InterruptedException {
		
		return Application.getStations(DBConnection.getConnectionFromXML(c), 100);
	}
	
	@DataProvider
	public Iterator<Object[]> getArtistsProvider(ITestContext c) 
						throws ClassNotFoundException, SQLException, InterruptedException {
		
		return Application.getPopularArtists(DBConnection.getConnectionFromXML(c), 100);
	}
	
	
	@DataProvider
	public Object[][] getPagesProvider() {
		
		return new Object [][]{
				{"/about"},
				{"/upgrade"},
				{"/mobile"},
				{"/stations/genres"},
				{"/stations/decades"},
				{"/stations/artists"},
				{"/signin/sign-in"},
				{"/signin/forgot-password"},
				{"/signin/password-sent"},
				{"/start"},
				{"/stations/recommended"}
		};
	}
	
	private int getResponseCode(String url) throws IOException{
		
		HttpURLConnection connection = null;
		
		URL serverAddress = null;
	    serverAddress = new URL(url);
	    connection = (HttpURLConnection)serverAddress.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(1000*60);
        connection.connect();
      
        return connection.getResponseCode();
	}
	
	@Test(dataProvider="getGenresProvider", enabled=true)
	public void testGenres404Links(ITestContext context, String name, String urlName) throws IOException  {
		
		String url = context.getCurrentXmlTest().getParameter("url");
		
		url = String.format("%s/listen/%s/genre/radio", url, urlName);
		System.out.println(url);
		
		Assert.assertNotEquals(getResponseCode(url), HttpURLConnection.HTTP_NOT_FOUND, url);
 	}
	
	@Test(dataProvider="getArtistsProvider", enabled=true)
	public void testArtists404Links(ITestContext context, long artistId,
							String name, String urlName) throws IOException  {
		
		String url = context.getCurrentXmlTest().getParameter("url");
		
		url = String.format("%s/listen/%s/radio", url, urlName);
		System.out.println(url);
			
		Assert.assertNotEquals(getResponseCode(url), HttpURLConnection.HTTP_NOT_FOUND, url);
	}
	
	@Test(dataProvider="getTracksProvider", enabled=true)
	public void testTracks04Links(ITestContext context, long albumId,
										String name, String urlName) throws IOException  {
		
		String url = context.getCurrentXmlTest().getParameter("url");
		
		url = String.format("%s/listen/%s/radio", url, urlName);
		System.out.println(url);
					
		Assert.assertNotEquals(getResponseCode(url), HttpURLConnection.HTTP_NOT_FOUND, url);
	}
	
	
	@Test( dataProvider="getStationsProvider")
	public void testStations404Links(ITestContext context, long radioStation, String name) throws IOException  {
		
		String url = context.getCurrentXmlTest().getParameter("url");
		
		url = url.replace(" ", "-");
		url = String.format("%s/listen/i/s%d/%s/stations/radio", url, radioStation, name);
		System.out.println(url);
		
		Assert.assertNotEquals(getResponseCode(url), HttpURLConnection.HTTP_NOT_FOUND, url);
    	
	}
		
	@Test( dataProvider="getPagesProvider", enabled=true )
	public void testPages404(ITestContext context, String page) throws IOException  {
		
		String url = context.getCurrentXmlTest().getParameter("url");
		System.out.println(url + page);
		Assert.assertNotEquals(getResponseCode(url + page),	
												HttpURLConnection.HTTP_NOT_FOUND, url + page);
	}

}
