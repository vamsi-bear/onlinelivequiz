package com.example.onlinelivequiz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.models.Score;

import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter
        extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<Score> scoreList;

    public LeaderboardAdapter(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_leaderboard,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Score score = scoreList.get(position);

        // Rank
        holder.rankText.setText(
                String.valueOf(position + 1)
        );

        // Top 3 medals
        if (position == 0) {
            holder.rankText.setText("🥇");

        } else if (position == 1) {
            holder.rankText.setText("🥈");

        } else if (position == 2) {
            holder.rankText.setText("🥉");
        }

        // User name
        String userName = score.getUserName();

        if (userName == null ||
                userName.trim().isEmpty()) {

            userName = "Anonymous";
        }

        holder.userNameText.setText(userName);

        // Score
        holder.scoreText.setText(
                String.valueOf(score.getScore())
        );

        // Completion time
        long completionTime = score.getCompletionTime();

        long minutes = completionTime / 60;
        long seconds = completionTime % 60;

        String formattedTime = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds
        );

        holder.timeText.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView rankText;
        TextView userNameText;
        TextView scoreText;
        TextView timeText;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            rankText = itemView.findViewById(
                    R.id.rankText
            );

            userNameText = itemView.findViewById(
                    R.id.userNameText
            );

            scoreText = itemView.findViewById(
                    R.id.scoreText
            );

            timeText = itemView.findViewById(
                    R.id.timeText
            );
        }
    }
}