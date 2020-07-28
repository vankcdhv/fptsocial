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
import fu.is1304.dv.fptsocial.entity.FriendMessage;
import fu.is1304.dv.fptsocial.entity.User;

public class FriendMessageAdapter extends RecyclerView.Adapter<FriendMessageAdapter.DataViewHolder> {

    private Context context;
    private List<FriendMessage> friendMessages;
    private OnEventListener onEventListener;

    public FriendMessageAdapter(Context context, List<FriendMessage> friendMessages, OnEventListener onEventListener) {
        this.context = context;
        this.friendMessages = friendMessages;
        this.onEventListener = onEventListener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_chat_item, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataViewHolder holder, int position) {
        final FriendMessage friendMessage = friendMessages.get(position);

        UserDAO.getInstance().getUserByUID(friendMessage.getUid(), new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                if (user.getAvatar() == null) {
                    if (user.getGender().equals(((Activity) context).getString(R.string.male))) {
                        holder.imgChatAva.setImageDrawable(((Activity) context).getDrawable(R.drawable.nam));
                    } else {
                        holder.imgChatAva.setImageDrawable(((Activity) context).getDrawable(R.drawable.nu));
                    }
                } else {
                    Glide.with(context).load(user.getAvatar()).into(holder.imgChatAva);
                }
                holder.labelChatUsername.setText(user.getFirstName() + " " + user.getLastName());
                holder.labelPreviewContent.setText(friendMessage.getLastestMessage().substring(0, Math.min(20, friendMessage.getLastestMessage().length() - 1)));
                holder.labelChatTime.setText(new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(friendMessage.getTime()));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layoutChatItem;
        CircleImageView imgChatAva;
        TextView labelChatUsername, labelPreviewContent, labelChatTime;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChatAva = itemView.findViewById(R.id.imgChatAva);
            layoutChatItem = itemView.findViewById(R.id.layoutChatItem);
            labelChatUsername = itemView.findViewById(R.id.labelChatIUserName);
            labelPreviewContent = itemView.findViewById(R.id.labelPreviewContent);
            labelChatTime = itemView.findViewById(R.id.labelChatTime);
        }
    }

    public interface OnEventListener {
        public void onClickMessage(FriendMessage friendMessage);
    }
}
