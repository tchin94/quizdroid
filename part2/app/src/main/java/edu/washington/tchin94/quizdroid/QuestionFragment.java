package edu.washington.tchin94.quizdroid;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class QuestionFragment extends Fragment {

    private String rightAnswer;
    private View rootView;
    private boolean selected = false;

    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //grabs the views in the fragment
        rootView = inflater.inflate(R.layout.fragment_question, container, false);
        TextView questionText = (TextView) rootView.findViewById(R.id.fragment_question_text);
        RadioButton answer1 = (RadioButton) rootView.findViewById(R.id.frag_answer1);
        RadioButton answer2 = (RadioButton) rootView.findViewById(R.id.frag_answer2);
        RadioButton answer3 = (RadioButton) rootView.findViewById(R.id.frag_answer3);
        RadioButton answer4 = (RadioButton) rootView.findViewById(R.id.frag_answer4);

        //gets question and answers from the activity
        MultiUseActivity multiUseActivity = (MultiUseActivity)getActivity();
        int curQuestion = multiUseActivity.getCurQuestion();
        Log.d("" + curQuestion, "DEBUG");
        String question = multiUseActivity.getQuestion(curQuestion);
        String[] answers = multiUseActivity.getAnswers();

        //sets the texts in the view
        questionText.setText(question);
        ArrayList<RadioButton> answer = new ArrayList<RadioButton>();
        answer.add(answer1); answer.add(answer2); answer.add(answer3); answer.add(answer4);
        setRadioAnswers(answer, answers, curQuestion);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //remembers if something is selected for the advance button
        if (savedInstanceState != null) {
            selected = savedInstanceState.getBoolean("selected");
        }

        Button submitBtn = (Button) getActivity().findViewById(R.id.advance_btn);
        submitBtn.setText("Submit");

        //sets the submit button to visible or invisible
        submitBtn.setEnabled(selected);
        if (!selected) {
            submitBtn.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.VISIBLE);
        }

        //submits the answer
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add a point if correct
                RadioGroup group = (RadioGroup) rootView.findViewById(R.id.frag_answer_group);
                RadioButton selected = (RadioButton) rootView.findViewById(group.getCheckedRadioButtonId());
                MultiUseActivity multiUseActivity = (MultiUseActivity)getActivity();
                if (selected.getContentDescription().equals("correct")) {
                    multiUseActivity.addPoint();
                }

                //these are the param sent to the instance of the answer summary page
                multiUseActivity.incrementCurQuestion();
                Log.d("increment question", "DEBUG");
                int correct = multiUseActivity.getCorrect();
                int totalQuestion = multiUseActivity.getNumQuestions();
                String yourAnswer = (String) selected.getText();

                Log.d(yourAnswer, "DEBUG your answer");

                AnswerSummaryFragment answerSummaryFragment =
                        AnswerSummaryFragment.newInstance(yourAnswer, rightAnswer, correct, totalQuestion);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragment, answerSummaryFragment);
                fragmentTransaction.commit();
            }
        });
    }

    public void setRadioAnswers(ArrayList<RadioButton> answer, String[] answers, int curQuestion) {
        for (int i = 0; i < 4; i++) {
            String curAnswer = answers[i + (curQuestion * 4)];
            if (curAnswer.endsWith("***")) {
                curAnswer = curAnswer.substring(0, curAnswer.length() - 3);
                rightAnswer = curAnswer;
                answer.get(i).setContentDescription("correct");
            } else {
                answer.get(i).setContentDescription("wrong");
            }
            answer.get(i).setText(curAnswer);
            answer.get(i).setOnClickListener(radioClickListener);
        }
    }

    View.OnClickListener radioClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button submitBtn = (Button) getActivity().findViewById(R.id.advance_btn);
            submitBtn.setEnabled(true);
            submitBtn.setVisibility(View.VISIBLE);
            selected = true;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("selected", selected);
    }

}
