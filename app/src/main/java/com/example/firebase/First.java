package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebase.Model.LeaderBoard;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class First extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    String uuid,email;
    ArrayList<LeaderBoard> leaderBoardArrayList;
    LeaderAdapter leaderAdapter;
    GoogleSignInClient mGoogleSignInClient;
    RecyclerView recyclerView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        button=findViewById(R.id.button);
        mAuth=FirebaseAuth.getInstance();
        recyclerView=findViewById(R.id.recyclerview);
        firebaseDatabase=FirebaseDatabase.getInstance();
        uuid=mAuth.getUid();
        addToDatabase(uuid,"200",email);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        button.setOnClickListener(v -> {
            mAuth.signOut();
            signOut();

        });
        leaderBoardArrayList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        leaderAdapter=new LeaderAdapter(leaderBoardArrayList);
        recyclerView.setAdapter(leaderAdapter);

    }

    private void getData() {
        Query DatabaseQuery =firebaseDatabase.getReference().child("USERS").orderByChild("Time");
        DatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaderBoardArrayList.clear();
                for(DataSnapshot postsnapshot: snapshot.getChildren()) {
                   for(DataSnapshot snap : postsnapshot.getChildren())
                   {
                       if(snap.getKey().equals("Email")) {
                           Log.d("vipin", snap.getValue().toString());
                           leaderBoardArrayList.add(new LeaderBoard(snap.getValue().toString(),snap.getValue().toString(),snap.getValue().toString()));

                       }
                   }
                }
                leaderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addToDatabase(String uuid,String steps,String time) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("Time", ServerValue.TIMESTAMP);
        hashMap.put("Steps",steps);
        hashMap.put("Email",mAuth.getCurrentUser().getEmail());
        firebaseDatabase.getReference().child("USERS").child(uuid).updateChildren(hashMap).addOnSuccessListener(aVoid ->
                Toast.makeText(First.this, "Succesful", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                Toast.makeText(First.this, "Failed", Toast.LENGTH_SHORT).show());


    }
    private void signOut() {

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        revokeAccess();
                    }
                });
    }
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(First.this,MainActivity.class));
                        finish();
                    }
                });
    }

    @Override
    protected void onStart() {
        getData();
        super.onStart();
    }
}