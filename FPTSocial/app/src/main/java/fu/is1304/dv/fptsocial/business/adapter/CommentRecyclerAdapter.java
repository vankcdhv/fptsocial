package fu.is1304.dv.fptsocial.business.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.common.StorageUtils;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.Comment;
import fu.is1304.dv.fptsocial.entity.User;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.DataViewHolder> {

    private Context context;
    private List<Comment> comments;
    private LruCache<String, Bitmap> memoryCache;
    private OnEventListener onEventListener;


    public CommentRecyclerAdapter(Context context, List<Comment> comments, OnEventListener listener) {
        this.context = context;
        this.comments = comments;
        this.onEventListener = listener;

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
        return new DataViewHolder(itemView);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        UserDAO.getInstance().getUserByUID(comment.getUid(), new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                holder.txtUsername.setText(user.getFirstName() + " " + user.getLastName());
                holder.txtUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onEventListener.onClickUsername(user);
                    }
                });
                holder.txtTime.setText(new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(comment.getTime()));
                if (comment.isBlocked())
                    holder.txtContent.setText(context.getString(R.string.comment_is_blocked));
                else holder.txtContent.setText(comment.getContent());
                if (user.getGender() == (context.getString(R.string.female))) {
                    holder.imgCmtAva.setImageDrawable(((Activity) context).getDrawable(R.drawable.nu));
                }
                if (user.getAvatar() != null) {
                    Bitmap bitmap = getBitmapFromMemCache(comment.getUid() + "avatar");
                    if (bitmap != null) {
                        holder.imgCmtAva.setImageBitmap(bitmap);
                    } else {
                        StorageDAO.getInstance().getImage(user.getAvatar(), new FirestorageGetByteCallback() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onComplete(byte[] bytes) {
                                Bitmap bitmap = StorageUtils.bytesToBitMap(bytes);
                                addBitmapToMemoryCache(comment.getUid() + "avatar", bitmap);
                                holder.imgCmtAva.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onFailed(Exception e) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCmtAva;
        TextView txtUsername, txtContent, txtTime;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCmtAva = itemView.findViewById(R.id.imgCmtAva);
            txtUsername = itemView.findViewById(R.id.labelUserName);
            txtContent = itemView.findViewById(R.id.labelContent);
            txtTime = itemView.findViewById(R.id.labelTime);
        }
    }

    public interface OnEventListener {
        public void onClickUsername(User user);

    }
}
