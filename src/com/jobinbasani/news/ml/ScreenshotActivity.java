package com.jobinbasani.news.ml;


import com.jobinbasani.news.ml.constants.NewsConstants;

import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v4.app.NavUtils;

public class ScreenshotActivity extends Activity {
	
	private String scrShotFile;
	private Uri imageUri;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screenshot);
		imageUri = null;
		// Show the Up button in the action bar.
		setupActionBar();
		scrShotFile = getIntent().getStringExtra(NewsConstants.SCR_SHOT_PATH_KEY);
		if(scrShotFile!=null){
			final ImageView imgView = (ImageView) findViewById(R.id.screenshotImageView);
			
			MediaScannerConnection.scanFile(this, new String[]{scrShotFile}, null, new OnScanCompletedListener() {
				
				@Override
				public void onScanCompleted(String path, final Uri uri) {
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							imgView.setImageURI(uri);
						}
					});
					imageUri = uri;
				}
			});
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screenshot, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void shareScreenshot(View v){
		if(scrShotFile!=null){
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
			shareIntent.setType("image/png");
			startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shareLink)));
		}
	}

}
