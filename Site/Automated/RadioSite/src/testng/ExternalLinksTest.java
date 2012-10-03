package testng;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import utility.DBConnection;
import we7int.Application;

public class ExternalLinksTest extends MethodConfiguration{
	
	private int links = 5, attempts =5, interval=10000;
			
	@DataProvider
	public Iterator<Object[]> getArtistsProvider(ITestContext c) 
						throws ClassNotFoundException, SQLException, InterruptedException {
		
		return Application.getPopularArtists(DBConnection.getConnectionFromXML(c), links);
	}
	
	@DataProvider
	public Iterator<Object[]> getStationsProvider(ITestContext c) 
						throws ClassNotFoundException, SQLException, InterruptedException {
		
		return Application.getStations(DBConnection.getConnectionFromXML(c), links);
	}
	
	@DataProvider
	public Iterator<Object[]> getGenresProvider(ITestContext c) 
						throws ClassNotFoundException, SQLException, InterruptedException {
		
		return Application.getGenres(DBConnection.getConnectionFromXML(c), links);
	}
	
	@DataProvider
	public Iterator<Object[]> getTracksProvider(ITestContext c) 
						throws ClassNotFoundException, SQLException, InterruptedException {
		
		return Application.getPopularTracks(DBConnection.getConnectionFromXML(c), links);
	}
	
	@Test(dataProvider="getArtistsProvider", enabled=true)
	public void playExternalArtistLink(ITestContext context, 
								long artistId, String name, String urlName) throws IOException, InterruptedException  {
		
		String link = String.format("%s/listen/%s/radio", url, urlName);
		System.out.println(link);
		
		Application application = new Application(driver);
		application.openPage(link);
		Assert.assertTrue(application.getChugger().isPlaying(interval, attempts), "Artist station was not started");
	}
	
	
	@Test(dataProvider="getGenresProvider", enabled=true)
	public void testGenresExternalLinks(ITestContext context, String name, String urlName) throws IOException, InterruptedException  {
		
		String link = String.format("%s/listen/%s/genre/radio", url, urlName);
		System.out.println(url);
		
		Application application = new Application(driver);
		application.openPage(link);
		
		Assert.assertTrue(application.getChugger().isPlaying(interval, attempts), "Genres station was not started");
 	}
	
	
	@Test( dataProvider="getStationsProvider", enabled=true)
	public void testStationsExternalLinks(ITestContext context, long radioStation, String name) throws IOException, InterruptedException  {
		
		String link = String.format("%s/listen/i/s%d/%s/stations/radio",
					url, radioStation, name.replace(" ", "-"));
		System.out.println(link);
		
		Application application = new Application(driver);
		application.openPage(link);
				
		Assert.assertTrue(application.getChugger().isPlaying(interval, attempts), "Station was not started");
	}
	
		
	@Test(dataProvider="getTracksProvider", enabled=true)
	public void testTracksExternalLinks(ITestContext context, long albumId,
										String name, String urlName) throws IOException, InterruptedException  {
		
		String link = String.format("%s/listen/%s/radio", url, urlName);
		System.out.println(link);
		
		Application application = new Application(driver);
		application.openPage(link);
		
		Assert.assertTrue(application.getChugger().isPlaying(interval, attempts), "Tracks station was not started");
	}
	
}
