package com.wots.lutmaar.UtilClass;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.StandardExceptionParser;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class AnalyticsExceptionParser extends StandardExceptionParser{


	public AnalyticsExceptionParser(Context context,
									Collection<String> additionalPackages) {
		super(context, additionalPackages);
	}

	@Override
	public String getDescription(String threadName, Throwable t) {
		return getDescription(getCause(t), getBestStackTraceElement(getCause(t)), threadName);
	}

	protected String getDescription(Throwable cause, StackTraceElement element, String threadName) {
		StringBuilder descriptionBuilder = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		String exceptionTrace = "DateTime: " + currentDateandTime + ", Crash :" + String.format("Thread: %s, UncaughtException: %s", threadName, Log.getStackTraceString(cause));
		//return String.format("Thread: %s, Exception: %s", thread,Log.getStackTraceString(throwable));
		Log.e("Crash", exceptionTrace);
		return exceptionTrace;

	}
	
}
