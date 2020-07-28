package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.adapter.ListChatAdapter;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.MessageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.Message;
import fu.is1304.dv.fptsocial.entity.User;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView imgAvatar;
    private TextView labelUserName;
    private RecyclerView listChat;
    private EditText txtChat;
    private ImageButton btnSend;

    private ListChatAdapter listChatAdapter;
    private List<Message> messages;
    private List<String> listPeople;
    private Intent intent;
    private String uid;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
        initComp();
    }

    private void initComp() {
        imgAvatar = findViewById(R.id.imgAvatar);
        labelUserName = findViewById(R.id.labelName);
        listChat = findViewById(R.id.frameChat);
        btnSend = findViewById(R.id.btnSend);
        txtChat = findViewById(R.id.txtChat);

        messages = new ArrayList<>();
        listChatAdapter = new ListChatAdapter(this, messages, new ListChatAdapter.OnEventListener() {
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listChat.setLayoutManager(layoutManager);
        listChat.setHasFixedSize(true);
        listChat.setAdapter(listChatAdapter);
        getData();
        setEvent();
    }

    private void setEvent() {
        labelUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, WallActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        intent = getIntent();
        uid = intent.getStringExtra("uid");
        UserDAO.getInstance().getUserByUID(uid, new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                if (user.getAvatar() == null) {
                    if (user.getGender().equals(getString(R.string.female))) {
                        imgAvatar.setImageDrawable(getDrawable(R.drawable.nu));
                    } else {
                        imgAvatar.setImageDrawable(getDrawable(R.drawable.nam));
                    }
                } else {
                    Glide.with(ChatActivity.this).load(user.getAvatar()).into(imgAvatar);
                }
                labelUserName.setText(user.getFirstName() + " " + user.getLastName());
                MessageDAO.getInstance().getMessageByUID(uid, new FirebaseGetCollectionCallback() {
                    @Override
                    public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                        List<Message> list = DatabaseUtils.convertListDocSnapToListMessage(documentSnapshots);
                        messages.addAll(list);
                        listChatAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public void send(View v) {
        String content = txtChat.getText().toString();
        if (!content.isEmpty()) {
            Message message = new Message();
            message.setContent(content);
            message.setSend(true);
            message.setTimeSend(new Date());
            message.setUid(uid);
        }
    }
}