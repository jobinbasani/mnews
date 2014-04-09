package com.jobinbasani.news.ml.provider;

import com.jobinbasani.news.ml.provider.NewsDataContract.NewsDataEntry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NewsContentProvider extends ContentProvider {
	
	private NewsDbHelper dbHelper = null;
	private static UriMatcher URI_MATCHER;
	private static final int NEWS_ADD = 1;
	private static final int NEWS_CATEGORIES = 2;
	private static final int NEWS_MAIN = 3;
	private static final int NEWS_CHILD = 4;
	
	static{
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(NewsDataContract.AUTHORITY, "add", NEWS_ADD);
		URI_MATCHER.addURI(NewsDataContract.AUTHORITY, "categories", NEWS_CATEGORIES);
		URI_MATCHER.addURI(NewsDataContract.AUTHORITY, "mainnews", NEWS_MAIN);
		URI_MATCHER.addURI(NewsDataContract.AUTHORITY, "childnews/#", NEWS_CHILD);
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int rowsAdded = 0;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		
		try{
			for(ContentValues value:values){
				db.insert(NewsDataEntry.TABLE_NAME, null, value);
				rowsAdded++;
			}
			
			db.setTransactionSuccessful();
		}catch(Exception e){
			
		}finally{
			db.endTransaction();
		}
		return rowsAdded;
	}

	@Override
	public boolean onCreate() {
		dbHelper = new NewsDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
