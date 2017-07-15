package com.ev.player.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ev.player.model.Model_history;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class HistoryDBHelper extends SQLiteOpenHelper {
	
	public static final int VERSION = 1;
	SQLiteDatabase db;
	
	public HistoryDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public HistoryDBHelper(Context context) {
		super(context, HistoryConfigs.DATABASE_NAME, null, VERSION);
	}
 
	@Override
	public void onCreate(SQLiteDatabase sqliteDB) {
		String createSQL = "create table if not exists " + HistoryConfigs.TABLE_NAME
				+ "(_id integer primary key autoincrement,subcatid varchar(100),playIndex varchar(100),playPos varchar(100),cid varchar(100),json text)";

		sqliteDB.execSQL(createSQL);
	}
    
	@Override
	public void onUpgrade(SQLiteDatabase sqliteDB, int arg1, int arg2) {
		String deleteSQL = "DROP TABLE IF EXISTS " + HistoryConfigs.TABLE_NAME;

		sqliteDB.execSQL(deleteSQL);
		onCreate(sqliteDB);
	}
	/**
	 * @return
	 */
	public List<Model_history> GetHistory(){
		 List<Model_history> list=new  ArrayList<Model_history>();
		try {
			db=getReadableDatabase();
			 Cursor cursor =db.rawQuery("select * from history_table order by _id desc limit 50",null); 
			 while(cursor.moveToNext()){  
		          
		            String subcatid=cursor.getString(cursor.getColumnIndex("subcatid"));  
		            String playIndex=cursor.getString(cursor.getColumnIndex("playIndex"));
		            String playPos=cursor.getString(cursor.getColumnIndex("playPos"));  
		            String cid=cursor.getString(cursor.getColumnIndex("cid"));
		            String json=cursor.getString(cursor.getColumnIndex("json"));
		            Model_history item=new Model_history();
		            item.setCid(cid);
		            item.setJson(json);
		            item.setPlayIndex(playIndex);
		            item.setPlayPos(playPos);
		            item.setSubcatid(subcatid);
		            
		            list.add(item);
		         //   Log.d("hisdb","1:"+subcatid+"--2:"+playIndex+"--3:"+playPos+"--4"+name);
		          
		            
		    }  
		        cursor.close();  
		} catch (Exception e) {
			// TODO: handle exception
		}

		return list;
	}
}
