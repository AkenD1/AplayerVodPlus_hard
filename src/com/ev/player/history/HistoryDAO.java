package com.ev.player.history;
import com.ev.player.util.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HistoryDAO {
	
	private Logger logger = Logger.getLogger();
	private HistoryDBHelper mDBHelper;
	
	public HistoryDAO(Context context){
		mDBHelper = new HistoryDBHelper(context);
	}
	
	public boolean insert(Context context,HistoryItem item){
		SQLiteDatabase db = null;
		try{
			db = mDBHelper.getReadableDatabase();
			ContentValues values = new ContentValues();
			try{
				values.put("subcatid", item.getSubcatid());
				values.put("playIndex", item.getPlayIndex());
				values.put("playPos", item.getPlayPos());
				values.put("cid", item.getCid());
				values.put("json", item.getProgramjson());
			}catch(Exception e){
				e.printStackTrace();
			}
			logger.i("insert");
			long insertId = db.insert(HistoryConfigs.TABLE_NAME, null, values);
			if(-1 == insertId)  {
				logger.i("Add vod history fail");
				return false;
			} else {
				logger.i("Add vod history success");
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if(null != db)db.close();
			if(null != mDBHelper)mDBHelper.close();
		}
		return false;
	}
	
	/**
	 * @param subcatid  
	 */
	public void delete(String subcatid){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String sql = "delete from " + HistoryConfigs.TABLE_NAME +" where subcatid = " + subcatid;
		logger.i(sql);
		db.execSQL(sql);
		db.close();
		mDBHelper.close();
	}
	
	public HistoryItem isExist(String subcatid){
		logger.i("subcatid = " + subcatid);
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			db = mDBHelper.getReadableDatabase();
			c = db.query(HistoryConfigs.TABLE_NAME, null, " subcatid = '" + subcatid +"'", null, null,
					null, null, null);
			c.moveToFirst();
			if(c.getCount() > 0) {
				HistoryItem item = new HistoryItem();
				int _idIndex = c.getColumnIndex("_id");
				int subcatIndex = c.getColumnIndex("subcatid");
				int playIndex = c.getColumnIndex("playIndex");
				int playPosIndex = c.getColumnIndex("playPos");
				item.set_id(c.getInt(_idIndex));
				item.setSubcatid(c.getString(subcatIndex));
				item.setPlayIndex(c.getString(playIndex));
				item.setPlayPos(c.getString(playPosIndex));
				return item;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(null != c) c.close();
			if(null != db) db.close();
		}
		return null;
	}
}
