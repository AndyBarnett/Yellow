//hi
package com.we7.test.monkey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Monitor {

	private Process p=null;
	private InputStream is=null;
	InputStreamReader isr=null;
	BufferedReader br=null;
		
	public void intialize(String process) throws IOException{
		destroy();
		Process p = Runtime.getRuntime().exec(process);
		is = p.getInputStream();
		isr = new InputStreamReader(is);
		br = new BufferedReader(isr);
	}

	public String getLine(String process, String pattern) throws IOException{
		
		intialize(process);
		
		String line;
		while ((line = br.readLine()) != null) {
			if(line.matches(pattern)){
				break;
			}else{
				line="";
			}
		}
		
		destroy();
		return line;
	}
		
	public boolean findInProcess(String process, String pattern) throws IOException{
			
		intialize(process);
		String line;
		
		boolean found=false;
		while ((line = br.readLine()) != null) {
				if(line.matches(pattern)){
				found=true;
				break;
			}
		}	

		destroy();
		return found;
	}
	
	public void runProcess(String process) throws IOException{
		
		intialize(process);
		try {
			p.waitFor();
		} catch (Exception e) {
	
		}
		destroy();		
	}
	
		
	public void destroy() throws IOException{
		
		if (br!=null){br.close(); br=null;}
		if (isr!=null){isr.close(); isr=null;} 
		if (is!=null){is.close(); is=null;}
		
		if (p!=null){
			p.destroy();
			p=null;
		}
	}
}
