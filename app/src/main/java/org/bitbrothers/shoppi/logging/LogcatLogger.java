package org.bitbrothers.shoppi.logging;

import android.os.Bundle;
import android.util.Log;

public class LogcatLogger implements Logger {

    @Override
    public void logEvent(String name, Bundle params) {
        Log.i("Event", String.format("Name: %s\nParams: %s", name, params.toString()));
    }

    @Override
    public void logError(String name, Bundle params) {
        Log.e("Event", String.format("Error: %s\nParams: %s", name, params.toString()));
    }

    @Override
    public void logError(String name, Throwable ex) {
        Log.e("Event", String.format("Error: %s\nException: %s", name, ex.toString()));
    }

    @Override
    public void logError(String name, Bundle params, Throwable ex) {
        Log.e("Event", String.format("Error: %s\nException: %s\nParams: %s", name, ex.toString(), params.toString()));
    }
}
