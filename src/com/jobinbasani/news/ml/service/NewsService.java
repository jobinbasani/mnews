package com.jobinbasani.news.ml.service;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.jobinbasani.news.ml.MainActivity;
import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.receiver.NewsReceiver;
import com.jobinbasani.news.ml.vo.NewsItem;

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
			     ArrayList<NewsItem> newsList = new ArrayList<NewsItem>();
			     while(eventType != XmlPullParser.END_DOCUMENT){
			    	 if(xpp.getName()!=null && xpp.getName().equals("description") && eventType==XmlPullParser.START_TAG){
			    		 xpp.next();
			    		 String details = xpp.getText().replaceAll("&nbsp;", " ").replaceAll("&raquo;", ">>");
			    		 if(details.startsWith("<table")){
			    			 NewsItem mainNews = new NewsItem();
			    			 NewsItem childNews = new NewsItem();
			    			 ArrayList<NewsItem> childNewsList = new ArrayList<NewsItem>();
				    		 XmlPullParser detailsParser = factory.newPullParser();
				    		 detailsParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				    		 detailsParser.setInput(new StringReader(details));
				    		 int detailsEventType = detailsParser.getEventType();
				    		 boolean upcomingMainNewsHeader = false;
				    		 boolean upcomingMainNewsDetails = false;
				    		 boolean upcomingMainNewsProvider = false;
				    		 boolean childNewsStart = true;
				    		 int fontCounter = 0;
				    		 int linkCounter = 0;
				    		 boolean mainHeaderSection = true;
				    		 while(detailsEventType != XmlPullParser.END_DOCUMENT){
				    			 if(mainHeaderSection){
				    				 if(mainNews.getNewsImageUrl()==null && detailsEventType == XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("img")){
					    				 mainNews.setNewsImageUrl(detailsParser.getAttributeValue(null, "src"));
					    				 if(mainNews.getNewsImageUrl().startsWith("//")){
					    					 mainNews.setNewsImageUrl("http:"+mainNews.getNewsImageUrl());
					    				 }
					    				 detailsParser.next();
					    				 continue;
					    			 }
					    			 if(mainNews.getNewsLink()==null && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("a")){
					    				 linkCounter++;
					    				 if(linkCounter == 2){
					    					 mainNews.setNewsLink(detailsParser.getAttributeValue(null, "href"));
						    				 upcomingMainNewsHeader = true;
					    				 }
					    				 detailsParser.next();
					    				 continue;
					    			 }
					    			 if(upcomingMainNewsHeader && mainNews.getNewsHeader()==null && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("b")){
					    				 upcomingMainNewsHeader = false;
					    				 upcomingMainNewsDetails = true;
					    				 detailsParser.next();
					    				 mainNews.setNewsHeader(detailsParser.getText());
					    				 detailsParser.next();
					    				 continue;
					    			 }
				    				 if(upcomingMainNewsDetails && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("font")){
				    					 fontCounter++;
				    					 if(fontCounter == 2){
				    						 detailsParser.next();
				    						 mainNews.setNewsProvider(detailsParser.getText());
				    						 upcomingMainNewsDetails = false;
				    						 upcomingMainNewsProvider = true;
					    				 }
				    					 detailsParser.next();
					    				 continue;
				    				 }
				    				 
				    				 if(upcomingMainNewsProvider && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("font")){
			    						 detailsParser.next();
			    						 detailsParser.next();
			    						 detailsParser.next();
			    						 detailsParser.next();
			    						 detailsParser.next();
			    						 detailsParser.next();
			    						 mainNews.setNewsDetails(detailsParser.getText());
			    						 upcomingMainNewsProvider = false;
			    						 mainHeaderSection = false;
				    					 detailsParser.next();
					    				 continue;
				    				 }
				    			 }else{
				    				 if(childNewsStart && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("a")){
				    					 childNewsStart = false;
				    					 childNews = new NewsItem();
				    					 childNews.setNewsLink(detailsParser.getAttributeValue(null, "href"));
				    					 detailsParser.next();
				    					 childNews.setNewsHeader(detailsParser.getText());
				    				 }else if(!childNewsStart && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("nobr")){
				    					 childNewsStart = true;
				    					 detailsParser.next();
				    					 childNews.setNewsProvider(detailsParser.getText());
				    					 if(childNews.getNewsHeader()!=null && childNews.getNewsLink()!=null && childNews.getNewsProvider()!=null){
				    						 childNewsList.add(childNews);
				    					 }
				    				 }
				    			 } 
				    			  
				    			 detailsEventType = detailsParser.next();
				    		 }
				    		 mainNews.setChildNewsItems(childNewsList,true);
				    		 newsList.add(mainNews);
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
