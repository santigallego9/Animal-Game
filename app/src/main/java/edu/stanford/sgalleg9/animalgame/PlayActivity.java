package edu.stanford.sgalleg9.animalgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.text.SimpleDateFormat;
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
    final HashMap<String, String> answer_node = new HashMap<>();
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
                                            answer_node.put(key, value.toString());

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

    }

    public void reportClick(View view) {

        try {
            String type = node.get("type").substring(0, 1).toUpperCase() + node.get("type").substring(1);

            ViewDialog alert = new ViewDialog();
            alert.showReportDialog(PlayActivity.this, node, type);
        } catch (Exception e) {
            //toast(e);
        }
    }

    public void reportAnswerClick(View view) {

        try {
            String type = answer_node.get("type").substring(0, 1).toUpperCase() + answer_node.get("type").substring(1);

            ViewDialog alert = new ViewDialog();
            alert.showReportDialog(PlayActivity.this, answer_node, type);
        } catch (Exception e) {
            //toast(e);
        }
    }
}
