package com.jobinbasani.news.ml;

import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.fragments.CategorySelector;
import com.jobinbasani.news.ml.fragments.NewsWidget;
import com.jobinbasani.news.ml.interfaces.NewsDataHandlers;
import com.jobinbasani.news.ml.provider.NewsDataContract;
import com.jobinbasani.news.ml.provider.NewsDataContract.NewsDataEntry;
import com.jobinbasani.news.ml.receiver.NewsReceiver;
import com.jobinbasani.news.ml.util.NewsUtil;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends Activity implements LoaderCallbacks<Cursor>, NewsDataHandlers {
	
	CategorySelector categorySelector;
	NewsWidget newsWidget;
	SharedPreferences prefs;
	private ImageView refreshIconView;
	private MenuItem refreshMenuItem;
	Animation rotation;
	boolean isLoading = false;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			afterRefresh(intent.getBooleanExtra(NewsConstants.FIRST_LOAD, false));
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences(NewsConstants.PREFS_FILE, MODE_PRIVATE);
		rotation = AnimationUtils.loadAnimation(this, R.anim.rotation_animation);
		rotation.setRepeatCount(Animation.INFINITE);
		long lastLoaded = prefs.getLong(NewsConstants.LAST_LOADED, 0);
		if(lastLoaded == 0){
			setContentView(R.layout.splashscreen_layout);
			getActionBar().hide();
		}else{
			setContentView(R.layout.activity_main);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(NewsConstants.NEWS_REFRESH_ACTION));
		long lastLoaded = prefs.getLong(NewsConstants.LAST_LOADED, 0);
		if(lastLoaded == 0){
			refreshNews(true);
		}else if(System.currentTimeMillis()-lastLoaded>300000){
			invalidateOptionsMenu();
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					refreshNews(false);
				}
			}, 200);
		}
		
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onStop();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(refreshMenuItem==null){
			refreshMenuItem = menu.findItem(R.id.action_refresh);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		refreshIconView = (ImageView) getLayoutInflater().inflate(R.layout.refresh_layout, null);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.action_refresh:
			if(refreshMenuItem==null)refreshMenuItem = item;
			refreshNews(false);
			break;
		}
		
		return true;
	}

	private void refreshNews(boolean firstLoad){
		if(!isLoading){
			isLoading = true;
			Intent newsIntent = new Intent(this, NewsReceiver.class);
			newsIntent.putExtra(NewsConstants.FIRST_LOAD, firstLoad);
			sendBroadcast(newsIntent);
			if(!firstLoad){
				if(refreshMenuItem!=null){
				refreshIconView.startAnimation(rotation);
				refreshMenuItem.setActionView(refreshIconView);
				}
			}
		}
	}
	private void afterRefresh(boolean firstLoad){
		isLoading = false;
		if(firstLoad){
			setContentView(R.layout.activity_main);
			getActionBar().show();
		}else{
			NewsUtil.showToast(this, "Refresh complete!");
			resetAndLoadList();
			if(refreshMenuItem!=null){
			refreshMenuItem.getActionView().clearAnimation();
			refreshMenuItem.setActionView(null);
			}
		}
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(NewsConstants.LAST_LOADED, System.currentTimeMillis());
		editor.commit();
	}
	
	private void resetAndLoadList(){
		newsWidget.swapMainCursor(null);
		categorySelector.changeSpinnerCursor(null);
		initLoaderWithId(NewsConstants.CATEGORY_LOADER_ID, null);
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
			return new CursorLoader(MainActivity.this, NewsDataContract.CONTENT_URI_MAINNEWS, new String[]{NewsDataEntry._ID,NewsDataEntry.COLUMN_NAME_NEWSHEADER,NewsDataEntry.COLUMN_NAME_NEWSDETAILS,NewsDataEntry.COLUMN_NAME_NEWSIMG,NewsDataEntry.COLUMN_NAME_NEWSID}, NewsDataEntry.COLUMN_NAME_NEWSID+" is not null and "+NewsDataEntry.COLUMN_NAME_CATEGORYID+"=?", new String[]{categoryId}, null);
		default:
			String newsId = args.getString(NewsConstants.NEWSID_KEY, "0");
			return new CursorLoader(MainActivity.this, NewsDataContract.CONTENT_URI_CHILDNEWS, new String[]{NewsDataEntry._ID,NewsDataEntry.COLUMN_NAME_NEWSHEADER,NewsDataEntry.COLUMN_NAME_NEWSPROVIDER,NewsDataEntry.COLUMN_NAME_NEWSLINK}, NewsDataEntry.COLUMN_NAME_PARENTID+"=?", new String[]{newsId}, null);
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
