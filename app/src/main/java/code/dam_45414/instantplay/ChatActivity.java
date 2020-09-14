package code.dam_45414.instantplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import code.dam_45414.instantplay.Adapter.MessageAdapter;
import code.dam_45414.instantplay.Model.ChatMessage;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    CircleImageView imageprofile;
    TextView username;

    EditText messageText;
    Button sendButton;

    FirebaseUser firebaseUser;
    String receiver;

    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ChatMessage> mMessages;

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        receiver = intent.getStringExtra("receiver");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        messageText = findViewById(R.id.messageText);
        sendButton  = findViewById(R.id.sendButton);
        imageprofile = findViewById(R.id.image_profile);
        username = findViewById(R.id.username);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mMessages = new ArrayList<>();
        messageAdapter = new MessageAdapter(getApplicationContext(), mMessages);
        recyclerView.setAdapter(messageAdapter);

        readMessages();

        username.setText(intent.getStringExtra("username"));

        Glide.with(getApplicationContext()).load(intent.getStringExtra("pic")).into(imageprofile);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage (messageText.getText().toString());
            }
        });

        back = findViewById(R.id.backbtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void readMessages () {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessage msg = snapshot.getValue(ChatMessage.class);
                    if ((msg.getSender().equals(firebaseUser.getUid()) || msg.getReceiver().equals(firebaseUser.getUid())) && (msg.getSender().equals(receiver) || msg.getReceiver().equals(receiver))) { // Falta uma condicao
                        mMessages.add(msg);
                    }
                }

                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage (String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        String messageid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("messageId", messageid);
        hashMap.put("messageText", message);
        hashMap.put("sender", firebaseUser.getUid());
        hashMap.put("receiver", receiver);
        hashMap.put("messageTime", new Date().getTime());

        reference.child(messageid).setValue(hashMap);

        messageText.setText("");

    }
}
