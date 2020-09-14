package code.dam_45414.instantplay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import code.dam_45414.instantplay.Model.ChatMessage;
import code.dam_45414.instantplay.Model.User;
import code.dam_45414.instantplay.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatMessage> mMessages;

    public MessageAdapter(Context mContext, List<ChatMessage> mMessages) {
        this.mContext = mContext;
        this.mMessages = mMessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_item, parent, false);

        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        ChatMessage msg = mMessages.get(position);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(msg.getSender());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                viewHolder.username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (msg.getSender().equals(FirebaseAuth.getInstance().getUid())) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            viewHolder.layout.setLayoutParams(params);
            viewHolder.username.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            viewHolder.messageText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            viewHolder.dateText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            viewHolder.layout.setLayoutParams(params);
            viewHolder.username.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            viewHolder.messageText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            viewHolder.dateText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        viewHolder.messageText.setText(msg.getMessageText());

        Date dateObj = new Date(msg.getMessageTime());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        StringBuilder now = new StringBuilder(format.format(dateObj));
        viewHolder.dateText.setText(now.toString());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView dateText;
        public TextView username;
        public LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messagetextItem);
            dateText = itemView.findViewById(R.id.dateItem);
            username = itemView.findViewById(R.id.usernameItem);
            layout = itemView.findViewById(R.id.layoutMSG);
        }
    }
}
