package com.jobinbasani.news.ml.fragments;

import com.jobinbasani.news.ml.R;
import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.interfaces.NewsDataHandlers;
import com.jobinbasani.news.ml.provider.NewsDataContract.NewsDataEntry;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

public class NewsWidget extends Fragment {
	
	ExpandableListView newsList;
	NewsTreeAdapter mAdapter;
	NewsDataHandlers newsDataHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.news_widget, null);
		newsList = (ExpandableListView) rootView.findViewById(R.id.newsList);
		mAdapter = new NewsTreeAdapter(getActivity(), R.layout.newstext, new String[]{NewsDataEntry.COLUMN_NAME_NEWSHEADER}, new int[]{R.id.newsText}, R.layout.newstext, new String[]{NewsDataEntry.COLUMN_NAME_NEWSHEADER}, new int[]{R.id.newsText});
		newsList.setAdapter(mAdapter);
		newsDataHandler = (NewsDataHandlers) getActivity();
		return rootView;
	}
	
	public void swapMainCursor(Cursor cursor){
		mAdapter.changeCursor(cursor);
	}
	
	public void swapChildCursor(int position, Cursor cursor){
		mAdapter.setChildrenCursor(position, cursor);
	}
	
	private class NewsTreeAdapter extends SimpleCursorTreeAdapter{

		public NewsTreeAdapter(Context context, int groupLayout,
				String[] groupFrom, int[] groupTo, int childLayout,
				String[] childFrom, int[] childTo) {
			super(context, null, groupLayout, groupFrom, groupTo, childLayout, childFrom,
					childTo);
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			Bundle args = new Bundle();
			args.putString(NewsConstants.NEWSID_KEY, groupCursor.getString(groupCursor.getColumnIndex(NewsDataEntry.COLUMN_NAME_NEWSID)));
			newsDataHandler.initLoaderWithId(groupCursor.getPosition(), args);
			return null;
		}
		
	}


}
