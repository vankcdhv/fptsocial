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
import fu.is1304.dv.fptsocial.business.adapter.ListChatAdapter;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.MessageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Message;
import fu.is1304.dv.fptsocial.entity.User;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView imgAvatar;
    private TextView labelUserName;
    private RecyclerView listChat;
    private EditText txtChat;
    private ImageButton btnSend;

    private LinearLayoutManager layoutManager;
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
        getSupportActionBar().hide();
        imgAvatar = findViewById(R.id.imgAvatar);
        labelUserName = findViewById(R.id.labelName);
        listChat = findViewById(R.id.frameChat);
        btnSend = findViewById(R.id.btnSend);
        txtChat = findViewById(R.id.txtChat);

        messages = new ArrayList<>();
        listChatAdapter = new ListChatAdapter(this, messages, new ListChatAdapter.OnEventListener() {
        });

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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
        MessageDAO.getInstance().realtimeChat(uid, new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                List<Message> list = DatabaseUtils.convertListDocSnapToListMessage(documentSnapshots);
                messages.clear();
                listChatAdapter.notifyDataSetChanged();
                messages.addAll(list);
                listChatAdapter.notifyDataSetChanged();
                layoutManager.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onFailed(Exception e) {

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
//                        List<Message> list = DatabaseUtils.convertListDocSnapToListMessage(documentSnapshots);
//                        messages.addAll(list);
//                        listChatAdapter.notifyDataSetChanged();
//                        layoutManager.scrollToPosition(messages.size() - 1);
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
            txtChat.setEnabled(false);
            btnSend.setEnabled(false);
            final Message message_send = new Message();
            Message message_receive = new Message();

            message_send.setContent(content);
            message_send.setSend(true);
            message_send.setTimeSend(new Date());
            message_send.setUid(uid);

            message_receive.setContent(content);
            message_receive.setSend(false);
            message_receive.setTimeSend(new Date());
            message_receive.setUid(AuthController.getInstance().getUID());
            MessageDAO.getInstance().sendMessage(message_send, message_receive, new FirestoreSetCallback() {
                @Override
                public void onSuccess(String id) {
                    txtChat.setEnabled(true);
                    btnSend.setEnabled(true);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ChatActivity.this, R.string.have_error, Toast.LENGTH_LONG).show();
                    txtChat.setEnabled(true);
                    btnSend.setEnabled(true);
                }
            });
        }
        txtChat.setText("");
    }
}