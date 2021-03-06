package edu.stanford.sgalleg9.animalgame;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.HashMap;


/**
 * Created by santigallego on 7/28/16.
 */
public class ViewDialog {

    public void showQuestionDialog(final Activity activity, final HashMap hash) {
        final android.app.Dialog dialog = new android.app.Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.question_dialog);

        TextView guessTextView = (TextView) dialog.findViewById(R.id.animal_quess);

        guessTextView.setText("Was your animal a " + hash.get("text") + "?");

        Button yesButton = (Button) dialog.findViewById(R.id.yes);
        Button noButton = (Button) dialog.findViewById(R.id.no);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ViewDialog alert = new ViewDialog();
                alert.showWinDialog(activity, hash);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ViewDialog alert = new ViewDialog();
                alert.showLoseDialog(activity, hash);
            }
        });

        dialog.show();
    }

    public void showWinDialog(final Activity activity, HashMap hash) {
        final android.app.Dialog dialog = new android.app.Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.win_dialog);

        Button okButton = (Button) dialog.findViewById(R.id.ok);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
            }
        });

        dialog.show();
    }

    public void showLoseDialog(final Activity activity, final HashMap hash){
        final android.app.Dialog dialog = new android.app.Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.lose_dialog);

        final EditText animalEditText = (EditText) dialog.findViewById(R.id.animal_answer);
        final EditText questionEditText = (EditText) dialog.findViewById(R.id.question_answer);

        TextView questionTextView = (TextView) dialog.findViewById(R.id.difference_question);

        questionTextView.setText("What is a yes or no question to seperate your animal from a " + hash.get("text") + "?");

        Button okButton = (Button) dialog.findViewById(R.id.ok);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String animal = animalEditText.getText().toString().toLowerCase();
                final String question = questionEditText.getText().toString();

                if(animal.length() > 0 && question.length() > 0) {
                    dialog.dismiss();
                    showAnswerDialog(activity, hash, question, animal);
                } else {
                    Toast.makeText(activity, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }

    public void showAnswerDialog(final Activity activity, final HashMap hash, final String question, final String animal){
        final android.app.Dialog dialog = new android.app.Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.answer_dialog);

        TextView animalQuestionTextView = (TextView) dialog.findViewById(R.id.animal_question);
        TextView userTextView = (TextView) dialog.findViewById(R.id.user_question);

        animalQuestionTextView
                .setText("Is " + animal + " the yes or no answer to the question:");
        userTextView.setText(question);

        Button yesButton = (Button) dialog.findViewById(R.id.yes);
        Button noButton = (Button) dialog.findViewById(R.id.no);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                PopulateDb.addDataToDatabase(activity, hash, true, question, animal);
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                PopulateDb.addDataToDatabase(activity, hash, false, question, animal);
                /*Toast.makeText(activity, hash.get("id").toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, hash.get("type").toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, hash.get("text").toString(), Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
            }
        });

        dialog.show();

    }

    public void showReportDialog(final Activity activity, final HashMap<String, String> hash, final String type){
        final android.app.Dialog dialog = new android.app.Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.report_dialog);

        TextView issueTextView = (TextView) dialog.findViewById(R.id.issue_question);
        TextView title = (TextView) dialog.findViewById(R.id.issue_title);
        final EditText issue = (EditText) dialog.findViewById(R.id.issue);

        issueTextView
                .setText("What is wrong with this " + type.toLowerCase() + "?");


        title.setText("Report " + type.toLowerCase());

        Button okButton = (Button) dialog.findViewById(R.id.ok);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Firebase fb = new Firebase(PlayActivity.URL);

                Firebase nodes = fb.child("nodes/");
                Firebase reports = fb.child("reports/");

                String timeStamp = new SimpleDateFormat("MMddyyyyHHmmss").format(new java.util.Date());

                Firebase single_node = nodes.child(hash.get("id"));
                single_node.child("reports").setValue(Integer.parseInt(hash.get("reports")) + 1);

                reports.child(timeStamp).child("node_id").setValue(Integer.parseInt(hash.get("id")));
                reports.child(timeStamp).child("issue").setValue(issue.getText().toString());
                reports.child(timeStamp).child("admin").setValue(false);

                Toast.makeText(activity, type + " reported", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}