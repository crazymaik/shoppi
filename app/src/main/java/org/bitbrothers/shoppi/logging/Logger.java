package org.bitbrothers.shoppi.logging;

import android.os.Bundle;

public interface Logger {

    void logEvent(String name, Bundle params);

    void logError(String name, Bundle params);

    void logError(String name, Throwable ex);

    void logError(String name, Bundle params, Throwable ex);
}
