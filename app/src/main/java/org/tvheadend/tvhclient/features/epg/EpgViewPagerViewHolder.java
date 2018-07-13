package org.tvheadend.tvhclient.features.epg;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.tvheadend.tvhclient.R;
import org.tvheadend.tvhclient.data.entity.Program;
import org.tvheadend.tvhclient.features.programs.ProgramDetailsActivity;
import org.tvheadend.tvhclient.features.shared.callbacks.RecyclerViewClickCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class EpgViewPagerViewHolder extends RecyclerView.ViewHolder implements RecyclerViewClickCallback {

    private final EpgProgramListRecyclerViewAdapter programListRecyclerViewAdapter;
    private final FragmentActivity activity;
    @BindView(R.id.program_list_recycler_view)
    protected RecyclerView programListRecyclerView;

    EpgViewPagerViewHolder(FragmentActivity activity, View view, float pixelsPerMinute, long fragmentStartTime, long fragmentStopTime) {
        super(view);
        ButterKnife.bind(this, view);

        this.activity = activity;
        programListRecyclerView.setLayoutManager(new CustomHorizontalLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        programListRecyclerView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.HORIZONTAL));
        programListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        programListRecyclerViewAdapter = new EpgProgramListRecyclerViewAdapter(activity, pixelsPerMinute,fragmentStartTime, fragmentStopTime, this);
        programListRecyclerView.setAdapter(programListRecyclerViewAdapter);
    }

    public void bindData(List<Program> programs) {
        //Timber.d("Adding " + (programs != null ? programs.size() : 0) + " programs");
        programListRecyclerViewAdapter.addItems(programs);
    }

    @Override
    public void onClick(View view, int position) {
        Timber.d("onClick at " + position);
        Program program = programListRecyclerViewAdapter.getItem(position);
        if (program == null) {
            return;
        }
        Timber.d("Found program " + program.getTitle());
        // Launch a new activity to display the program list of the selected channelTextView.
        Intent intent = new Intent(activity, ProgramDetailsActivity.class);
        intent.putExtra("eventId", program.getEventId());
        intent.putExtra("channelId", program.getChannelId());
        activity.startActivity(intent);
    }

    @Override
    public void onLongClick(View view, int position) {
        Timber.d("onLongClick at " + position);
        final Program program = (Program) view.getTag();
        if (program == null) {
            return;
        }
        Timber.d("Found program " + program.getTitle());

        Fragment fragment = activity.getSupportFragmentManager().findFragmentById(R.id.main);
        if (fragment != null
                && fragment.isAdded()
                && fragment.isResumed()
                && fragment instanceof ProgramGuideFragment) {
            ((ProgramGuideFragment) fragment).showPopupMenu(view, program);
        }
    }
}