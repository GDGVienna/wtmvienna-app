package at.wtmvienna.app.ui.venue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.wtmvienna.app.R;
import at.wtmvienna.app.ui.BaseFragment;
import at.wtmvienna.app.ui.BaseFragmentPresenter;
import at.wtmvienna.app.ui.drawer.DrawerActivity;
import butterknife.BindView;

/**
 * Created by helmuth on 10/09/16.
 */

public class VenuePagerFragment extends BaseFragment {

    @BindView(R.id.venue_viewpager) ViewPager viewPager;

    @Override
    protected BaseFragmentPresenter newPresenter() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.venue_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setAdapter(new VenuePagerAdapter(getContext(), getChildFragmentManager()));
        ((DrawerActivity) getActivity()).setupTabLayoutWithViewPager(viewPager);
    }
}
