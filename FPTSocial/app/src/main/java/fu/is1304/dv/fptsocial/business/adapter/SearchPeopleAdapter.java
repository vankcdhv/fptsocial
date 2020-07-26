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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.entity.User;

public class SearchPeopleAdapter extends RecyclerView.Adapter<SearchPeopleAdapter.DataViewHolder> {
    private Context context;
    private List<User> users;
    private OnEventListener onEventListener;

    public SearchPeopleAdapter(Context context, List<User> users, OnEventListener onEventListener) {
        this.context = context;
        this.users = users;
        this.onEventListener = onEventListener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.search_people_item, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        final User user = users.get(position);
        if (user.getAvatar() != null) {
            Glide.with(context).load(user.getAvatar()).into(holder.imgSearchAva);
        } else {
            if (user.getGender().equals(((Activity) context).getString(R.string.male))) {
                holder.imgSearchAva.setImageDrawable(((Activity) context).getDrawable(R.drawable.nam));
            } else {
                holder.imgSearchAva.setImageDrawable(((Activity) context).getDrawable(R.drawable.nu));
            }
        }
        holder.labelFullName.setText(user.getFirstName() + " " + user.getLastName());
        holder.labelMore.setText("K " + user.getCourse());
        holder.peopleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventListener.onClickPeople(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout peopleItem;
        TextView labelFullName, labelMore;
        CircleImageView imgSearchAva;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSearchAva = itemView.findViewById(R.id.imgSearchAva);
            labelFullName = itemView.findViewById(R.id.labelFullName);
            labelMore = itemView.findViewById(R.id.labelMoreInfo);
            peopleItem = itemView.findViewById(R.id.peopelItem);
        }
    }

    public interface OnEventListener {
        public void onClickPeople(User user);
    }
}
