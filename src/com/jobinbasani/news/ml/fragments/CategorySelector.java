package com.jobinbasani.news.ml.fragments;

import com.jobinbasani.news.ml.R;
import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.interfaces.NewsDataHandlers;
import com.jobinbasani.news.ml.provider.NewsDataContract.NewsDataEntry;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class CategorySelector extends Fragment implements OnItemSelectedListener {
	
	Spinner categorySpinner;
	NewsDataHandlers newsDataHandler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.category_selector, null);
		categorySpinner = (Spinner) rootView.findViewById(R.id.categorySpinner);
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item, null, new String[]{NewsDataEntry.COLUMN_NAME_NEWSCATEGORY}, new int[]{android.R.id.text1}, SimpleCursorAdapter.NO_SELECTION);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(mAdapter);
		return inflater.inflate(R.layout.category_selector, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(newsDataHandler==null)
			newsDataHandler = (NewsDataHandlers) getActivity();
		newsDataHandler.initLoaderWithId(NewsConstants.CATEGORY_LOADER_ID);
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int i,
			long l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
		// TODO Auto-generated method stub
		
	}
	
	

}
