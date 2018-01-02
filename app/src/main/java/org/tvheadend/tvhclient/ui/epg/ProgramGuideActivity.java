package org.tvheadend.tvhclient.ui.epg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.tvheadend.tvhclient.R;
import org.tvheadend.tvhclient.data.DatabaseHelper;
import org.tvheadend.tvhclient.ui.NavigationActivity;
import org.tvheadend.tvhclient.ui.NavigationDrawer;
import org.tvheadend.tvhclient.ui.NavigationDrawerCallback;
import org.tvheadend.tvhclient.ui.base.ToolbarInterface;
import org.tvheadend.tvhclient.utils.MiscUtils;

import static org.tvheadend.tvhclient.ui.NavigationDrawer.MENU_PROGRAM_GUIDE;

// TODO search
// TODO refresh
// TODO extend draweractivity
// TODO back press shows the same activity

public class ProgramGuideActivity extends AppCompatActivity implements FragmentScrollInterface, ToolbarInterface, NavigationDrawerCallback {

    private SharedPreferences sharedPreferences;
    private NavigationDrawer navigationDrawer;
    private DatabaseHelper databaseHelper;
    private int programGuideListPosition = 0;
    private int programGuideListPositionOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MiscUtils.getThemeId(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        MiscUtils.setLanguage(this);

        // Get the main toolbar and the floating action button (fab). The fab is
        // hidden as a default and only visible when required for certain actions
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        navigationDrawer = new NavigationDrawer(this, savedInstanceState, toolbar, this);
        navigationDrawer.createHeader();
        navigationDrawer.createMenu();
        navigationDrawer.getDrawer().setSelection(MENU_PROGRAM_GUIDE, false);

        if (savedInstanceState == null) {
            ProgramGuidePagerFragment fragment = new ProgramGuidePagerFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.main, fragment).commit();
        }

        navigationDrawer.updateDrawerHeader();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // add the values which need to be saved from the drawer to the bundle
        outState = navigationDrawer.getDrawer().saveInstanceState(outState);
        // add the values which need to be saved from the accountHeader to the bundle
        outState = navigationDrawer.getHeader().saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNavigationMenuSelected(int id) {
        if (id != MENU_PROGRAM_GUIDE) {
            Intent intent = new Intent(this, NavigationActivity.class);
            intent.putExtra("navigation_menu_position", id);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onScrollingChanged(final int position, final int offset, final String tag) {
        // Save the scroll values so they can be reused after an orientation change.
        programGuideListPosition = position;
        programGuideListPositionOffset = offset;

        if (tag.equals(ProgramGuideChannelListFragment.class.getSimpleName())
                || tag.equals(ProgramGuideListFragment.class.getSimpleName())) {
            // Scrolling was initiated by the channel or program guide list fragment. Keep
            // the currently visible program guide list in sync by scrolling it to the same position
            final Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if (f instanceof FragmentControlInterface) {
                ((FragmentControlInterface) f).setSelection(position, offset);
            }
        }
    }

    @Override
    public void onScrollStateIdle(final String tag) {
        if (tag.equals(ProgramGuideListFragment.class.getSimpleName())
                || tag.equals(ProgramGuideChannelListFragment.class.getSimpleName())) {
            // Scrolling stopped by the program guide or the channel list
            // fragment. Scroll all program guide fragments in the current
            // view pager to the same position.
            final Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if (f instanceof ProgramGuidePagerFragment) {
                ((FragmentControlInterface) f).setSelection(
                        programGuideListPosition,
                        programGuideListPositionOffset);
            }
        }
    }

    @Override
    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void setSubtitle(String subtitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }
}
