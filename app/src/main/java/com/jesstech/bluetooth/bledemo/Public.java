package com.jesstech.bluetooth.bledemo;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class Public {
	public static boolean  b_ascii_mode = true;

	//////////////////////////
    public static void ShowAlert(String title, String msg, Context context) {
	    new AlertDialog.Builder(context)
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setTitle(title)
	    .setMessage(msg)
	    .setCancelable(false)
	    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        })
	    .show();
    }

    public static void ShowInfo(String title, String msg, Context context) {
	    new AlertDialog.Builder(context)
	    .setIcon(android.R.drawable.ic_dialog_info)
	    .setTitle(title)
	    .setMessage(msg)
	    .setCancelable(false)
	    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        })
	    .show();
    }

    public static boolean is_hex_char(String str) {
    	for (int i=0; i<str.length(); i++) {
    		char c = str.charAt(i);

    		if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
    			return false;
    		}
    	}
    	return true;
    }

    public static String get_date() {
    	int mYear;
    	int mMonth;
    	int mDay;

    	final Calendar c = Calendar.getInstance();

    	mYear = c.get(Calendar.YEAR);
    	mMonth = c.get(Calendar.MONTH) + 1;
    	mDay = c.get(Calendar.DAY_OF_MONTH);

    	String result = String.format("%04d-%02d-%02d", mYear, mMonth, mDay);
    	return result;
    }

    public static String get_date_time() {
    	int mYear;
    	int mMonth;
    	int mDay;
    	int mHour;
    	int mMinute;
    	int mSecond;

    	final Calendar c = Calendar.getInstance();

    	mYear = c.get(Calendar.YEAR);
    	mMonth = c.get(Calendar.MONTH) + 1;
    	mDay = c.get(Calendar.DAY_OF_MONTH);
    	mHour = c.get(Calendar.HOUR_OF_DAY);
    	mMinute = c.get(Calendar.MINUTE);
    	mSecond = c.get(Calendar.SECOND);

    	String result = String.format("%04d-%02d-%02d %02d:%02d:%02d",
    			mYear, mMonth, mDay, mHour, mMinute, mSecond);
    	return result;
    }

    public static String get_date_time_for_filename() {
    	int mYear;
    	int mMonth;
    	int mDay;
    	int mHour;
    	int mMinute;
    	int mSecond;

    	final Calendar c = Calendar.getInstance();

    	mYear = c.get(Calendar.YEAR);
    	mMonth = c.get(Calendar.MONTH) + 1;
    	mDay = c.get(Calendar.DAY_OF_MONTH);
    	mHour = c.get(Calendar.HOUR_OF_DAY);
    	mMinute = c.get(Calendar.MINUTE);
    	mSecond = c.get(Calendar.SECOND);

    	String result = String.format("%04d-%02d-%02d_%02d.%02d.%02d",
    			mYear, mMonth, mDay, mHour, mMinute, mSecond);
    	return result;
    }

	//bitmap缩放
	public static Bitmap bitmapZoom(Bitmap srcBitmap, int newWidth, int newHeight) {
		int srcWidth = srcBitmap.getWidth();
		int srcHeight = srcBitmap.getHeight();

		float scaleWidth = ((float) newWidth) / srcWidth;
		float scaleHeight = ((float) newHeight) / srcHeight;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth,
				srcHeight, matrix, true);
		if (resizedBitmap != null) {
			return resizedBitmap;
		} else {
			return srcBitmap;
		}
	}

	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "1.0";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}

}
