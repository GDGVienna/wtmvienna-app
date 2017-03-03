package at.wtmvienna.app.ui.sessions.list;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import at.wtmvienna.app.data.app.SelectedSessionsMemory;
import at.wtmvienna.app.data.app.model.Session;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SessionsListAdapter extends RecyclerView.Adapter<SessionsListEntry> {

    private final List<Session> sessions;
    private final Picasso picasso;
    private final SelectedSessionsMemory selectedSessionsMemory;
    private final SessionsListMvp.SessionDetailsHandler listener;
    private final boolean showTime;

    public SessionsListAdapter(List<Session> sessions, Picasso picasso, SelectedSessionsMemory selectedSessionsMemory, SessionsListMvp.SessionDetailsHandler listener, boolean showTime) {
        this.sessions = sessions;
        this.picasso = picasso;
        this.selectedSessionsMemory = selectedSessionsMemory;
        this.listener = listener;
        this.showTime = showTime;
    }

    @Override
    public SessionsListEntry onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SessionsListEntry(parent, picasso);
    }

    @Override
    public void onBindViewHolder(SessionsListEntry holder, int position) {
        Session session = sessions.get(position);
        holder.bindSession(session, selectedSessionsMemory.isSelected(session), showTime);
        holder.itemView.setOnClickListener(v -> listener.startSessionDetails(session));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
}
