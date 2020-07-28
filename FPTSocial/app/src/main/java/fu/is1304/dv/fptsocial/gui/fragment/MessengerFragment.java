package fu.is1304.dv.fptsocial.gui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.adapter.FriendMessageAdapter;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.MessageDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.FriendMessage;
import fu.is1304.dv.fptsocial.entity.Message;
import fu.is1304.dv.fptsocial.gui.ChatActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessengerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessengerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Variable
    private List<FriendMessage> listFriendMessage;
    private RecyclerView recyclerView;
    private FriendMessageAdapter friendMessageAdapter;

    public MessengerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessengerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessengerFragment newInstance(String param1, String param2) {
        MessengerFragment fragment = new MessengerFragment();
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
        View v = inflater.inflate(R.layout.fragment_messenger, container, false);
        init(v);
        return v;
    }

    public void init(View v) {

        recyclerView = v.findViewById(R.id.recyclerListFriendMessage);

        listFriendMessage = new ArrayList<>();

        //Init adapter for recyclerview
        friendMessageAdapter = new FriendMessageAdapter(getContext(), listFriendMessage, new FriendMessageAdapter.OnEventListener() {
            @Override
            public void onClickMessage(FriendMessage friendMessage) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("uid", friendMessage.getUid());
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(friendMessageAdapter);

        getData();
    }

    public void getData() {
        MessageDAO.getInstance().getListFriendMessage(new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final List<String> listPeople = new ArrayList<>();
                if (documentSnapshot.getData() == null) return;
                listPeople.addAll((Collection<? extends String>) documentSnapshot.getData().get("list"));
                for (final String uid : listPeople) {
                    MessageDAO.getInstance().getLastMessageByUID(uid, new FirebaseGetCollectionCallback() {
                        @Override
                        public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                            List<Message> messages = DatabaseUtils.convertListDocSnapToListMessage(documentSnapshots);
                            for (Message message : messages) {
                                if (message.getUid().equals(uid)) {
                                    FriendMessage friendMessage = new FriendMessage();
                                    friendMessage.setUid(uid);
                                    friendMessage.setLastestMessage(message.getContent());
                                    friendMessage.setTime(message.getTimeSend());
                                    listFriendMessage.add(friendMessage);
                                    if (uid.equals(listPeople.get(listPeople.size() - 1))) {
                                        friendMessageAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
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

    private void changeData() {
    }


}