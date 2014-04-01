package com.jobinbasani.news.ml.service;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.jobinbasani.news.ml.MainActivity;
import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.receiver.NewsReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NewsService extends IntentService {

	public NewsService() {
		super("NewsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(MainActivity.LOG_TAG, "In intent service");
		try{
			URL url = new URL(NewsConstants.NEWS_FEED_URL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			   try {
			     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			     XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			     XmlPullParser xpp = factory.newPullParser();
			     xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			     xpp.setInput(in, "UTF-8");
			     int eventType = xpp.getEventType();
			     while(eventType != XmlPullParser.END_DOCUMENT){
			    	 if(xpp.getName()!=null && xpp.getName().equals("description") && eventType==XmlPullParser.START_TAG){
			    		 xpp.next();
			    		 String details = xpp.getText().replaceAll("&nbsp;", " ").replaceAll("&raquo;", ">>");
			    		 if(details.startsWith("<table")){
				    		 XmlPullParser detailsParser = factory.newPullParser();
				    		 detailsParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				    		 detailsParser.setInput(new StringReader(details));
				    		 int detailsEventType = detailsParser.getEventType();
				    		 String mainImage = "";
				    		 while(detailsEventType != XmlPullParser.END_DOCUMENT){
				    			 if(mainImage.length()==0 && detailsEventType == XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("img")){
				    				 mainImage = detailsParser.getAttributeValue(null, "src");
				    				 if(mainImage.startsWith("//"))
				    					 mainImage = "http:"+mainImage;
				    				 detailsParser.next();
				    				 System.out.println(mainImage);
				    				 continue;
				    			 }
				    			 detailsEventType = detailsParser.next();
				    		 }
			    		 }
			    	 }
			    	 
			    	 eventType = xpp.next();
			     }
			   }
			    finally {
			     urlConnection.disconnect();
			   }
		}catch(Exception e){
			e.printStackTrace();
		}
		NewsReceiver.completeWakefulIntent(intent);
	}

}
