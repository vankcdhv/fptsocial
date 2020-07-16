package fu.is1304.dv.fptsocial.gui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.business.NewFeedAdapter;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.PostDAO;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageUploadCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;
import fu.is1304.dv.fptsocial.gui.LoginActivity;
import fu.is1304.dv.fptsocial.gui.MainActivity;
import fu.is1304.dv.fptsocial.gui.ProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewfeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewfeedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Variable of fragment
    private NewFeedAdapter newFeedAdapter;
    private ListView lvNewFeed;
    private TextView labelStatus;
    private List<Post> listPost;

    //Variable of dialog
    private Dialog postDialog;
    private EditText txtPostTitle;
    private EditText txtPostContent;
    private ImageView imgPostImage;
    private Button btnPost;
    private Uri imageUri;
    private String statusImage;

    public NewfeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewfeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewfeedFragment newInstance(String param1, String param2) {
        NewfeedFragment fragment = new NewfeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_newfeed, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        lvNewFeed = v.findViewById(R.id.listNewFeed);
        labelStatus = v.findViewById(R.id.labelStatus);

        labelStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPostDialog();
            }
        });

        listPost = new ArrayList<>();
        newFeedAdapter = new NewFeedAdapter(getContext(), 0, listPost);
        lvNewFeed.setAdapter(newFeedAdapter);
        initDialog();
        getAllPost();
    }

    private void initDialog() {
        postDialog = new Dialog(getContext());
        postDialog.setContentView(R.layout.dialog_post_status_layout);
        txtPostTitle = postDialog.findViewById(R.id.txtPostStatusTitle);
        txtPostContent = postDialog.findViewById(R.id.txtPostStatusContent);
        imgPostImage = postDialog.findViewById(R.id.imgStatusImage);
        btnPost = postDialog.findViewById(R.id.btnPostStatus);

        imgPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStatus();
            }
        });
    }

    //Open post status dialog
    private void openPostDialog() {
        postDialog.show();
    }

    //Select image for status
    public void selectImage(View view) {
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
        getActivity().startActivityForResult(myIntent, Const.REQUEST_CODE_CHOSE_STATUS_IMAGE);
    }

    //Change image in dialog after user select image
    public void changeImage(int resultCode, @Nullable Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            imageUri = data.getData();
            imgPostImage.setImageURI(imageUri);
            statusImage = "/images/albums/" + AuthController.getInstance().getUID() + "/" + imageUri.getLastPathSegment();
        }
    }

    //Post the status
    public void postStatus() {
        String title = txtPostTitle.getText().toString();
        String content = txtPostContent.getText().toString();
        String uid = AuthController.getInstance().getUID();
        Date postDate = new Date();
        Post post = new Post(uid, title, content, statusImage, postDate);
        if (statusImage != null) {
            uploadImage(post);
        } else {
            createStatus(post);
        }
    }

    //Upload image if user selected one image
    private void uploadImage(final Post post) {
        StorageDAO.getInstance().upImage(statusImage, imageUri, new FirestorageUploadCallback() {
            @Override
            public void onComplete(UploadTask.TaskSnapshot taskSnapshot) {
                createStatus(post);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Update your image failed, your post will not create", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createStatus(Post post) {
        PostDAO.getInstance().postStatus(post, new FirestoreSetCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), "Đã đăng bài thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Có lỗi xảy ra! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Get all post and show posts to listview
    private void getAllPost() {
        PostDAO.getInstance().getAllPost(new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                List<Post> list = DatabaseUtils.convertListDocSnapToListPost(documentSnapshots);
                newFeedAdapter.addAll(list);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(getActivity(), "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
            }
        });
    }

}