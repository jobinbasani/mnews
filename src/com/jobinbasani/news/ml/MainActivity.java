package com.jobinbasani.news.ml;

import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.fragments.CategorySelector;
import com.jobinbasani.news.ml.fragments.NewsWidget;
import com.jobinbasani.news.ml.interfaces.NewsDataHandlers;
import com.jobinbasani.news.ml.provider.NewsDataContract;
import com.jobinbasani.news.ml.provider.NewsDataContract.NewsDataEntry;
import com.jobinbasani.news.ml.receiver.NewsReceiver;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements LoaderCallbacks<Cursor>, NewsDataHandlers {
	
	CategorySelector categorySelector;
	NewsWidget newsWidget;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void launchService(View v){
		Log.d(NewsConstants.LOG_TAG, "In btn click");
		Intent bIntent = new Intent(this, NewsReceiver.class);
		sendBroadcast(bIntent);
		Log.d(NewsConstants.LOG_TAG, "After btn click");
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if(categorySelector==null){
			categorySelector = (CategorySelector) getFragmentManager().findFragmentByTag(getResources().getString(R.string.categorySelectorTag));
		}
		if(newsWidget==null){
			newsWidget = (NewsWidget) getFragmentManager().findFragmentByTag(getResources().getString(R.string.newsWidgetTag));
		}
		switch(id){
		case NewsConstants.CATEGORY_LOADER_ID:
			return new CursorLoader(MainActivity.this, NewsDataContract.CONTENT_URI_CATEGORIES, null, null, null, null);
		case NewsConstants.NEWSGROUP_LOADER_ID:
			String categoryId = args.getInt(NewsConstants.CATEGORY_KEY, 0)+"";
			return new CursorLoader(MainActivity.this, NewsDataContract.CONTENT_URI_MAINNEWS, new String[]{NewsDataEntry._ID,NewsDataEntry.COLUMN_NAME_NEWSHEADER,NewsDataEntry.COLUMN_NAME_NEWSID}, NewsDataEntry.COLUMN_NAME_NEWSID+" is not null and "+NewsDataEntry.COLUMN_NAME_CATEGORYID+"=?", new String[]{categoryId}, null);
		default:
			String newsId = args.getString(NewsConstants.NEWSID_KEY, "0");
			return new CursorLoader(MainActivity.this, NewsDataContract.CONTENT_URI_CHILDNEWS, new String[]{NewsDataEntry._ID,NewsDataEntry.COLUMN_NAME_NEWSHEADER}, NewsDataEntry.COLUMN_NAME_PARENTID+"=?", new String[]{newsId}, null);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()){
		case NewsConstants.CATEGORY_LOADER_ID:
			categorySelector.changeSpinnerCursor(cursor);
			break;
		case NewsConstants.NEWSGROUP_LOADER_ID:
			newsWidget.swapMainCursor(cursor);
			break;
		default:
			System.out.println("child="+cursor.getCount());
			newsWidget.swapChildCursor(loader.getId(), cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch(loader.getId()){
		case NewsConstants.CATEGORY_LOADER_ID:
			categorySelector.changeSpinnerCursor(null);
			break;
		case NewsConstants.NEWSGROUP_LOADER_ID:
			newsWidget.swapMainCursor(null);
			break;
		default:
			newsWidget.swapChildCursor(loader.getId(), null);
		}
	}

	@Override
	public void initLoaderWithId(int loaderId, Bundle args) {
		if(getLoaderManager().getLoader(loaderId)!=null){
			getLoaderManager().restartLoader(loaderId, args, this);
		}else{
			getLoaderManager().initLoader(loaderId, args, this);
		}
	}

}
