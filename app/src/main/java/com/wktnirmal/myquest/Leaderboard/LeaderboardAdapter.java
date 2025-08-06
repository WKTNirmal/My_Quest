package com.wktnirmal.myquest.Leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wktnirmal.myquest.R;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private List<UserEntry> leaderboardList;

    public LeaderboardAdapter(List<UserEntry> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }


    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_list_item, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        UserEntry user = leaderboardList.get(position);
        holder.username.setText(user.getUsername());
        holder.xp.setText(String.valueOf(user.getXp()));
        holder.placement.setText(String.valueOf(user.getPlacement())+ getPlacementSuffix(user.getPlacement()));

    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView username, xp, placement;
        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameText);
            xp = itemView.findViewById(R.id.UserXpCountText);
            placement = itemView.findViewById(R.id.placementText);
        }
    }

    private String getPlacementSuffix(int number) {
        if (number % 100 >= 11 && number % 100 <= 13) return "th";
        switch (number % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
