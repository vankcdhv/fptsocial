package fu.is1304.dv.fptsocial.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.common.StorageUtils;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.entity.Post;

public class NewFeedAdapter extends ArrayAdapter<Post> {

    Context context;
    private List<Post> list;

    public NewFeedAdapter(@NonNull Context context, int resource, @NonNull List<Post> objects) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Post getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getPosition(@Nullable Post item) {
        return list.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewItem(position, convertView, parent);
    }

    private View createViewItem(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View res = convertView;
        if (res == null) {
            res = LayoutInflater.from(context).inflate(R.layout.listview_newfeed_layout, parent, false);
        }
        Post newFeed = list.get(position);
        TextView txtAuthor = res.findViewById(R.id.txtNewfeedAuthor);
        TextView txtTitle = res.findViewById(R.id.txtNewfeedTitle);
        TextView txtContent = res.findViewById(R.id.txtNewfeedContent);
        final ImageView imgNewfeedImage = res.findViewById(R.id.imgNewfeedImage);
        TextView txtTime = res.findViewById(R.id.txtNewfeedTime);
        txtTitle.setText(newFeed.getTitle());
        txtContent.setText(newFeed.getContent());
        txtTime.setText(new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss").format(newFeed.getPostDate()));
        txtAuthor.setText(newFeed.getAuthor());
        if (newFeed.getImage() == null) {
            imgNewfeedImage.setVisibility(View.INVISIBLE);
            imgNewfeedImage.setMaxHeight(0);
        } else {
            StorageDAO.getInstance().getImage(newFeed.getImage(), new FirestorageGetByteCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onComplete(byte[] bytes) {
                    Bitmap bitmap = StorageUtils.bytesToBitMap(bytes);
                    imgNewfeedImage.setImageBitmap(bitmap);
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        }
        return res;
    }
}
