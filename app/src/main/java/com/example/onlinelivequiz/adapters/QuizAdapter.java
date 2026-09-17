package com.example.onlinelivequiz.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.activities.QuizActivity;
import com.example.onlinelivequiz.models.Quiz;

import java.util.List;

public class QuizAdapter
        extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    private final Context context;
    private final List<Quiz> quizList;

    public QuizAdapter(
            Context context,
            List<Quiz> quizList) {

        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_quiz,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Quiz quiz =
                quizList.get(position);

        holder.titleText.setText(
                quiz.getTitle()
        );

        holder.descriptionText.setText(
                quiz.getDescription()
        );

        holder.durationText.setText(
                "Duration: "
                        + quiz.getDuration()
                        + " min"
        );

        holder.questionsText.setText(
                "Questions: "
                        + quiz.getTotalQuestions()
        );

        holder.startButton.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    context,
                                    QuizActivity.class
                            );

                    intent.putExtra(
                            "quizId",
                            quiz.getQuizId()
                    );

                    intent.putExtra(
                            "quizTitle",
                            quiz.getTitle()
                    );

                    intent.putExtra(
                            "duration",
                            quiz.getDuration()
                    );

                    context.startActivity(intent);
                }
        );
    }

    @Override
    public int getItemCount() {

        return quizList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView descriptionText;
        TextView durationText;
        TextView questionsText;

        Button startButton;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            titleText =
                    itemView.findViewById(
                            R.id.titleText
                    );

            descriptionText =
                    itemView.findViewById(
                            R.id.descriptionText
                    );

            durationText =
                    itemView.findViewById(
                            R.id.durationText
                    );

            questionsText =
                    itemView.findViewById(
                            R.id.questionsText
                    );

            startButton =
                    itemView.findViewById(
                            R.id.startButton
                    );
        }
    }
}