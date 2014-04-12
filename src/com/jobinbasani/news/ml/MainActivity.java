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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements LoaderCallbacks<Cursor>, NewsDataHandlers {
	
	CategorySelector categorySelector;
	NewsWidget newsWidget;
	private ImageView refreshIconView;
	Animation rotation;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			afterRefresh();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rotation = AnimationUtils.loadAnimation(this, R.anim.rotation_animation);
		rotation.setRepeatCount(Animation.INFINITE);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(NewsConstants.NEWS_REFRESH_ACTION));
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		refreshIconView = (ImageView) menu.findItem(R.id.action_refresh).getActionView();
		refreshIconView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshNews();
			}
		});
		return true;
	}

	private void refreshNews(){
		sendBroadcast(new Intent(this, NewsReceiver.class));
		refreshIconView.startAnimation(rotation);
	}
	private void afterRefresh(){
		
		Toast.makeText(this, "News Refreshed", Toast.LENGTH_LONG).show();
		resetAndLoadList();
		refreshIconView.clearAnimation();
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
			return new CursorLoader(MainActivity.this, NewsDataContract.CONTENT_URI_CHILDNEWS, new String[]{NewsDataEntry._ID,NewsDataEntry.COLUMN_NAME_NEWSHEADER,NewsDataEntry.COLUMN_NAME_NEWSPROVIDER}, NewsDataEntry.COLUMN_NAME_PARENTID+"=?", new String[]{newsId}, null);
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
