package at.devfest.app.ui.home;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import at.devfest.app.data.app.SelectedSessionsMemory;
import at.devfest.app.data.app.model.Session;
import at.devfest.app.ui.sessions.list.SessionsListEntry;
import at.devfest.app.ui.sessions.list.SessionsListMvp;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by helmuth on 31/08/16.
 */

public class HomeCurrentSessions extends RecyclerView.Adapter<HomeCurrentListEntry> {
    private final List<Session> sessions;
    private final Picasso picasso;
    private final SelectedSessionsMemory selectedSessionsMemory;
    private final SessionsListMvp.SessionDetailsHandler listener;
    private final boolean showTime;

    public HomeCurrentSessions(List<Session> sessions, Picasso picasso, SelectedSessionsMemory selectedSessionsMemory, SessionsListMvp.SessionDetailsHandler listener, boolean showTime) {
        this.sessions = sessions;
        this.picasso = picasso;
        this.selectedSessionsMemory = selectedSessionsMemory;
        this.listener = listener;
        this.showTime = showTime;
    }

    @Override
    public HomeCurrentListEntry onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeCurrentListEntry(parent, picasso);
    }

    @Override
    public void onBindViewHolder(HomeCurrentListEntry holder, int position) {
        Session session = sessions.get(position);
        holder.bindSession(session, selectedSessionsMemory.isSelected(session), showTime);
        holder.itemView.setOnClickListener(v -> listener.startSessionDetails(session));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
}
