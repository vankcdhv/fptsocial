package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.common.StorageUtils;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;

public class PostDetailActivity extends AppCompatActivity {

    private TextView txtTitle, txtContent, txtAuthor, txtTime;
    private ImageView imgSmallAva, imgStatusImage, btnStatusMenu;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initComponents();
    }

    private void initComponents() {
        txtTitle = findViewById(R.id.txtDetailNewfeedTitle);
        txtContent = findViewById(R.id.txtDetailNewfeedContent);
        txtAuthor = findViewById(R.id.txtDetailNewfeedAuthor);
        txtTime = findViewById(R.id.txtDetailNewfeedTime);
        imgSmallAva = findViewById(R.id.imgDetailSmallAva);
        imgStatusImage = findViewById(R.id.imgDetailNewfeedImage);
        btnStatusMenu = findViewById(R.id.btnDetailPostMenu);
        data = getIntent();
        loadData();
    }

    private void loadData() {
        final Post post = (Post) data.getSerializableExtra("post");
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
                if (user.getAvatar() == null) {
                    if (user.getGender() == (getString(R.string.female))) {
                        imgSmallAva.setImageDrawable(getDrawable(R.drawable.nu));
                    }
                } else {
                    StorageDAO.getInstance().getImage(user.getAvatar(), new FirestorageGetByteCallback() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onComplete(byte[] bytes) {
                            Bitmap bitmap = StorageUtils.bytesToBitMap(bytes);
                            imgSmallAva.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onFailed(Exception e) {
                        }
                    });
                }

                if (post.getImage() == null) {
                } else {

                    StorageDAO.getInstance().getImage(post.getImage() + "_900x900", new FirestorageGetByteCallback() {
                        @Override
                        public void onStart() {
                            Glide.with(PostDetailActivity.this).load(getDrawable(R.drawable.loading)).into(imgStatusImage);
                        }

                        @Override
                        public void onComplete(byte[] bytes) {
                            Bitmap bitmap = StorageUtils.bytesToBitMap(bytes);
                            imgStatusImage.setImageBitmap(bitmap);
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
    }


}