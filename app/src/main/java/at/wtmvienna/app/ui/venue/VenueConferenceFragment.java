package at.wtmvienna.app.ui.venue;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import at.wtmvienna.app.R;
import at.wtmvienna.app.ui.BaseFragment;
import at.wtmvienna.app.ui.BaseFragmentPresenter;
import at.wtmvienna.app.utils.Intents;
import at.wtmvienna.app.utils.Views;
import butterknife.BindView;
import butterknife.OnClick;

public class VenueConferenceFragment extends BaseFragment {

    @BindView(R.id.venue_conference_image) ImageView photo;

    private static final float PHOTO_RATIO = 0.406f;
    private static final String COORDINATES_URI = "geo:48.1965898,16.3680991?q=" + Uri.encode("Fakultät+für+Elektrotechnik+und+Informationstechnik");

    @Override
    protected BaseFragmentPresenter newPresenter() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.venue_conference, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPhotoSize();
    }

    @OnClick(R.id.venue_conference_locate)
    void openMapsLocation() {
        if (!Intents.startUri(getContext(), COORDINATES_URI)) {
            View view = getView();
            if (view != null) {
                Snackbar.make(view, R.string.venue_see_location_error, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void initPhotoSize() {
        photo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = photo.getWidth();
                if (width != 0) {
                    Views.removeOnGlobalLayoutListener(photo.getViewTreeObserver(), this);
                    photo.getLayoutParams().height = Math.round(width * PHOTO_RATIO);
                    photo.requestLayout();
                }
            }
        });
    }
}
