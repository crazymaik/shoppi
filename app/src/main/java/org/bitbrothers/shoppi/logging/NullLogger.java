package org.bitbrothers.shoppi.logging;

import android.os.Bundle;

public class NullLogger implements Logger {

    @Override
    public void logEvent(String name, Bundle params) {
    }

    @Override
    public void logError(String name, Bundle params) {
    }

    @Override
    public void logError(String name, Throwable ex) {
    }

    @Override
    public void logError(String name, Bundle params, Throwable ex) {
    }
}
