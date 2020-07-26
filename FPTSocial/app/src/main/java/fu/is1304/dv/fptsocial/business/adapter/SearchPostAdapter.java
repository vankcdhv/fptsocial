package fu.is1304.dv.fptsocial.business.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;

public class SearchPostAdapter extends RecyclerView.Adapter<SearchPostAdapter.DataViewHolder> {
    private Context context;
    private List<Post> posts;
    private OnEventListener onEventListener;

    public SearchPostAdapter(Context context, List<Post> posts, OnEventListener onEventListener) {
        this.context = context;
        this.posts = posts;
        this.onEventListener = onEventListener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.search_post_item, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataViewHolder holder, int position) {
        final Post post = posts.get(position);
        final String uid = post.getUid();
        UserDAO.getInstance().getUserByUID(uid, new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                if (user.getAvatar() != null) {
                    Glide.with(context).load(user.getAvatar()).into(holder.imgSearchUserAva);
                } else {
                    if (user.getGender().equals(((Activity) context).getString(R.string.male))) {
                        holder.imgSearchUserAva.setImageDrawable(((Activity) context).getDrawable(R.drawable.nam));
                    } else {
                        holder.imgSearchUserAva.setImageDrawable(((Activity) context).getDrawable(R.drawable.nu));
                    }
                }
                holder.labelUserName.setText(user.getFirstName() + " " + user.getLastName());
                holder.labelTitle.setText(post.getTitle());
                holder.labelContent.setText(post.getContent().substring(0, Math.min(30, post.getContent().length() - 1)) + "...");
                holder.labelTime.setText(new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(post.getPostDate()));
                holder.postItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onEventListener.onClickPost(post);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout postItem;
        TextView labelUserName, labelTitle, labelContent, labelTime;
        CircleImageView imgSearchUserAva;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSearchUserAva = itemView.findViewById(R.id.imgSmallAva);
            labelUserName = itemView.findViewById(R.id.txtNewfeedAuthor);
            labelTitle = itemView.findViewById(R.id.txtNewfeedTitle);
            labelContent = itemView.findViewById(R.id.txtNewfeedContent);
            labelTime = itemView.findViewById(R.id.txtNewfeedTime);
            postItem = itemView.findViewById(R.id.postItem);
        }
    }

    public interface OnEventListener {
        public void onClickPost(Post post);
    }
}
