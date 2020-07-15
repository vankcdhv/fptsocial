package fu.is1304.dv.fptsocial.gui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.business.NewFeedAdapter;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.PostDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
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

    private NewFeedAdapter newFeedAdapter;
    private ListView lvNewFeed;
    List<Post> listPost;

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
        listPost = new ArrayList<>();
        newFeedAdapter = new NewFeedAdapter(getContext(), 0, listPost);
        lvNewFeed.setAdapter(newFeedAdapter);
        getAllPost();
    }

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