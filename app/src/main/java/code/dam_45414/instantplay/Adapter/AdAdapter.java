package code.dam_45414.instantplay.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import code.dam_45414.instantplay.ChatActivity;
import code.dam_45414.instantplay.Model.AdItem;
import code.dam_45414.instantplay.Model.User;
import code.dam_45414.instantplay.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.ViewHolder> {

    private Context context;
    private List<AdItem> listitem;
    private String cat;

    public AdAdapter (Context context, List listitem, String cat) {
        this.context = context;
        this.listitem = listitem;
        this.cat = cat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.adrow, parent, false);

        return new AdAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final AdItem item = listitem.get(i);

        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());

        if (!item.getId().equals(FirebaseAuth.getInstance().getUid())) {
            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(item.getId());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Intent intent = new Intent (context, ChatActivity.class);
                            intent.putExtra("receiver", item.getId());
                            User user = snapshot.getValue(User.class);
                            intent.putExtra("pic", user.getImageurl());
                            intent.putExtra("username", user.getUsername());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
        } else {
            viewHolder.title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    //Toast.makeText(context, cat + " " + item.getId(), Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference("Announces").child(cat).child(item.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });


                    return false;
                }
            });
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(item.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(viewHolder.pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listitem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView description;
        public CircleImageView pic;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.authorTitle);
            description = itemView.findViewById(R.id.authorDescription);
            pic = itemView.findViewById(R.id.authorPic);
        }
    }
}
