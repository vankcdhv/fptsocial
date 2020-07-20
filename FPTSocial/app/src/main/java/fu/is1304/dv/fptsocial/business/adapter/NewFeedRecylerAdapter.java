package fu.is1304.dv.fptsocial.business.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.common.StorageUtils;
import fu.is1304.dv.fptsocial.dao.PostDAO;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreDeleteDocCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;

public class NewFeedRecylerAdapter extends RecyclerView.Adapter<NewFeedRecylerAdapter.DataViewHolder> {
    private List<Post> posts;
    private Context context;
    private LruCache<String, Bitmap> memoryCache;
    private EventListener eventListener;

    public NewFeedRecylerAdapter(Context context, List<Post> posts, EventListener listener) {
        this.posts = posts;
        this.context = context;
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
        eventListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (posts.get(position).getImage() == null) return 0;
        else return 1;
    }

    @NonNull
    @Override
    public NewFeedRecylerAdapter.DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 1)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_have_image_layout, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_no_image_layout, parent, false);

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
    public void onBindViewHolder(@NonNull final NewFeedRecylerAdapter.DataViewHolder holder, int position) {
        final Post post = posts.get(position);
        if (post.getUid().equals(AuthController.getInstance().getUID())) {
            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] items = {"Chỉnh sửa", "Xóa"};
                    AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context);
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                eventListener.onClickEdit(post);
                            } else {
                                eventListener.onClickDelete(post);
                            }
                        }
                    });
                    builder.show();
                }
            });
        } else {
            holder.btnMenu.setVisibility(View.INVISIBLE);
            holder.btnMenu.setEnabled(false);
        }

        UserDAO.getInstance().getUserByUID(post.getUid(), new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                holder.txtTitle.setText(post.getTitle());
                holder.txtContent.setText(post.getContent());
                holder.txtTime.setText(new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss").format(post.getPostDate()));
                holder.txtAuthor.setText(user.getFirstName() + " " + user.getLastName());
                holder.txtAuthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_LONG).show();
                    }
                });
                holder.txtTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventListener.onClickTitle(post);
                    }
                });
                if (user.getAvatar() == null) {
                    if (user.getGender() == (context.getString(R.string.female))) {
                        holder.imgAva.setImageDrawable(((Activity) context).getDrawable(R.drawable.nu));
                    }
                } else {
                    if (getBitmapFromMemCache(post.getUid() + "avatar") != null) {
                        Bitmap bitmap = getBitmapFromMemCache(post.getUid() + "avatar");
                        holder.imgAva.setImageBitmap(bitmap);
                    } else {
                        StorageDAO.getInstance().getImage(user.getAvatar(), new FirestorageGetByteCallback() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onComplete(byte[] bytes) {
                                Bitmap bitmap = StorageUtils.bytesToBitMap(bytes);
                                addBitmapToMemoryCache(post.getUid() + "avatar", bitmap);
                                holder.imgAva.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onFailed(Exception e) {

                            }
                        });
                    }
                }
                if (post.getImage() == null) {
                } else {
                    if (getBitmapFromMemCache(post.getId() + "image") != null) {
                        Bitmap bitmap = getBitmapFromMemCache(post.getUid() + "image");
                        holder.imgNewfeedImage.setImageBitmap(bitmap);
                    } else {
                        StorageDAO.getInstance().getImage(post.getImage() + "_900x900", new FirestorageGetByteCallback() {
                            @Override
                            public void onStart() {
                                Glide.with(context).load(((Activity) context).getDrawable(R.drawable.loading)).into(holder.imgNewfeedImage);
                            }

                            @Override
                            public void onComplete(byte[] bytes) {
                                Bitmap bitmap = StorageUtils.bytesToBitMap(bytes);
                                addBitmapToMemoryCache(post.getId() + "image", bitmap);
                                holder.imgNewfeedImage.setImageBitmap(bitmap);
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
        return (posts == null ? 0 : posts.size());
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView txtAuthor;
        TextView txtTitle;
        TextView txtContent;
        ImageView imgNewfeedImage;
        TextView txtTime;
        ImageView imgAva;
        ImageButton btnMenu;
        ConstraintLayout layoutItem;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAuthor = itemView.findViewById(R.id.txtNewfeedAuthor);
            txtTitle = itemView.findViewById(R.id.txtNewfeedTitle);
            txtContent = itemView.findViewById(R.id.txtNewfeedContent);
            imgNewfeedImage = itemView.findViewById(R.id.imgNewfeedImage);
            txtTime = itemView.findViewById(R.id.txtNewfeedTime);
            imgAva = itemView.findViewById(R.id.imgSmallAva);
            btnMenu = itemView.findViewById(R.id.btnPostMenu);
            layoutItem = itemView.findViewById(R.id.layoutNewFeedItem);
        }
    }

    public interface EventListener {
        public void onClickEdit(Post post);

        public void onClickDelete(Post post);

        public void onClickTitle(Post post);
    }
}
