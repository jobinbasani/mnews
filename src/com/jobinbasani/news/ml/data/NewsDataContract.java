package com.jobinbasani.news.ml.data;

import android.provider.BaseColumns;

public final class NewsDataContract {
	
	public NewsDataContract(){
		
	}
	
	public static abstract class NewsDataEntry implements BaseColumns{
		public static final String TABLE_NAME = "newsdata";
        public static final String COLUMN_NAME_NEWSID = "newsid";
        public static final String COLUMN_NAME_PARENTID = "parentid";
        public static final String COLUMN_NAME_NEWSHEADER = "newsheader";
        public static final String COLUMN_NAME_NEWSCATEGORY = "newscategory";
        public static final String COLUMN_NAME_NEWSDETAILS = "newsdetails";
        public static final String COLUMN_NAME_NEWSPROVIDER = "newsprovider";
        public static final String COLUMN_NAME_NEWSIMG = "newsimg";
        public static final String COLUMN_NAME_NEWSLINK = "newslink";
	}

}
