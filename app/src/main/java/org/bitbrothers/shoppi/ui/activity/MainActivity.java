package org.bitbrothers.shoppi.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.bitbrothers.shoppi.BuildConfig;
import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ui.fragment.AllCategoriesFragment;
import org.bitbrothers.shoppi.ui.fragment.AllShoppingItemsFragment;
import org.bitbrothers.shoppi.ui.fragment.ShoppingListFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    private final List<WeakReference<OnBackPressedListener>> onBackPressedListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new PagerAdapter(this, getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        TabLayout tabLayout = findViewById(R.id.pager_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void addOnBackPressedListener(OnBackPressedListener listener) {
        onBackPressedListeners.add(new WeakReference<>(listener));
    }

    public void removeOnBackPressedListener(OnBackPressedListener removableListener) {
        for (int i = onBackPressedListeners.size() - 1; i >= 0; --i) {
            OnBackPressedListener listener = onBackPressedListeners.get(i).get();
            if (listener != null) {
                if (listener == removableListener) {
                    onBackPressedListeners.remove(i);
                }
            } else {
                onBackPressedListeners.remove(i);
            }
        }
    }

    @Override
    public void onBackPressed() {
        for (int i = onBackPressedListeners.size() - 1; i >= 0; --i) {
            OnBackPressedListener listener = onBackPressedListeners.get(i).get();
            if (listener != null) {
                if (listener.onBackPressed()) {
                    return;
                }
            } else {
                onBackPressedListeners.remove(i);
            }
        }

        super.onBackPressed();
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {

        private final Context context;
        private final FirebaseAnalytics firebaseAnalytics;
        private int previousPrimaryPosition = -1;

        public PagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (previousPrimaryPosition != position) {
                Bundle bundle = new Bundle();
                bundle.putString("name", positionToAnalyticsName(position));
                bundle.putString("previous", positionToAnalyticsName(previousPrimaryPosition));
                firebaseAnalytics.logEvent("page_view", bundle);
                previousPrimaryPosition = position;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ShoppingListFragment();
                case 1:
                    return new AllShoppingItemsFragment();
                case 2:
                    return new AllCategoriesFragment();
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getString(R.string.tab_shopping_list);
                case 1:
                    return context.getString(R.string.tab_all_items);
                case 2:
                    return context.getString(R.string.tab_all_categories);
                default:
                    throw new IllegalArgumentException();
            }
        }

        private String positionToAnalyticsName(int position) {
            switch (position) {
                case 0:
                    return "shopping_list";
                case 1:
                    return "all_items";
                case 2:
                    return "all_categories";
                default:
                    return "unknown";
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
