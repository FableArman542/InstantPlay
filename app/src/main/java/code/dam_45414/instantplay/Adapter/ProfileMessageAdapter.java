package code.dam_45414.instantplay.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import code.dam_45414.instantplay.ChatActivity;
import code.dam_45414.instantplay.Model.User;
import code.dam_45414.instantplay.R;

public class ProfileMessageAdapter extends RecyclerView.Adapter<ProfileMessageAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public ProfileMessageAdapter (Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    public ProfileMessageAdapter () { }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_profile_item, parent, false);

        return new ProfileMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final User user = mUsers.get(position);

        Glide.with(mContext).load(user.getImageurl()).into(viewHolder.profile_image);
        viewHolder.username.setText(user.getUsername());
        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (mContext, ChatActivity.class);

                intent.putExtra("receiver", user.getId());
                intent.putExtra("pic", user.getImageurl());
                intent.putExtra("username", user.getUsername());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public de.hdodenhof.circleimageview.CircleImageView profile_image;
        public TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.image_profileMSG);
            username = itemView.findViewById(R.id.usernameMSG);
        }
    }

}
