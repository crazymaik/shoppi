package org.bitbrothers.shoppi.ui.view;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * This adapter provides empty implementations of the methods of {@link TextWatcher}.
 * Subclasses only have to implement the individual methods they care about.
 */
public class TextWatcherAdapter implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
