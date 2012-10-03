package com.we7.test.monkey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RunTest {
	
	private final static String notify = "andrewb@we7.com";
	private final static String filter = ".*(We7Application)+.*";
	private static String email="andrewb@we7.com";
	private static String password="86753098675309";
	private static long startTime;
	private static long endTime;
			
	public static void main(String[] args)throws IOException, InterruptedException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Which phone do you want to unleash monkey on?");
        
        System.out.print("1. Acer e400\n");
        System.out.print("2. Archos Tablet\n");
        System.out.print("3. Barry's 10.1 tablet\n");
        System.out.print("4. Gareth's S3\n");
        System.out.print("5. htc Desire\n");
        System.out.print("6. htc One S\n");
        System.out.print("7. htc One X\n");
        System.out.print("8. htc Sensation\n");
        System.out.print("9. Motorola RAZR MAXX\n");
        System.out.print("10. Nexus 7\n");
        System.out.print("11. Samsung Europa\n");
        System.out.print("12. Samsung Galaxy Ace\n");
        System.out.print("13. Samsung Galaxy Ace 2\n");
        System.out.print("14. Samsung Galaxy S2\n");
        System.out.print("15. Samsung Galaxy S3\n");
        System.out.print("16. Sony Xperia x10i\n");
		
		 String idInput = null;
		 String phoneID = idInput;

			 try {
				 idInput = br.readLine();
			 } catch (IOException e) {
		         System.out.println("Error!");
		         System.exit(1);
		       }
			 int idInputInt = Integer.parseInt(idInput);
			 
			 switch (idInputInt){
			 case 1:
				 phoneID = "018459380543";
			 case 2:
				 phoneID = "A80S-92CD11F7";
			 case 3:
				 phoneID = "C8F13C7005EFF8E";
			 case 4:
				 phoneID = "4df184e008e55f8d";
			 case 5:
				 phoneID = "HT0CKPL00415";
			 case 6:
				 phoneID = "SH23WW403849";
			 case 7:
				 phoneID = "HT23YW126050";	 
			 case 8:
				 phoneID = "SH19WV807699";
			 case 9: 
				 phoneID = "014FDD3708015018";
			 case 10: 
				 phoneID = "015d2a5064180615";
			 case 11:
				 phoneID = "I55002720533d";
			 case 12:
				 phoneID = "S5830286e11b8";
			 case 13:
				 phoneID = "B1AEF789D32220BEF3637E3C96A018A";
			 case 14:
				 phoneID = "00192E2C34C7BE";
			 case 15:
				 phoneID = "4df1caf500455fdd";
			 case 16:
				 phoneID = "CB511J75EV";
			 }
		 

		Monitor m = new Monitor();
		startTime = System.currentTimeMillis();
		
		m.runProcess("adb -s"+phoneID+" shell monkey -p com.we7.player --throttle 500 -vv -s 0 99999999");
		m.runProcess("adb -s"+phoneID+" shell logcat -c");
		System.out.println("Waiting for error: " + filter);
		String line = m.getLine("adb -s"+phoneID+" shell logcat *:E", filter);
		
		if (!line.equals("")){
			endTime = System.currentTimeMillis();
			
			System.out.println("Found error: " + filter);
			String pid = m.getLine("adb -s"+phoneID+" shell ps", ".*(com.android.commands.monkey).*");
			
			if (!pid.equals("")){
				pid = pid.split("\\s+")[1];
				System.out.println("Killing Monkey");
				m.runProcess("adb -s"+phoneID+" shell kill " + pid);
			}
			
			sendMail(line);
		}
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