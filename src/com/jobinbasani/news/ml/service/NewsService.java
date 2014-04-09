package com.jobinbasani.news.ml.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.provider.NewsDataContract;
import com.jobinbasani.news.ml.receiver.NewsReceiver;
import com.jobinbasani.news.ml.vo.NewsItem;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class NewsService extends IntentService {

	public NewsService() {
		super("NewsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(NewsConstants.LOG_TAG, "In intent service");
		String[] topics = {"","w","s"};
		ArrayList<NewsItem> newsCollection = new ArrayList<NewsItem>();
		long batchId = System.currentTimeMillis();
		int categoryId = 0;
		for(String topic:topics){
			newsCollection.addAll(getTopicNews(topic, batchId+"", ++categoryId+""));
		}
		boolean imgDownloadStatus = true;
		for(NewsItem mainNews:newsCollection){
			if(mainNews.getImageId()!=null){
				if(!downloadImage(mainNews.getNewsImageUrl(), mainNews.getImageId()))
					imgDownloadStatus = false;
			}
		}
		if(imgDownloadStatus){
			clearOldImages(batchId);
		}
		NewsItem newsValues = new NewsItem();
		newsValues.setChildNewsItems(newsCollection);
		getContentResolver().bulkInsert(NewsDataContract.CONTENT_URI, newsValues.getContentValueArray());
		NewsReceiver.completeWakefulIntent(intent);
	}
	
	private ArrayList<NewsItem> getTopicNews(String topic, String batchId, String categoryId){
		ArrayList<NewsItem> newsList = new ArrayList<NewsItem>();
		try{
			String feedUrl = NewsConstants.NEWS_FEED_URL;
			if(topic.length()>0)
				feedUrl = feedUrl+"&topic="+topic;
			URL url = new URL(feedUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			   try {
			     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			     XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			     XmlPullParser xpp = factory.newPullParser();
			     xpp.setInput(in, "UTF-8");
			     int eventType = xpp.getEventType();
			     String newsCategory = null;
			     while(eventType != XmlPullParser.END_DOCUMENT){
			    	 if( xpp.getName()!=null && xpp.getName().equals("category") && eventType==XmlPullParser.START_TAG){
			    		 eventType = xpp.next();
			    		 newsCategory = xpp.getText();
			    		 eventType = xpp.next();
			    		 continue;
			    	 }
			    	 if(xpp.getName()!=null && xpp.getName().equals("description") && eventType==XmlPullParser.START_TAG){
			    		 eventType = xpp.next();
			    		 String details = xpp.getText().replaceAll("&nbsp;", " ").replaceAll("&raquo;", ">>");
			    		 if(details.startsWith("<table")){
			    			 String newsId = System.currentTimeMillis()+"";
			    			 NewsItem mainNews = new NewsItem();
			    			 NewsItem childNews = new NewsItem();
			    			 ArrayList<NewsItem> childNewsList = new ArrayList<NewsItem>();
				    		 XmlPullParser detailsParser = factory.newPullParser();
				    		 detailsParser.setInput(new StringReader(details));
				    		 int detailsEventType = detailsParser.getEventType();
				    		 boolean upcomingMainNewsHeader = false;
				    		 boolean upcomingMainNewsDetails = false;
				    		 boolean upcomingMainNewsProvider = false;
				    		 boolean childNewsStart = true;
				    		 int fontCounter = 0;
				    		 int linkCounter = 0;
				    		 boolean mainHeaderSection = true;
				    		 mainNews.setNewsId(newsId);
				    		 mainNews.setBatchId(batchId);
				    		 mainNews.setCategoryId(categoryId);
				    		 if(newsCategory!=null && newsCategory.length()>0){
				    			 mainNews.setNewsCategory(newsCategory);
				    		 }
				    		 while(detailsEventType != XmlPullParser.END_DOCUMENT){
				    			 if(mainHeaderSection){
				    				 if(mainNews.getNewsImageUrl()==null && detailsEventType == XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("img")){
					    				 mainNews.setNewsImageUrl(detailsParser.getAttributeValue(null, "src"));
					    				 if(mainNews.getNewsImageUrl()!=null && mainNews.getNewsImageUrl().startsWith("//")){
					    					 mainNews.setNewsImageUrl("http:"+mainNews.getNewsImageUrl());
					    				 }
					    				 if(mainNews.getNewsImageUrl()!=null){
					    					 mainNews.setImageId("nimg"+mainNews.getNewsId()+".png");
					    				 }
					    				 detailsEventType = detailsParser.next();
					    				 continue;
					    			 }
					    			 if(mainNews.getNewsLink()==null && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("a")){
					    				 linkCounter++;
					    				 if(linkCounter == 2){
					    					 mainNews.setNewsLink(detailsParser.getAttributeValue(null, "href"));
						    				 upcomingMainNewsHeader = true;
					    				 }
					    				 detailsEventType = detailsParser.next();
					    				 continue;
					    			 }
					    			 if(upcomingMainNewsHeader && mainNews.getNewsHeader()==null && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("b")){
					    				 upcomingMainNewsHeader = false;
					    				 upcomingMainNewsProvider = true;
					    				 
					    				 detailsEventType = detailsParser.next();
					    				 mainNews.setNewsHeader(detailsParser.getText());
					    				 detailsEventType = detailsParser.next();
					    				 continue;
					    			 }
				    				 if(upcomingMainNewsProvider && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("font")){
				    					 fontCounter++;
				    					 if(fontCounter == 2){
				    						 detailsEventType = detailsParser.next();
				    						 mainNews.setNewsProvider(detailsParser.getText());
				    						 upcomingMainNewsProvider = false;
				    						 upcomingMainNewsDetails = true;
					    				 }
				    					 detailsEventType = detailsParser.next();
					    				 continue;
				    				 }
				    				 
				    				 if(upcomingMainNewsDetails && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("font")){
				    					 detailsEventType = detailsParser.next();
				    					 mainNews.setNewsDetails(detailsParser.getText());
			    						 upcomingMainNewsProvider = false;
			    						 mainHeaderSection = false;
			    						 detailsEventType = detailsParser.next();
					    				 continue;
				    				 }
				    			 }else{
				    				 if(childNewsStart && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("a")){
				    					 childNewsStart = false;
				    					 childNews = new NewsItem();
				    					 childNews.setParentId(newsId);
				    					 childNews.setNewsLink(detailsParser.getAttributeValue(null, "href"));
				    					 detailsEventType = detailsParser.next();
				    					 childNews.setNewsHeader(detailsParser.getText());
				    				 }else if(!childNewsStart && detailsEventType==XmlPullParser.START_TAG && detailsParser.getName()!=null && detailsParser.getName().equals("nobr")){
				    					 childNewsStart = true;
				    					 detailsEventType = detailsParser.next();
				    					 childNews.setNewsProvider(detailsParser.getText());
				    					 if(childNews.getNewsHeader()!=null && childNews.getNewsLink()!=null && childNews.getNewsProvider()!=null){
				    						 childNews.setBatchId(batchId);
				    						 childNews.setCategoryId(categoryId);
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
		return newsList;
	}
	
	private boolean downloadImage(String imgUrl, String imgId){
		HttpURLConnection conn = null;
		boolean status = false;
		try {
			URL url = new URL(imgUrl);
			int responseCode = -1;
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			responseCode = conn.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK){
				InputStream input = conn.getInputStream();
				Bitmap newsImg = BitmapFactory.decodeStream(input);
				FileOutputStream outStream = openFileOutput(imgId, Context.MODE_PRIVATE);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				newsImg.compress(Bitmap.CompressFormat.PNG, 100, bytes);
				outStream.write(bytes.toByteArray());
				outStream.close();
				if(newsImg!=null){
					newsImg.recycle();
					newsImg = null;
				}
				status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(conn!=null){
				conn.disconnect();
			}
		}
		return status;
	}
	
	private void clearOldImages(long referenceTimeStamp){
		FilenameFilter newsImgFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if(filename.startsWith("nimg") && filename.endsWith(".png")){
					return true;
				}
				return false;
			}
		};
		File[] newsImages = getFilesDir().listFiles(newsImgFilter);
		for(File newsImage:newsImages){
			if(newsImage.lastModified()<referenceTimeStamp)
				newsImage.delete();
		}
	}
}
