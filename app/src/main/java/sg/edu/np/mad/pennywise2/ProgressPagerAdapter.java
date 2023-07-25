package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import sg.edu.np.mad.pennywise.fragments.CardViewFragment;
import sg.edu.np.mad.pennywise.fragments.LineChartFragment;
import sg.edu.np.mad.pennywise.fragments.ProgressBarFragment;

public class ProgressPagerAdapter extends FragmentStateAdapter {
    private ArrayList<IndivisualGoalI> progressList;
    private static final int NUM_PAGES = 3;

    public ProgressPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<IndivisualGoalI> progressList) {
        super(fragmentActivity);
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProgressBarFragment();
            case 1:
                return new LineChartFragment();
            case 2:
                return new CardViewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
