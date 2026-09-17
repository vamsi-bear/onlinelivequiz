package com.example.onlinelivequiz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.models.Question;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;

    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);

        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull QuestionViewHolder holder,
            int position) {

        Question question = questionList.get(position);

        holder.questionText.setText(
                (position + 1) + ". " + question.getQuestion()
        );

        holder.option1.setText(question.getOptionA());
        holder.option2.setText(question.getOptionB());
        holder.option3.setText(question.getOptionC());
        holder.option4.setText(question.getOptionD());

        // Handle selection logic
        holder.optionsGroup.setOnCheckedChangeListener(null);
        holder.optionsGroup.clearCheck();

        String selected = question.getSelectedAnswer();
        if (selected != null) {
            if (selected.equals("A")) holder.option1.setChecked(true);
            else if (selected.equals("B")) holder.option2.setChecked(true);
            else if (selected.equals("C")) holder.option3.setChecked(true);
            else if (selected.equals("D")) holder.option4.setChecked(true);
        }

        holder.optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.option1) question.setSelectedAnswer("A");
            else if (checkedId == R.id.option2) question.setSelectedAnswer("B");
            else if (checkedId == R.id.option3) question.setSelectedAnswer("C");
            else if (checkedId == R.id.option4) question.setSelectedAnswer("D");
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder
            extends RecyclerView.ViewHolder {

        TextView questionText;

        RadioButton option1;
        RadioButton option2;
        RadioButton option3;
        RadioButton option4;

        RadioGroup optionsGroup;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            questionText = itemView.findViewById(R.id.questionText);

            option1 = itemView.findViewById(R.id.option1);
            option2 = itemView.findViewById(R.id.option2);
            option3 = itemView.findViewById(R.id.option3);
            option4 = itemView.findViewById(R.id.option4);

            optionsGroup = itemView.findViewById(R.id.optionsGroup);
        }
    }
}