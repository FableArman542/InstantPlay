package code.dam_45414.instantplay.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import code.dam_45414.instantplay.Model.Post;
import code.dam_45414.instantplay.Model.User;
import code.dam_45414.instantplay.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mUsers;
    private MediaPlayer mp;

    public ProfilePostsAdapter(Context mContext, List<Post> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    public ProfilePostsAdapter () { }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profileposts, parent, false);

        return new ProfilePostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Post post = mUsers.get(position);
        Glide.with(mContext).load(post.getPostimage()).into(viewHolder.image);

        viewHolder.image.setBorderColor((post.getVisible() ? Color.parseColor("#2c2f33") : Color.parseColor("#dd2727")));
        viewHolder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid()).child("visible");

                boolean isVisible = !post.getVisible();

                String t = (isVisible) ? "Post now visible" : "Post now invisible";
                int color = (isVisible) ? Color.parseColor("#2c2f33") : Color.parseColor("#dd2727");
                reference.setValue(!post.getVisible());
                viewHolder.image.setBorderColor(color);
                Toast.makeText(mContext, t, Toast.LENGTH_SHORT).show();

                mp = MediaPlayer.create(mContext, R.raw.bubble);
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mp.start();
                    }
                });

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.release();
                    }
                });

                Animation animShake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                viewHolder.image.startAnimation(animShake);

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public de.hdodenhof.circleimageview.CircleImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_posts);
        }
    }

}
