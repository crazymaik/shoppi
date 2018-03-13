package org.bitbrothers.shoppi.logging;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class ProdLogger implements Logger {

    private final FirebaseAnalytics analytics;

    public ProdLogger(Context context) {
        analytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void logEvent(String name, Bundle params) {
        analytics.logEvent(name, params);
    }

    @Override
    public void logError(String name, Bundle params) {
        analytics.logEvent("error_" + name, params);
    }

    @Override
    public void logError(String name, Throwable ex) {
        logError(name, new Bundle(), ex);
    }

    @Override
    public void logError(String name, Bundle params, Throwable ex) {
        params.putString("exception_class", ex != null ? ex.getClass().getCanonicalName() : "null");
        params.putString("exception_message", ex != null ? ex.getMessage() : "null");
        analytics.logEvent("error_" + name, params);
    }
}
