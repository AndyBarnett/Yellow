package com.we7.test.monkey;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Tasks implements Runnable {  
    
	private String phoneID;
	private final static String notify = "andrewb@we7.com";
	private final static String filter = ".*(We7Application)+.*";
	private static String email="andrewb@we7.com";
	private static String password="86753098675309";
	private static long startTime;
	private static long endTime;
	
	
	public Tasks(String pPhoneID)
	{
		phoneID = pPhoneID;
	}
	
	public void run(){  
		Monitor m = new Monitor();
		System.out.print("Processing phone with ID " + phoneID +"...\n");
		startTime = System.currentTimeMillis();

		m.runProcess("adb -s" + phoneID + " shell monkey -p com.we7.player --throttle 500 -vv -s 0 99999999");
		System.out.println("adb -s" + phoneID + " shell logcat -c");
		System.out.println("Waiting for error: " + filter + " on phone with ID" + phoneID);
		String line = m.getLine("adb -s" + phoneID + " shell logcat *:E", filter); 

		if (!line.equals("")) {
			endTime = System.currentTimeMillis();

			System.out.println("Found error on phone with ID: " + phoneID + filter);
			String pid = m.getLine("adb -s" + phoneID + " shell ps", ".*(com.android.commands.monkey).*");

			if (!pid.equals("")) {
				pid = pid.split("\\s+")[1];
				System.out.println("Killing Monkey on phone with ID" + phoneID );
				m.runProcess("adb -s" + phoneID + " shell kill " + pid);
			}
			sendMail(line);
		}
		System.out.print("Processing of phone with ID " + phoneID +" Complete!\n");
		
    }  
	
private static void sendMail(String line){
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		Session session = Session.getInstance(props);
	    Message message = new MimeMessage(session);
	    
	    long toaltime = endTime - startTime;
	    System.out.println(String.format("Test ran for: %d seconds", toaltime/1000));
		    			 			    
	    // add a TO address
	    try {
	    	
	    	System.out.println("Sending mail: " + line);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(notify));
			message.setSubject(line);
		    message.setContent(line, "text/plain");
			
		    Transport transport = session.getTransport("smtp");
		    transport.connect("smtp.gmail.com", 587, email, password);
		    transport.sendMessage(message, message.getAllRecipients());
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
}  