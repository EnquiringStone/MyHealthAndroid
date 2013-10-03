package com.example.myhealthapp.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper{
	
	final static int DB_VERSION = 1;
	final static String DB_NAME = "myhealth.s3db";
	
	private Context context;
	     
	public Database(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	    this.context = context;
	}
	
	/**
	 * Creates, if not already exists, tables bloodpressure and pulse
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		try{
			database.execSQL("CREATE TABLE bloodpressure (timestamp TIMESTAMP PRIMARY KEY NOT NULL, "
							+ "systolic FLOAT NOT NULL DEFAULT '0.0', "
							+ "diastolic FLOAT NOT NULL DEFAULT '0.0')");
			
			database.execSQL("CREATE TABLE pulse (timestamp TIMESTAMP PRIMARY KEY NOT NULL, "
							+ "pulsevalue FLOAT NOT NULL DEFAULT '0.0')");		
		}
		catch (SQLException e){
			Log.e("Database", "Cannot create database: " + e);
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub	
	}
	
	/**
	 * Executes a SQL query without returning a result (For Inserts, Deletes and Updates)
	 * 
	 * @param query, SQL query to be executed
	 */
	public void executeQuery(String query){
		try{
			SQLiteDatabase database = this.getReadableDatabase();
			database.execSQL(query);
		}
		
		catch(SQLException e){
			Log.e("Database", "Query was not succesful: " + e);
		}
	}
	
	/**
	 * Checks if database file is created
	 * 
	 * @return boolean, dbFileExists
	 */
	public boolean databaseExists() {
		 File dbFile = context.getDatabasePath(DB_NAME);
		 return dbFile.exists();
	}
	
	/**
	 * Send a query to the database which returns a List with results from the database or null when no results are found
	 * When a parameter is not needed, use null instead
	 * 
	 * @param table, table where query is executed
	 * @param colomNames, String array with used coloms
	 * @param where, where clause
	 * @param selection, selection clause, joins etc
	 * @param groupBy, group by clause
	 * @param having, having clause
	 * @param orderBy, order by colom
	 * @param limit, limit results
	 * 
	 * @return List<HashMap<String colomName, String colomValue>>, null if no results were found
	 */
	public List<HashMap<String, String>> sendQuery(String table, String[] colomNames, String where, String[] selection, String groupBy, String having, String orderBy, String limit){
		List<HashMap<String, String>> resultArray = new ArrayList<HashMap<String, String>>();
		
		try{
			SQLiteDatabase database = this.getReadableDatabase();
			Cursor recordSet = database.query(table, colomNames, where, selection, groupBy, having, orderBy, limit);
			
			
			
			while (recordSet.moveToNext()){
				HashMap<String, String> rowMap = new HashMap<String, String>();
				
				for (int i=0; i < recordSet.getColumnCount(); i++){
					rowMap.put(recordSet.getColumnName(i), recordSet.getString(i));
				}
				
				resultArray.add(rowMap);
			}
		}
		
		catch(SQLException e){
			Log.e("Database", "Query was not succesful: " + e);
		}
		
		if (resultArray.isEmpty()){
			return null;
			
		} 
		else{
			return resultArray;
		}
		
	}

}
