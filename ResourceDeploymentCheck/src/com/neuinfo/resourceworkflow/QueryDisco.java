package com.neuinfo.resourceworkflow;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class QueryDisco {
	private static final Logger LOG = Logger.getLogger(QueryDisco.class.getSimpleName());
	// Example from http://www.mkyong.com/jdbc/jdbc-transaction-example/
	private static final String DB_DRIVER = "org.postgresql.Driver";
	private static final String DB_CONNECTION = "YourDBConnection";
	private static final String DB_USER = "YourDBUser";
	private static final String DB_PASSWORD = "YourDBPWD";

	public static HashMap<String, List<String>> queryDiscoUserDb() throws SQLException { 
		Connection dbConnection = null;
		Statement statementSelect = null;
		HashMap<String,List<String>> stuckResources = new HashMap<String,List<String>>();

		String selectTableSQL = "SELECT a.view_nif_id, a.beta_records, a.rebuild_time, b.status, b.status_date, b.current_version " +
				"FROM lucene_view_status as a, lucene_view_status_log as b " +
				"WHERE a.cycle_id=b.cycle_id " +
				"and a.status=31 " +
				"ORDER BY b.status_date desc";
		
		try {
			dbConnection = getDBConnection();
			dbConnection.setAutoCommit(false);

			statementSelect = dbConnection.createStatement();
			ResultSet rs = statementSelect.executeQuery(selectTableSQL);
			
			while (rs.next())
			{
				String neurolexId = rs.getString(1);
				String statusDate = rs.getString("status_date"); //status_date is the name of a column in the table
				LOG.info(neurolexId+"\t"+rs.getString(2)+"\t"+statusDate);
				
				//Add values to List and then rows to HashMap, key is NIFID and values are beta_records and status_date
				List<String> values = new ArrayList<String>();
				values.add(rs.getString(2));
				values.add(statusDate);
				stuckResources.put(neurolexId, values);
			} rs.close();
			statementSelect.close();
			//System.out.println("Done!");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			dbConnection.rollback();
		} finally {
			if (statementSelect != null) {
				statementSelect.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		return stuckResources;
	}


	private static Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,
					DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}
	
}	
