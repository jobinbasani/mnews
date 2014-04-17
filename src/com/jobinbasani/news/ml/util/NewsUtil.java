package com.jobinbasani.news.ml.util;

import com.jobinbasani.news.ml.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class NewsUtil {

	public static void showToast(Activity activity, String message){
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
		                               (ViewGroup) activity.findViewById(R.id.toastLayoutRoot));

		TextView text = (TextView) layout.findViewById(R.id.toastText);
		text.setText(message);

		Toast toast = new Toast(activity.getApplicationContext());
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

}
