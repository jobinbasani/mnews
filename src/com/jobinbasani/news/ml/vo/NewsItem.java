package com.jobinbasani.news.ml.vo;

import java.util.ArrayList;

public class NewsItem {

	private String newsId;
	private String newsHeader;
	private String newsDetails;
	private String newsImageUrl;
	private String imageId;
	private String newsSavedImageName;
	private String parentId;
	private String newsLink;
	private String newsProvider;
	private ArrayList<NewsItem> childNewsItems;
	
	public String getNewsId() {
		return newsId;
	}
	public void setNewsId(String newsId) {
		this.newsId = newsId;
	}
	public String getNewsHeader() {
		return newsHeader;
	}
	public void setNewsHeader(String newsHeader) {
		if(newsHeader!=null)
			this.newsHeader = newsHeader;
		else
			this.newsHeader = " ";
	}
	public String getNewsDetails() {
		return newsDetails;
	}
	public void setNewsDetails(String newsDetails) {
		this.newsDetails = newsDetails;
	}
	public String getNewsImageUrl() {
		return newsImageUrl;
	}
	public void setNewsImageUrl(String newsImageUrl) {
		this.newsImageUrl = newsImageUrl;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getNewsSavedImageName() {
		return newsSavedImageName;
	}
	public void setNewsSavedImageName(String newsSavedImageName) {
		this.newsSavedImageName = newsSavedImageName;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public ArrayList<NewsItem> getChildNewsItems() {
		return childNewsItems;
	}
	public String getNewsLink() {
		return newsLink;
	}
	public void setNewsLink(String newsLink) {
		this.newsLink = newsLink;
	}
	public String getNewsProvider() {
		return newsProvider;
	}
	public void setNewsProvider(String newsProvider) {
		this.newsProvider = newsProvider;
	}
	public void setChildNewsItems(ArrayList<NewsItem> childNewsItems) {
		this.childNewsItems = childNewsItems;
	}
	public void setChildNewsItems(ArrayList<NewsItem> childNewsItems, boolean addMainNewsItem) {
		if(addMainNewsItem){
			NewsItem childItem = new NewsItem();
			childItem.setNewsHeader(this.newsHeader);
			childItem.setNewsLink(this.newsLink);
			childItem.setNewsProvider(this.newsProvider);
			childNewsItems.add(childItem);
			setChildNewsItems(childNewsItems);
		}else{
			setChildNewsItems(childNewsItems);
		}
	}
	@Override
	public String toString() {
		return "newsId="+newsId+", newsHeader="+newsHeader+", newsDetails="+newsDetails+", newsLink="+newsLink+", newsProvider="+newsProvider;
	}

}
