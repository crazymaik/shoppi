package org.bitbrothers.shoppi.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ui.fragment.AllCategoriesFragment;
import org.bitbrothers.shoppi.ui.fragment.AllShoppingItemsFragment;
import org.bitbrothers.shoppi.ui.fragment.ShoppingListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new PagerAdapter(this, getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.pager_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private final Context context;

        public PagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
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

        @Override
        public int getCount() {
            return 3;
        }
    }

}
