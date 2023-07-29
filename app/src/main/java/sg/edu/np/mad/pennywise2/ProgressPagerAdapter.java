package sg.edu.np.mad.pennywise2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import sg.edu.np.mad.pennywise2.fragments.CardViewFragment;
import sg.edu.np.mad.pennywise2.fragments.LineChartFragment;
import sg.edu.np.mad.pennywise2.fragments.ProgressBarFragment;

public class ProgressPagerAdapter extends FragmentStateAdapter {
    private String uid;
    private static final int NUM_PAGES = 3;

    public ProgressPagerAdapter(@NonNull FragmentActivity fragmentActivity, String uid) {
        super(fragmentActivity);
        this.uid = uid;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProgressBarFragment(uid);
            case 1:
                return new LineChartFragment(uid);
            case 2:
                return new CardViewFragment(uid);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
