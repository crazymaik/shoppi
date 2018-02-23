package org.bitbrothers.shoppi.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

public class AddShoppingItemDialogFragment extends AppCompatDialogFragment {

    public static AddShoppingItemDialogFragment newInstance() {
        final AddShoppingItemDialogFragment fragment = new AddShoppingItemDialogFragment();
        return fragment;
    }

    private AddShoppingItemAsyncTask addShoppingItemAsyncTask;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.add_shopping_item_title);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.add_shopping_item_positive_button, null);
        builder.setNegativeButton(R.string.add_shopping_item_negative_button, null);

        builder.setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_add_shopping_item, null));

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                configureButtonBehavior((AlertDialog) dialog);
            }
        });

        return dialog;
    }

    @Override
    public void onDestroy() {
        cancelAddShoppingItem();
        super.onDestroy();
    }

    private void configureButtonBehavior(final AlertDialog dialog) {
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        final Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        final EditText editText = dialog.findViewById(android.R.id.edit);

        positiveButton.setEnabled(false);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                positiveButton.setEnabled(s.length() > 0);
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setEnabled(false);
                positiveButton.setEnabled(false);
                addShoppingItem(new ShoppingItem(editText.getText().toString().trim()));
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void addShoppingItem(ShoppingItem shoppingItem) {
        ShoppingItemRepository shoppingItemRepository = ShoppiApplication.from(getContext()).getShoppingItemRepository();
        addShoppingItemAsyncTask = new AddShoppingItemAsyncTask(shoppingItemRepository, new AddShoppingItemAsyncTask.Callback() {
            @Override
            public void onSucceeded(ShoppingItem shoppingItem) {
                AddShoppingItemDialogFragment.this.dismiss();
                addShoppingItemAsyncTask = null;
            }
        });
        addShoppingItemAsyncTask.execute(shoppingItem);
    }

    private void cancelAddShoppingItem() {
        if (addShoppingItemAsyncTask != null) {
            addShoppingItemAsyncTask.destroy();
            addShoppingItemAsyncTask = null;
        }
    }

    private static class AddShoppingItemAsyncTask extends AsyncTask<ShoppingItem, Void, ShoppingItem> {

        private final ShoppingItemRepository repository;
        private Callback callback;

        interface Callback {

            void onSucceeded(ShoppingItem shoppingItem);

            //void onFailed();
        }

        public AddShoppingItemAsyncTask(ShoppingItemRepository repository, Callback callback) {
            this.repository = repository;
            this.callback = callback;
        }

        void destroy() {
            callback = null;
            cancel(false);
        }

        @Override
        protected ShoppingItem doInBackground(ShoppingItem... shoppingItems) {
            return repository.create(shoppingItems[0]);
        }

        @Override
        protected void onPostExecute(ShoppingItem shoppingItem) {
            callback.onSucceeded(shoppingItem);
        }
    }
}
