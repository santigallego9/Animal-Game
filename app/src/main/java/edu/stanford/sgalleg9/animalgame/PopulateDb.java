package edu.stanford.sgalleg9.animalgame;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

/**
 * Created by santigallego on 7/28/16.
 */
public class PopulateDb {

    static Firebase fb;
    final static HashMap<String, String> counts = new HashMap<>();

    public static void addDataToDatabase(final Activity activity, final HashMap<String, String> node, final boolean isYes, final String question, final String animal) {
        fb = new Firebase(PlayActivity.URL);

        final Firebase nodes = fb.child("nodes/");
        final Firebase graphs = fb.child("graphs/");
        final Firebase reports = fb.child("reports/");

        fb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey().toString();
                Object value = dataSnapshot.getValue();

                counts.put(key, value.toString());

                //Toast.makeText(activity, "KEY: " + key + "\nValue: " + value.toString(), Toast.LENGTH_SHORT).show();
                if(key.equals("reports")) {

                    final int node_count = Integer.parseInt(counts.get("node_count"));
                    final int graph_count = Integer.parseInt(counts.get("graph_count"));

                    nodes.child((node_count + 1) + "").child("id").setValue((node_count + 1));
                    nodes.child((node_count + 1) + "").child("report").setValue(0);
                    nodes.child((node_count + 1) + "").child("type").setValue("question");
                    nodes.child((node_count + 1) + "").child("text").setValue(question);

                    nodes.child((node_count + 2) + "").child("id").setValue((node_count + 2));
                    nodes.child((node_count + 2) + "").child("report").setValue(0);
                    nodes.child((node_count + 2) + "").child("type").setValue("answer");
                    nodes.child((node_count + 2) + "").child("text").setValue(animal);

                    fb.child("node_count").setValue(node_count + 2);

                    final Query graph_query = graphs.orderByChild("child_id").equalTo(Integer.parseInt(node.get("id")));

                    graph_query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                /*Log.d("test", "child " + child.getKey() + " => "
                                        + child.getValue());*/
                                String key = child.getKey().toString();
                                Object value = child.getValue();

                                String first, second;

                                //Toast.makeText(activity, "KEY: " + key + "\nValue: " + value.toString() + "\nCount: " + counts.get("graph_count"), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(activity, "KEY: " + key, Toast.LENGTH_SHORT).show();

                                if(isYes) {
                                    first = "yes";
                                    second = "no";
                                } else {
                                    first = "no";
                                    second = "yes";
                                }

                                String key1 = (graph_count + 101) + "";
                                String key2 = (graph_count + 102) + "";

                                graphs.child(key).child("child_id").setValue(node_count + 1);

                                graphs.child(key1).child("id").setValue(Integer.parseInt(key1));
                                graphs.child(key1).child("parent_id").setValue(node_count + 1);
                                graphs.child(key1).child("child_id").setValue(node_count + 2);
                                graphs.child(key1).child("type").setValue(first);

                                graphs.child(key2).child("id").setValue(Integer.parseInt(key2));
                                graphs.child(key2).child("parent_id").setValue(node_count + 1);
                                graphs.child(key2).child("child_id").setValue(Integer.parseInt(node.get("id")));
                                graphs.child(key2).child("type").setValue(second);

                                fb.child("graph_count").setValue(graph_count + 2);
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
}
