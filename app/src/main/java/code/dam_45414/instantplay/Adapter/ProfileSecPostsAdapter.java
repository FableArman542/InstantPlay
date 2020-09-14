package code.dam_45414.instantplay.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ProfileSecPostsAdapter extends RecyclerView.Adapter<ProfileSecPostsAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mUsers;

    public ProfileSecPostsAdapter(Context mContext, List<Post> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    public ProfileSecPostsAdapter () { }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profileposts, parent, false);

        return new ProfileSecPostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Post post = mUsers.get(position);
        Glide.with(mContext).load(post.getPostimage()).into(viewHolder.image);
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
