package at.devfest.app.ui.venue;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import at.devfest.app.R;

/**
 * Created by helmuth on 10/09/16.
 */

public class VenuePagerAdapter extends FragmentPagerAdapter {

    private Context ctx;
    
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return ctx.getString(R.string.venue_conference_tab);
            case 1:
                return ctx.getString(R.string.venue_afterparty_tab);
            case 2:
                return ctx.getString(R.string.venue_hackathon_tab);
            default:
                return super.getPageTitle(position);
        }
    }

    public VenuePagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        this.ctx = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new VenueConferenceFragment();
            case 1:
                return new VenueAfterpartyFragment();
            case 2:
                return new VenueHackathonFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
