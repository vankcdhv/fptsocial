package fu.is1304.dv.fptsocial.business.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
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
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.Notification;
import fu.is1304.dv.fptsocial.entity.User;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.DataViewHolder> {
    private Context context;
    private List<Notification> notificationList;
    private LruCache<String, Bitmap> memoryCache;
    private OnEventListener onEventListener;

    public NotificationRecyclerAdapter(Context context, List<Notification> notificationList, OnEventListener onEventListener) {
        this.context = context;
        this.notificationList = notificationList;
        this.onEventListener = onEventListener;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout, parent, false);
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
        final Notification notification = notificationList.get(position);

        //Set notify avatar
        UserDAO.getInstance().getUserByUID(notification.getUid(), new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                holder.labelMessage.setText(user.getFirstName() + " " + user.getLastName() + " " + notification.getMessage());
                holder.labelTime.setText(new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(notification.getTime()));
                if (notification.isSeen())
                    holder.itemLayout.setBackgroundColor(Color.WHITE);
                else holder.itemLayout.setBackgroundColor(Color.GRAY);
                if (user.getAvatar() == null) {
                    if (user.getGender() == (context.getString(R.string.female))) {
                        holder.imgNotiAva.setImageDrawable(((Activity) context).getDrawable(R.drawable.nu));
                    }
                } else {
                    Glide.with(context).load(user.getAvatar()).into(holder.imgNotiAva);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventListener.onClickNotify(notification);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView labelMessage, labelTime;
        ConstraintLayout itemLayout;
        ImageView imgNotiAva;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            labelMessage = itemView.findViewById(R.id.lableNotificationMessage);
            labelTime = itemView.findViewById(R.id.labelNotificationTime);
            itemLayout = itemView.findViewById(R.id.notificationItemLayout);
            imgNotiAva = itemView.findViewById(R.id.imgNotiAvatar);
        }
    }

    public interface OnEventListener {
        public void onClickNotify(Notification notification);
    }
}
