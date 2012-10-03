//hi
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RunTest {

	private static ArrayList<String> phoneIDs = new ArrayList<String>();
	private static String inputID;
	private static BufferedReader br;

	public static void main(String[] args) throws IOException,
			InterruptedException {

		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Select the ID(s) of the phones you wish to unleash monkies on? (Enter 0 when done)\n");

		System.out.println("1. Acer e400");
		System.out.println("2. Archos Tablet");
		System.out.println("3. Barry's 10.1 tablet");
		System.out.println("4. Gareth's S3");
		System.out.println("5. htc Desire");
		System.out.println("6. htc One S");
		System.out.println("7. htc One X");
		System.out.println("8. htc Sensation");
		System.out.println("9. Motorola RAZR MAXX");
		System.out.println("\n0. Done\n");

		MakeList();
	}

	private static void MakeList() throws IOException {
		inputID = br.readLine();

		if (TryParseInt(inputID)) {
			int idInputInt = Integer.parseInt(inputID);
			switch (idInputInt) {
			case 0:
				ProcessList();
				break;
			case 1:
				phoneIDs.add("018459380543");
				break;
			case 2:
				phoneIDs.add("A80S-92CD11F7");
				break;
			case 3:
				phoneIDs.add("C8F13C7005EFF8E");
				break;
			case 4:
				phoneIDs.add("4df184e008e55f8d");
				break;
			case 5:
				phoneIDs.add("HT0CKPL00415");
				break;
			case 6:
				phoneIDs.add("SH23WW403849");
				break;
			case 7:
				phoneIDs.add("HT23YW126050");
				break;
			case 8:
				phoneIDs.add("SH19WV807699");
				break;
			case 9:
				phoneIDs.add("014FDD3708015018");
				break;
			default:
				break;
			}
			MakeList();
		}
		else 
		{
			
		}
	}

	private static void ProcessList() {
		for (int i = 0; i < phoneIDs.size(); i++) {
			new Thread(new Tasks(phoneIDs.get(i))).start();
		}
	}

	private static boolean TryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
}