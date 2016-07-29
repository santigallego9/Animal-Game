package edu.stanford.sgalleg9.animalgame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

import stanford.androidlib.SimpleActivity;

public class PlayActivity extends SimpleActivity {

    final static String URL = "https://animal-game-83ef0.firebaseio.com/";
    final private static String YES = "yes";
    final private static String NO = "no";
    final private static String QUESTION = "question";
    final private static String ANSWER = "answer";
    final private static String TYPE = "type";

    final HashMap<String, String> node = new HashMap<>();
    final HashMap<String, String> graph = new HashMap<>();
    final HashMap<String, String> current_type = new HashMap<>();
    TextView questionView;
    Firebase fb;
    String button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Firebase.setAndroidContext(this);

        questionView = (TextView) findViewById(R.id.question);
        fb = new Firebase(URL);

        final Firebase nodes = fb.child("nodes/");
        final Firebase graphs = fb.child("graphs/");
        final Firebase reports = fb.child("reports/");

        button = null;

        current_type.put(TYPE, QUESTION);

        checkDatabase("1");
    }

    public void noClick(View view) {
        questionView.setText("Loading...");
        try {
            setType(NO);
        } catch (Exception e) {
            log("Early click: " + e);
        }

        /*Log.d("DB ERROR", "In node: " + node.get("id") + ", the type is incorrectly set as: " + node.get("type"));
        Firebase nodes = fb.child("nodes/");
        Firebase reports = fb.child("reports/");

        String timeStamp = new SimpleDateFormat("MMddyyyyHHmmss").format(new java.util.Date());

        Firebase single_node = nodes.child(node.get("id"));
        single_node.child("reports").setValue(Integer.parseInt(node.get("reports")) + 1);

        reports.child(timeStamp).child("node_id").setValue(Integer.parseInt(node.get("id")));
        reports.child(timeStamp).child("issue").setValue("Type in nodes is incorrectly set");
        reports.child(timeStamp).child("admin").setValue(true);*/
    }

    public void yesClick(View view) {
        questionView.setText("Loading...");
        try {
            setType(YES);
        } catch (Exception e) {
            log("Early click: " + e);
        }
    }

    public void checkDatabaseAnswer(final String type) {
        final Firebase nodes = fb.child("nodes/");
        final Firebase graphs = fb.child("graphs/");
        Firebase reports = fb.child("reports/");
        final Firebase single_node = nodes.child(node.get("id"));

        // nodes.child("tester").setValue("this is a test");

        single_node.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey().toString();
                Object value = dataSnapshot.getValue();

                //String type = null;

                if (key.equals("id")) {

                    final Query graph_query = graphs.orderByChild("parent_id").equalTo(Integer.parseInt(value.toString()));

                    // nodes.child("tester").setValue("this is a test");

                    graph_query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot record : dataSnapshot.getChildren()) {
                                String key = record.getKey();

                                //toast(key);

                                if(record.child(TYPE).getValue().toString().equals(type)) {

                                    final Firebase new_single_node = nodes.child(record.child("child_id").getValue().toString());
                                    final HashMap<String, String> result = new HashMap<String, String>();

                                    new_single_node.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            String key = dataSnapshot.getKey().toString();
                                            Object value = dataSnapshot.getValue();

                                            Log.d("DEBUG", "KEY: " + key + "\n" + "VALUE: " + value.toString());

                                            result.put(key, value.toString());

                                            if(key.equals("type")) {
                                                displayAnswer(result);
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void checkDatabaseQuestion(final String type) {
        Firebase nodes = fb.child("nodes/");
        Firebase graphs = fb.child("graphs/");
        Firebase reports = fb.child("graphs/");
        Firebase single_node = nodes.child(node.get("id"));

        final Query graph_query = graphs.orderByChild("parent_id").equalTo(Integer.parseInt(node.get("id")));

        // nodes.child("tester").setValue("this is a test");

        graph_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot record : dataSnapshot.getChildren()) {
                    String key = record.getKey();

                    if(record.child("type").getValue().toString().equals(type)) {
                        try {
                            if(current_type.get(TYPE).equals(QUESTION)) {
                                checkDatabase(record.child("child_id").getValue().toString());
                            } else if (current_type.get(TYPE).equals(ANSWER)) {
                                checkDatabaseAnswer(type);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void checkDatabase(String id) {
        Firebase nodes = fb.child("nodes/");
        Firebase graphs = fb.child("graphs/");
        Firebase reports = fb.child("reports/");
        Firebase single_node = nodes.child(id);

        // nodes.child("tester").setValue("this is a test");

        single_node.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey().toString();
                Object value = dataSnapshot.getValue();

                String type = null;

                node.put(key, value.toString());

                if (key.equals("text")) {
                    questionView.setText(value.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setType(final String type) {
        final Firebase nodes = fb.child("nodes/");
        Firebase graphs = fb.child("graphs/");
        Firebase reports = fb.child("reports/");

        final Query graph_query = graphs.orderByChild("parent_id").equalTo(Integer.parseInt(node.get("id")));

        // nodes.child("tester").setValue("this is a test");

        graph_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot record : dataSnapshot.getChildren()) {

                    if(record.child("type").getValue().toString().equals(type)) {

                        //toast(record.child("child_id").getValue().toString());
                        String id = record.child("child_id").getValue().toString();
                        Firebase single_node = nodes.child(id);

                        single_node.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                String key = dataSnapshot.getKey().toString();
                                Object value = dataSnapshot.getValue();

                                //toast("Key: " + key + " Value: " + value.toString());

                                if (key.equals("type")) {
                                    current_type.put(TYPE, value.toString());
                                    //toast("SET TYPE " + current_type.get(TYPE));

                                    if(node.get("type").equals("question")) {
                                        checkDatabaseQuestion(type);
                                    }
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // nodes.child("tester").setValue("this is a test");
    }

    public void displayAnswer(HashMap<String, String> hash) {

        ViewDialog alert = new ViewDialog();
        alert.showQuestionDialog(PlayActivity.this, hash);
        /*new AlertDialog.Builder(PlayActivity.this)
                .setTitle("My guess is...")
                .setMessage("Was your animal a " + hash.get("text"))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface lose_dialog, int which) {
                        toast("I win");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface lose_dialog, int which) {

                        final EditText animal = new EditText(PlayActivity.this);
                        animal.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                        new AlertDialog.Builder(PlayActivity.this)
                                .setMessage("What was your animal?")
                                .setView(animal)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface lose_dialog, int which) {

                                        toast(animal.getText().toString());

                                        final EditText question = new EditText(PlayActivity.this);
                                        question.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                                        new AlertDialog.Builder(PlayActivity.this)
                                                .setMessage("What is a question to seperate a " + animal.getText().toString().toLowerCase() + " from an [animal]")
                                                .setView(question)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface lose_dialog, int which) {

                                                        toast(question.getText().toString());

                                                        Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                                                        startActivity(intent);

                                                    }
                                                })
                                                .show();
                                    }
                                })
                                .show();
                    }
                })
                .show();*/
        /*new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface lose_dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface lose_dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();*/
    }
}
