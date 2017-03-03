package at.wtmvienna.app.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import at.wtmvienna.app.R;
import at.wtmvienna.app.data.app.DataProvider;
import at.wtmvienna.app.data.app.SelectedSessionsMemory;
import at.wtmvienna.app.data.app.model.Session;
import at.wtmvienna.app.ui.BaseFragment;
import at.wtmvienna.app.ui.drawer.DrawerActivity;
import at.wtmvienna.app.ui.sessions.details.SessionDetailsActivityIntentBuilder;
import at.wtmvienna.app.ui.sessions.list.SessionsListMvp;
import at.wtmvienna.app.utils.Analytics;
import at.wtmvienna.app.utils.Intents;
import butterknife.BindView;

/**
 * Created by helmuth on 26/08/16.
 */

public class HomeFragment extends BaseFragment<HomePresenter> implements HomeMvp.View, SessionsListMvp.SessionDetailsHandler {

    @BindView(R.id.home_news_card) View newsCard;
    @BindView(R.id.home_news_title) TextView newsTitle;
    @BindView(R.id.home_news_text) TextView newsText;
    @BindView(R.id.home_current_title) TextView currentTitle;
    @BindView(R.id.home_current_text) TextView currentText;
    @BindView(R.id.home_current_recyclerview) RecyclerView currentRecyclerView;
    @BindView(R.id.home_content) View content;
    @BindView(R.id.home_loading) View loading;
    @BindView(R.id.home_sponsor_willhaben) View willhaben;
    @BindView(R.id.home_sponsor_erste) View erste;
    @BindView(R.id.home_sponsor_pavelka) View pavelka;
    @BindView(R.id.home_sponsor_openforce) View openforce;
    @BindView(R.id.home_sponsor_easyname) View easyname;
    @BindView(R.id.home_buttons) View agenda_buttons;
    @BindView(R.id.home_button_agenda_mine) View agenda_mine;
    @BindView(R.id.home_button_agenda_full) View agenda_full;
    @Inject Analytics analytics;
    @Inject DatabaseReference dbRef;
    @Inject DataProvider dataProvider;
    @Inject Picasso picasso;
    @Inject SelectedSessionsMemory selectedSessionsMemory;

    @Override
    public void hideAnnouncement() {
        newsCard.setVisibility(View.GONE);
    }

    @Override
    public void setComingNext(@StringRes int title, @StringRes int text) {
        setComingNext(title, text, null);
    }

    @Override
    public void setComingNext(@StringRes int title, List<Session> sessions) {
        setComingNext(title, 0, sessions);
    }

    public void setComingNext(@StringRes int title, @StringRes int text, @Nullable List<Session> sessions) {
        //
        currentTitle.setText(title);
        if (sessions != null) {
            HomeCurrentSessions adapter = new HomeCurrentSessions(sessions, picasso, selectedSessionsMemory, this, true);
            currentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            currentRecyclerView.setHasFixedSize(true);
            currentRecyclerView.setAdapter(adapter);
            currentRecyclerView.setVisibility(View.VISIBLE);
            currentText.setVisibility(View.GONE);
            agenda_buttons.setVisibility(View.GONE);
        }
        else {
            currentRecyclerView.setVisibility(View.GONE);
            currentText.setText(text);
            currentText.setVisibility(View.VISIBLE);
            agenda_buttons.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setIsLoading(boolean isLoading) {
        content.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void openLink(String url) {
        if (url != null) {
            Intents.startExternalUrl(getActivity(), url);
        }
    }

    @Override
    protected HomePresenter newPresenter() {
        return new HomePresenter(this, dbRef, analytics, dataProvider);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        at.wtmvienna.app.WTMViennaApp.get(getContext()).component().inject(this);
        super.onCreate(savedInstanceState);
    }

    private void openDrawerItem(@IdRes int itemId) {
        Activity parent = getActivity();
        if (parent instanceof DrawerActivity) {
            DrawerActivity drawer = (DrawerActivity)parent;
            drawer.getPresenter().onNavigationItemSelected(itemId);
            drawer.selectDrawerMenuItem(itemId);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View.OnClickListener linkOpener = v -> openLink(v.getTag() != null ? v.getTag().toString() : null) ;
        willhaben.setOnClickListener(linkOpener);
        erste.setOnClickListener(linkOpener);
        pavelka.setOnClickListener(linkOpener);
        openforce.setOnClickListener(linkOpener);
        easyname.setOnClickListener(linkOpener);
        agenda_full.setOnClickListener(v -> openDrawerItem(R.id.drawer_nav_agenda));
        agenda_mine.setOnClickListener(v -> openDrawerItem(R.id.drawer_nav_schedule));
    }

    @Override
    public void updateAnnouncement(String title, String text) {
        newsCard.setVisibility(View.VISIBLE);
        newsTitle.setText(title);
        newsText.setText(text);
    }

    @Override
    public void startSessionDetails(Session session) {
        if (!TextUtils.isEmpty(session.getRoom())) {
            startActivity(new SessionDetailsActivityIntentBuilder(session).build(getContext()));
        }
    }
}
