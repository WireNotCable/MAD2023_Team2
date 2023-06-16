package sg.edu.np.mad.pennywise;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import sg.edu.np.mad.pennywise.fragments.Mobile;
import sg.edu.np.mad.pennywise.fragments.NRICFIN;
import sg.edu.np.mad.pennywise.fragments.UEN;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new Mobile();
            case 1:
                return new NRICFIN();
            case 2:
                return new UEN();
            default:
                return new Mobile();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
