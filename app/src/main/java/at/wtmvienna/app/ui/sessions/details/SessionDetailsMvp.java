package at.wtmvienna.app.ui.sessions.details;

import android.support.annotation.StringRes;

import at.wtmvienna.app.data.app.model.Session;

public interface SessionDetailsMvp {

    interface View {
        void bindSessionDetails(Session session);

        void updateFabButton(boolean isSelected, boolean animate);

        void showSnackbarMessage(@StringRes int resId);

        void enableFeedback(boolean show);
    }

    interface Presenter {
        void onFloatingActionButtonClicked();
    }
}
