package com.wktnirmal.myquest.QuestLog;

import android.content.Context;
import android.content.Intent;
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
    private Context context;

    public QuestLogAdapter(List<Quest> questList, Context context) {
        this.questList = questList;
        this.context = context;
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
        holder.reward.setText(String.valueOf(quest.getReward()));
        holder.description.setText(quest.getQuestDescription());

        //pass the data to the ViewQuest activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewQuest.class);
            intent.putExtra("questID", quest.getId());
            intent.putExtra("questTitle", quest.getQuestTitle());
            intent.putExtra("questReward", quest.getReward());
            intent.putExtra("questDescription", quest.getQuestDescription());
            intent.putExtra("questEndLat", quest.getEndLat());
            intent.putExtra("questEndLng", quest.getEndLng());
            intent.putExtra("questRepetitive", quest.getRepetitive());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    static class QuestViewHolder extends RecyclerView.ViewHolder {
        TextView title, reward, description;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.questTitleText);
            reward = itemView.findViewById(R.id.questXpAmountText);
            description = itemView.findViewById(R.id.descriptionText);
        }
    }



}
