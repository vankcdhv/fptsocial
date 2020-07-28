package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.business.adapter.NewFeedRecylerAdapter;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.CountDAO;
import fu.is1304.dv.fptsocial.dao.FriendDAO;
import fu.is1304.dv.fptsocial.dao.NotificationDAO;
import fu.is1304.dv.fptsocial.dao.PostDAO;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageUploadCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreDeleteDocCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Friend;
import fu.is1304.dv.fptsocial.entity.Notification;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;

public class WallActivity extends AppCompatActivity {

    private ImageView imgAddFriend, imgViewImage;
    private CircleImageView imgWallAvatar;
    private TextView labelFullName;
    private Button btnMessage;
    private RecyclerView recyclerListPost;
    private NewFeedRecylerAdapter recylerAdapter;
    private List<Post> posts;

    private User user;
    private String uid;
    private Intent data;

    private int countPost = 0;

    //Variable of dialog
    private Dialog postDialog;
    private EditText txtPostTitle;
    private EditText txtPostContent;
    private ImageView imgPostImage;
    private Button btnPost;
    private Uri imageUri;
    private String statusImage;

    //Variable of dialog
    private Dialog infoDialog;
    private EditText txtCourse;
    private EditText txtGender;
    private EditText txtDepartment;
    private TextView txtDob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        initComponents();
    }

    private void initComponents() {
        getSupportActionBar().hide();
        imgWallAvatar = findViewById(R.id.imgWallAvatar);
        imgAddFriend = findViewById(R.id.imgAddFriend);
        imgViewImage = findViewById(R.id.imgViewImage);
        labelFullName = findViewById(R.id.labelFullName);
        btnMessage = findViewById(R.id.btnMessage);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WallActivity.this, ChatActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
        posts = new ArrayList<>();
        recylerAdapter = new NewFeedRecylerAdapter(this, posts, new NewFeedRecylerAdapter.EventListener() {
            @Override
            public void onClickEdit(Post post) {
                if (!post.getUid().equals(AuthController.getInstance().getUID())) {
                    Toast.makeText(WallActivity.this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
                } else {
                    editPostDialog(post);
                }

            }

            @Override
            public void onClickDelete(final Post post) {
                if (!post.getUid().equals(AuthController.getInstance().getUID())) {
                    Toast.makeText(WallActivity.this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
                } else {
                    final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(WallActivity.this);
                    confirmDialog.setMessage(getString(R.string.confirm_yes_no))
                            .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PostDAO.getInstance().deleteStatus(post, new FirestoreDeleteDocCallback() {
                                        @Override
                                        public void onComplete() {
                                            Toast.makeText(WallActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                                            countPost--;
                                            posts.remove(post);
                                            recylerAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailed(Exception e) {
                                            Toast.makeText(WallActivity.this, R.string.have_error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    confirmDialog.show();
                }
            }

            @Override
            public void onClickTitle(Post post) {
                Intent intent = new Intent(WallActivity.this, PostDetailActivity.class);
                intent.putExtra("post", post);
                startActivity(intent);
            }

            @Override
            public void onAuthorClick(String uid) {

            }
        });
        recyclerListPost = findViewById(R.id.recyclerListPost);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerListPost.setLayoutManager(layoutManager);
        recyclerListPost.setHasFixedSize(true);
        recyclerListPost.setAdapter(recylerAdapter);
        initDialog();
        getData();

    }

    private void getData() {
        data = getIntent();
        uid = data.getStringExtra("uid");
        if (uid.equals(AuthController.getInstance().getUID())) {
            imgAddFriend.setEnabled(false);
            imgAddFriend.setVisibility(View.INVISIBLE);
        }
        UserDAO.getInstance().getUserByUID(uid, new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                if (user.getAvatar() == null) {
                    if (user.getGender().equals(getString(R.string.female))) {
                        imgWallAvatar.setImageDrawable(getDrawable(R.drawable.nu));
                    }
                } else {
                    Glide.with(WallActivity.this).load(user.getAvatar()).into(imgWallAvatar);
                    imgWallAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog viewImage = new Dialog(WallActivity.this);
                            viewImage.setContentView(R.layout.dialog_view_image);
                            ImageView image = viewImage.findViewById(R.id.imgShowImage);
                            Glide.with(WallActivity.this).load(user.getAvatar()).into(image);
                            viewImage.show();
                        }
                    });
                }
                labelFullName.setText(user.getFirstName() + " " + user.getLastName());
                getAllPost();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        FriendDAO.getInstance().checkIsFriend(uid, new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                boolean isFriend = false;
                List<Friend> friendList = DatabaseUtils.convertListDocSnapToListFriend(documentSnapshots);
                for (Friend friend : friendList) {
                    if (friend.getUid().equals(AuthController.getInstance().getUID())) {
                        isFriend = true;
                        setCancelFriend();
                    }
                }
                if (!isFriend) {
                    setAddFriend();
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void setAddFriend() {
        imgAddFriend.setImageDrawable(getDrawable(R.drawable.ic_action_person_add));
        imgAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });
    }

    private void setCancelFriend() {
        imgAddFriend.setImageDrawable(getDrawable(R.drawable.ic_action_mobile_friendly));
        imgAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFriend();
            }
        });
    }

    private void initDialog() {
        postDialog = new Dialog(this);
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
    }

    //Open post status dialog to edit post
    private void editPostDialog(final Post post) {
        txtPostTitle.setText(post.getTitle());
        txtPostContent.setText(post.getContent());
        if (post.getImage() != null) {
            Glide.with(this).load(post.getImage()).into(imgPostImage);
        }
        btnPost.setText(getString(R.string.btn_save));
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPost(post);
            }
        });
        postDialog.show();
    }

    //Select image for status
    public void selectImage(View view) {
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
        startActivityForResult(myIntent, Const.REQUEST_CODE_CHOSE_STATUS_IMAGE);
    }

    //Change image in dialog after user select image
    public void changeImage(int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            imageUri = data.getData();
            imgPostImage.setImageURI(imageUri);
            statusImage = "/images/albums/" + AuthController.getInstance().getUID() + "/" + imageUri.getLastPathSegment();
        }
    }

    public void editPost(Post post) {
        String oldImage = post.getImage();
        String title = txtPostTitle.getText().toString();
        String content = txtPostContent.getText().toString();
        String uid = AuthController.getInstance().getUID();
        Date postDate = post.getPostDate();
        if (statusImage != null && statusImage != oldImage) {
            post = new Post(post.getId(), uid, title, content, statusImage, postDate, post.getCountLike());
            uploadImage(post);
        } else {
            post = new Post(post.getId(), uid, title, content, post.getImage(), postDate, post.getCountLike());
            updateStatus(post);
        }
    }

    //Upload image if user selected one image
    private void uploadImage(final Post post) {
        StorageDAO.getInstance().upImage(statusImage, imageUri, new FirestorageUploadCallback() {
            @Override
            public void onComplete(Uri uri) {
                String url = uri.toString();
                post.setImage(url);
                updateStatus(post);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(WallActivity.this, R.string.upload_image_post_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    //Update post on database
    private void updateStatus(Post post) {
        PostDAO.getInstance().updatePost(post, new FirestoreSetCallback() {
            @Override
            public void onSuccess(String postID) {
                Toast.makeText(WallActivity.this, R.string.update_successfuly, Toast.LENGTH_SHORT).show();
                postDialog.dismiss();
                refreshList();
                initDialog();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(WallActivity.this, R.string.have_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Reload list post
    private void refreshList() {
        clearListPost();
        getAllPost();
    }

    //Clear list post
    private void clearListPost() {
        posts.clear();
        recylerAdapter.notifyDataSetChanged();
    }

    //Get all post of an user
    private void getAllPost() {
        CountDAO.getInstance().getCount(Const.POST_COLLECTION + "_" + user.getUid(), new CountDAO.GetCountCallback() {
            @Override
            public void onComplete(long count) {
                countPost = (int) count;
                PostDAO.getInstance().getPostByUID(user.getUid(), (int) count, new FirebaseGetCollectionCallback() {
                    @Override
                    public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                        List<Post> list = DatabaseUtils.convertListDocSnapToListPost(documentSnapshots);
                        posts.addAll(list);
                        recylerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(Exception e) {
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }


    private void addFriend() {
        FriendDAO.getInstance().addFriend(uid, new FirestoreSetCallback() {
            @Override
            public void onSuccess(String id) {
                Toast.makeText(WallActivity.this, R.string.add_friend_complete, Toast.LENGTH_LONG).show();
                setCancelFriend();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(WallActivity.this, R.string.have_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelFriend() {
        FriendDAO.getInstance().deleteFriend(uid, new FirestoreSetCallback() {
            @Override
            public void onSuccess(String id) {
                Toast.makeText(WallActivity.this, R.string.delete_friend_complete, Toast.LENGTH_LONG).show();
                setAddFriend();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(WallActivity.this, R.string.have_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showInfoDialog(View view) {
        infoDialog = new Dialog(this);
        infoDialog.setContentView(R.layout.info_layout);
        txtCourse = infoDialog.findViewById(R.id.etCourse);
        txtGender = infoDialog.findViewById(R.id.spinnerGender);
        txtDepartment = infoDialog.findViewById(R.id.edMajor);
        txtDob = infoDialog.findViewById(R.id.etDob);

        txtCourse.setText(user.getCourse() + "");
        txtGender.setText(user.getGender());
        txtDob.setText(user.getDob());
        txtDepartment.setText(user.getDepartment());
        infoDialog.show();
    }

}