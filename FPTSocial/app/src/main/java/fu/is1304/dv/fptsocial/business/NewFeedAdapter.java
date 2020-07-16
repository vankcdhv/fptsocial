package fu.is1304.dv.fptsocial.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
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
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;

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
        final Post newFeed = list.get(position);

        final TextView txtAuthor = res.findViewById(R.id.txtNewfeedAuthor);
        final TextView txtTitle = res.findViewById(R.id.txtNewfeedTitle);
        final TextView txtContent = res.findViewById(R.id.txtNewfeedContent);
        final ImageView imgNewfeedImage = res.findViewById(R.id.imgNewfeedImage);
        final TextView txtTime = res.findViewById(R.id.txtNewfeedTime);


        UserDAO.getInstance().getUserByUID(newFeed.getUid(), new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                txtTitle.setText(newFeed.getTitle());
                txtContent.setText(newFeed.getContent());
                txtTime.setText(new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss").format(newFeed.getPostDate()));
                txtAuthor.setText(user.getFirstName() + " " + user.getLastName());
                txtAuthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_LONG).show();
                    }
                });
                if (newFeed.getImage() == null) {
                    imgNewfeedImage.setVisibility(View.INVISIBLE);
                    imgNewfeedImage.setMaxHeight(0);
                } else {
                    StorageDAO.getInstance().getImage(newFeed.getImage(), new FirestorageGetByteCallback() {
                        @Override
                        public void onStart() {
                            Glide.with(context).load(((Activity) context).getDrawable(R.drawable.loading)).into(imgNewfeedImage);
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
            }

            @Override
            public void onFailure(Exception e) {

            }
        });


        return res;
    }
}
