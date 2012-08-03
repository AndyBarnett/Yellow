package utility;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.testng.ITestContext;

public class DBConnection {
	
	public static Connection getConnectionFromXML(ITestContext c) throws ClassNotFoundException, SQLException{
		
		String server = c.getCurrentXmlTest().getParameter("db.server");
		String database = c.getCurrentXmlTest().getParameter("db.database");
		String user = c.getCurrentXmlTest().getParameter("db.user");
		String password = c.getCurrentXmlTest().getParameter("db.pasword");
		
		return DBConnection.getConnection(server, database, user, password);
	}
	
	public static Connection getConnection(String server, String database, String user, String pass) throws ClassNotFoundException, SQLException{
		
		Connection connection = null;
		
		try {
 			Class.forName("org.postgresql.Driver");
 		} catch (ClassNotFoundException e) {
 			
 			System.out.println("PostgreSQL JDBC Driver not found, "
 										+ "Include in your library path!");
			throw (e);
		}
 			
 		try {
 			connection = DriverManager.getConnection(
 					String.format("jdbc:postgresql://%s/%s", server, database)
 						, user, pass);
		} catch (SQLException e) {
 			System.out.println("Connection Failed! Check paramaters.");
 			throw (e);
		}
		
		return connection;
	}
	
	public static ResultSet pollDatabase(PreparedStatement pstmt, int trys, int interval) throws SQLException, InterruptedException{
		
		/*Keep trying until object found, if nothing found return null*/
		
		for (int i=0; i< trys; i++){
							
			ResultSet resultSet = pstmt.executeQuery();
		
			if (resultSet.next()){
				return resultSet; //found
			}else if((i+1)==trys){
				return null; //last try
			}
			
			Thread.sleep(interval);
		}
			
		return null;
	}
	
	
	public static Iterator <Object[]> resultSetToDataProvider(ResultSet resultSet) throws SQLException{
		
		ArrayList<Object[]> rows =null;
		
		ResultSetMetaData md = resultSet.getMetaData();
		
		int columns = md.getColumnCount();
		rows= new ArrayList<Object[]>() ;
		
		while (resultSet.next()){
			
			Object t[] = new Object[columns];
			for(int i=1; i<=columns; ++i){            
				t[i-1] = resultSet.getObject(i);
		    }
			rows.add(t);
		}
		
		return rows.iterator();
		
	}
	
	public static Iterator <Object[]>/*testng dataprovider format*/ getData(Connection connection, String query) throws SQLException, ClassNotFoundException{
		
		ResultSet resultSet = null;
		Statement selectGenresStatement=null;
		
		Iterator <Object[]> rows=null;
				
		try {
			
			System.out.println("Getting data from database.....");
			
			selectGenresStatement = connection.createStatement();
			
			resultSet = selectGenresStatement.executeQuery(query);
			
			rows = resultSetToDataProvider(resultSet);
			
		} finally {
		    	     		       
		       if (selectGenresStatement != null) {
		    	   selectGenresStatement.close();
		       }
		       if (resultSet != null) {
		    	   resultSet.close();
		       }
		       if (connection != null) {
		    	   connection.close();
		       }
		}
			
		return rows;
	}
	

	public static int update(Connection connection, String statement) throws SQLException, ClassNotFoundException{
	
		int updateCount=0;
		Statement stmt = null;
		
		try {
			
			stmt = connection.createStatement();
			
		    updateCount = stmt.executeUpdate(statement);
		    
		} finally {
		       
		       if (stmt != null) {
		    	   stmt.close();
		       }
		       
		       if (connection != null) {
		    	   connection.close();
		       }
		}
		
		return updateCount;
		
	}
}


