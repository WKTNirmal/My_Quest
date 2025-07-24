package com.wktnirmal.myquest.QuestLog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wktnirmal.myquest.Quest;
import com.wktnirmal.myquest.R;

import java.util.List;

public class QuestLogAdapter extends RecyclerView.Adapter<QuestLogAdapter.QuestViewHolder>{
    private List<Quest> questList;

    public QuestLogAdapter(List<Quest> questList) {
        this.questList = questList;
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_list_item, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        Quest quest = questList.get(position);
        holder.title.setText(quest.getQuestTitle());
        holder.distance.setText(String.valueOf(quest.getDistance() / 10));
        holder.description.setText(quest.getQuestDescription());
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    static class QuestViewHolder extends RecyclerView.ViewHolder {
        TextView title, distance, description;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.questTitleText);
            distance = itemView.findViewById(R.id.questXpAmountText);
            description = itemView.findViewById(R.id.descriptionText);
        }
    }



}
