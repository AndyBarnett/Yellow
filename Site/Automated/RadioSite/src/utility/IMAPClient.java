package utility;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;
import org.testng.ITestContext;

public class IMAPClient {
	
	private String userName, password;
	private Store store;
	private Folder folder;
	
		
	public IMAPClient(ITestContext tc){
		
		userName = tc.getCurrentXmlTest().getParameter("we7.email.prefix") + "@we7.com" ;
		password = tc.getCurrentXmlTest().getParameter("we7.email.password");
	}
	
	public IMAPClient(String userName, String password){
		
		this.userName = userName;
		this.password = password;
	}
	
	private Store getStore() throws MessagingException{
		
		if (store == null){
			
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			Session session = Session.getDefaultInstance(props, null);
			store = session.getStore("imaps");
		}
		
		if( store.isConnected() == false){
			store.connect("imap.gmail.com", userName, password);
		}

		return store;
	}
	
	
	public void refresh(String folderName) throws MessagingException{
		
		if (folder != null && folder.isOpen()){
			folder.close(false);
		}	
			
		folder = store.getFolder(folderName);
		folder.open(Folder.READ_ONLY);
	}
	
	public Message [] waitForMail(String folderName, SearchTerm term, int attempts, long milliSeconds) 
				throws InterruptedException, MessagingException{
		
		int i = 0;
		Message[] msgs;
		store = getStore();
					
		do { /*Reopen the folder*/
			
			System.out.println("Searching for mail....");
			refresh(folderName);
			msgs = folder.search(term);
			
			if (msgs.length == 0)Thread.sleep(milliSeconds);
			i++;
				
		} while (msgs.length == 0 && i < attempts );
		
		return msgs;
	}
	
	public int waitForMailCount(String folderName, SearchTerm term, int attempts, long milliSeconds) 
				throws InterruptedException, MessagingException{
		return waitForMail(folderName, term, attempts, milliSeconds).length;
	}
	
	public void close() throws MessagingException{
		
		if (folder != null && folder.isOpen()){
			folder.close(false);
		}
		
		if (store != null && store.isConnected()){
			store.close();
		}
	}
}

