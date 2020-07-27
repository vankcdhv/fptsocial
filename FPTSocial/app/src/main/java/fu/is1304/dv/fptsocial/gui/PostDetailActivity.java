package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.business.adapter.CommentRecyclerAdapter;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.CommentDAO;
import fu.is1304.dv.fptsocial.dao.CountDAO;
import fu.is1304.dv.fptsocial.dao.LikeDAO;
import fu.is1304.dv.fptsocial.dao.NotificationDAO;
import fu.is1304.dv.fptsocial.dao.PostDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreDeleteDocCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Comment;
import fu.is1304.dv.fptsocial.entity.Friend;
import fu.is1304.dv.fptsocial.entity.Notification;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;

public class PostDetailActivity extends AppCompatActivity {

    private TextView txtTitle, txtContent, txtAuthor, txtTime, labelCountLike;
    private ImageView imgSmallAva, imgStatusImage, btnStatusMenu;
    private ImageButton btnLikePost, btnSendComment;
    private EditText txtCreateComment;
    private Intent data;
    private CommentRecyclerAdapter commentRecyclerAdapter;
    private RecyclerView recyclerViewComment;
    private List<Comment> comments;
    private Post currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initComponents();
    }

    private void initComponents() {
        getSupportActionBar().hide();
        txtTitle = findViewById(R.id.txtDetailNewfeedTitle);
        txtContent = findViewById(R.id.txtDetailNewfeedContent);
        txtAuthor = findViewById(R.id.txtDetailNewfeedAuthor);
        txtTime = findViewById(R.id.txtDetailNewfeedTime);
        imgSmallAva = findViewById(R.id.imgDetailSmallAva);
        imgStatusImage = findViewById(R.id.imgDetailNewfeedImage);
        btnStatusMenu = findViewById(R.id.btnDetailPostMenu);
        recyclerViewComment = findViewById(R.id.recyclerListComment);
        btnLikePost = findViewById(R.id.btnLikePost);
        btnSendComment = findViewById(R.id.btnSendComment);
        txtCreateComment = findViewById(R.id.txtCreateComment);
        labelCountLike = findViewById(R.id.labelCountLike);

        comments = new ArrayList<>();
        commentRecyclerAdapter = new CommentRecyclerAdapter(this, comments, new CommentRecyclerAdapter.OnEventListener() {
            @Override
            public void onClickUsername(User user) {
                Intent intent = new Intent(PostDetailActivity.this, WallActivity.class);
                intent.putExtra("uid", user.getUid());
                startActivity(intent);
            }

            @Override
            public void onClickDeleteComment(Comment comment) {
                deleteComment(comment);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewComment.setLayoutManager(layoutManager);
        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setAdapter(commentRecyclerAdapter);
        data = getIntent();
        loadData();
    }

    public void btnLike(View view) {
        LikeDAO.getInstance().likePost(currentPost.getId(), new FirestoreSetCallback() {
            @Override
            public void onSuccess(String id) {
                int count = Integer.parseInt(id.trim());
                currentPost.setCountLike(count);
                labelCountLike.setText(id.trim());
                btnLikePost.setBackground(getDrawable(R.drawable.icon_like));
                btnLikePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnUnLike(v);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public void btnUnLike(View view) {
        LikeDAO.getInstance().unLikePost(currentPost.getId(), new FirestoreSetCallback() {
            @Override
            public void onSuccess(String id) {
                int count = Integer.parseInt(id.trim());
                currentPost.setCountLike(count);
                labelCountLike.setText(id.trim());
                btnLikePost.setBackground(getDrawable(R.drawable.icon_not_like));
                btnLikePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnLike(v);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void loadData() {
        currentPost = (Post) data.getSerializableExtra("post");
        final Post post = currentPost;
        if (post.getUid().equals(AuthController.getInstance().getUID())) {
            btnStatusMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] items = {"Chỉnh sửa", "Xóa"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                //eventListener.onClickEdit(post);
                            } else {
                                //eventListener.onClickDelete(post);
                            }
                        }
                    });
                    builder.show();
                }
            });
        } else {
            btnStatusMenu.setVisibility(View.INVISIBLE);
            btnStatusMenu.setEnabled(false);
        }

        UserDAO.getInstance().getUserByUID(post.getUid(), new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                txtTitle.setText(post.getTitle());
                txtContent.setText(post.getContent());
                txtTime.setText(new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss").format(post.getPostDate()));
                txtAuthor.setText(user.getFirstName() + " " + user.getLastName());
                txtAuthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(PostDetailActivity.this, user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_LONG).show();
                    }
                });
                txtTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //eventListener.onClickTitle(post);
                    }
                });
                LikeDAO.getInstance().checkIsLikePost(AuthController.getInstance().getUID(), currentPost.getId(), new FirebaseGetCollectionCallback() {
                    @Override
                    public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                        if (documentSnapshots.size() > 0 && documentSnapshots.get(0).getBoolean("liked")) {
                            btnLikePost.setBackground(getDrawable(R.drawable.icon_like));
                            btnLikePost.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    btnUnLike(v);
                                }
                            });
                        } else {
                            btnLikePost.setBackground(getDrawable(R.drawable.icon_not_like));
                            btnLikePost.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    btnLike(v);
                                }
                            });
                        }
                    }


                    @Override
                    public void onFailed(Exception e) {

                    }
                });
                labelCountLike.setText(currentPost.getCountLike() + "");
                if (user.getAvatar() == null) {
                    if (user.getGender() == (getString(R.string.female))) {
                        imgSmallAva.setImageDrawable(getDrawable(R.drawable.nu));
                    }
                } else {
                    Glide.with(PostDetailActivity.this).load(user.getAvatar()).into(imgSmallAva);
                }

                if (post.getImage() == null) {
                } else {
                    Glide.with(PostDetailActivity.this).load(post.getImage()).into(imgStatusImage);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        getListComment(post);

    }

    private void clearListComment() {
        comments.clear();
        commentRecyclerAdapter.notifyDataSetChanged();
    }

    private void getListComment(Post post) {
        CommentDAO.getInstance().getAllCommentOfPost(post.getId(), new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                List<Comment> list = DatabaseUtils.convertListDocSnapToListComment(documentSnapshots);
                //comments = list;
                comments.addAll(list);
                commentRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    public void btnSendCommentOnClick(View view) {
        String content = txtCreateComment.getText().toString();
        if (content != null && content.length() > 0) {
            final Comment comment = new Comment();
            comment.setPostID(currentPost.getId());
            comment.setContent(content);
            comment.setTime(new Date());
            comment.setUid(AuthController.getInstance().getUID());
            txtCreateComment.setEnabled(false);
            CommentDAO.getInstance().createComment(currentPost.getId(), comment, new FirestoreSetCallback() {
                @Override
                public void onSuccess(String id) {
                    comment.setId(id);
                    comments.add(0, comment);
                    commentRecyclerAdapter.notifyDataSetChanged();
                    txtCreateComment.setEnabled(true);
                    txtCreateComment.setText("");
                    makeNotify();
                }

                @Override
                public void onFailure(Exception e) {
                    txtCreateComment.setEnabled(true);
                }
            });
        }
    }

    public void deleteComment(final Comment comment) {
        CommentDAO.getInstance().deleteComment(comment, new FirestoreDeleteDocCallback() {
            @Override
            public void onComplete() {
                comments.remove(comment);
                commentRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(PostDetailActivity.this, R.string.delete_comment_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeNotify() {
        String title = Const.POST_NOTIFICATION_TITLE;
        String message = getString(R.string.notify_comment_your_post);
        Date time = new Date();
        String uid = AuthController.getInstance().getUID();
        Boolean seen = false;
        Notification notification = new Notification(title, message, time, uid, currentPost.getId(), seen);

        NotificationDAO.getInstance().createNotification(currentPost.getUid(), notification, new FirestoreSetCallback() {
            @Override
            public void onSuccess(String notiID) {
                CountDAO.getInstance().getCountNotificationByUID(currentPost.getUid(), new CountDAO.GetCountCallback() {
                    @Override
                    public void onComplete(long count) {
                        CountDAO.getInstance().setCountNotify(currentPost.getUid(), (int) (count + 1));
                    }

                    @Override
                    public void onFailed(Exception e) {
                        int count = 1;
                        CountDAO.getInstance().setCountNotify(currentPost.getUid(), count);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}