package com.jobinbasani.news.ml.fragments;

import java.io.File;

import com.jobinbasani.news.ml.R;
import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.interfaces.NewsDataHandlers;
import com.jobinbasani.news.ml.provider.NewsDataContract.NewsDataEntry;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.SimpleCursorTreeAdapter.ViewBinder;

public class NewsWidget extends Fragment {
	
	ExpandableListView newsList;
	NewsTreeAdapter mAdapter;
	NewsDataHandlers newsDataHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.news_widget, null);
		newsList = (ExpandableListView) rootView.findViewById(R.id.newsList);
		String[] groupFrom = new String[]{NewsDataEntry.COLUMN_NAME_NEWSHEADER,NewsDataEntry.COLUMN_NAME_NEWSDETAILS,NewsDataEntry.COLUMN_NAME_NEWSIMG};
		int[] groupTo = new int[]{R.id.mainNewsHeader,R.id.mainNewsDetails, R.id.mainNewsImage};
		String[] childFrom = new String[]{NewsDataEntry.COLUMN_NAME_NEWSHEADER,NewsDataEntry.COLUMN_NAME_NEWSPROVIDER};
		int[] childTo = new int[]{R.id.childNewsHeader,R.id.childNewsProvider};
		mAdapter = new NewsTreeAdapter(getActivity(), R.layout.mainnews_layout, groupFrom, groupTo, R.layout.childnews_layout, childFrom, childTo);
		mAdapter.setViewBinder(new NewsViewBinder());
		newsList.setAdapter(mAdapter);
		newsDataHandler = (NewsDataHandlers) getActivity();
		return rootView;
	}
	
	public void swapMainCursor(Cursor cursor){
		mAdapter.changeCursor(cursor);
	}
	
	public void swapChildCursor(int position, Cursor cursor){
		if(mAdapter.getGroupCount()>0)
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

	private class NewsViewBinder implements ViewBinder{

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if(view.getId()==R.id.mainNewsImage){
				ImageView imgView = (ImageView) view;
				String filePath = getActivity().getFilesDir().getAbsolutePath()+File.separatorChar+cursor.getString(columnIndex);
				imgView.setImageURI(Uri.fromFile(new File(filePath)));
				return true;
			}
			return false;
		}
		
	}
}
