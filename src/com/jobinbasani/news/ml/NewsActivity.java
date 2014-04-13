package com.jobinbasani.news.ml;

import com.jobinbasani.news.ml.constants.NewsConstants;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class NewsActivity extends Activity {
	
	private String url;
	private ProgressBar progressBar;
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		setupActionBar();
		this.url = getIntent().getStringExtra(NewsConstants.NEWS_URL);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		//
		webView = (WebView) findViewById(R.id.webView);
		loadPage();
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
		getMenuInflater().inflate(R.menu.news, menu);
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
	
	@SuppressLint("SetJavaScriptEnabled")
	private void loadPage(){

		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				try{
					progressBar.setProgress(newProgress);
				}catch(Exception e){

				}
			}

		});
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(NewsActivity.this, description, Toast.LENGTH_SHORT).show();
			}

		}); 
		webView.loadUrl(this.url);
		//progressBar.getProgressDrawable().setColorFilter(Color.GRAY, Mode.SRC_IN);
	}


}
