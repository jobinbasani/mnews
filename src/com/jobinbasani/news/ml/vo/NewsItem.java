package com.jobinbasani.news.ml.vo;

import java.util.ArrayList;

public class NewsItem {

	private String newsId;
	private String newsHeader;
	private String newsDetails;
	private String newsImageUrl;
	private String newsSavedImageName;
	private String parentId;
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
		this.newsHeader = newsHeader;
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
	public void setChildNewsItems(ArrayList<NewsItem> childNewsItems) {
		this.childNewsItems = childNewsItems;
	}

}
