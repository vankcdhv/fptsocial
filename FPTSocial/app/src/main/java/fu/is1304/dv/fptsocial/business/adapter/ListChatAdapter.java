package fu.is1304.dv.fptsocial.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.entity.Message;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.DataViewHolder> {

    private Context context;
    private List<Message> messages;
    private OnEventListener onEventListener;

    public ListChatAdapter(Context context, List<Message> messages, OnEventListener onEventListener) {
        this.context = context;
        this.messages = messages;
        this.onEventListener = onEventListener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_send_chat_item, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_receive_chat_item, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.contentChat.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isSend()) return 0;
        return 1;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView contentChat;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            contentChat = itemView.findViewById(R.id.labelTextChat);
        }
    }

    public interface OnEventListener {
    }


}
