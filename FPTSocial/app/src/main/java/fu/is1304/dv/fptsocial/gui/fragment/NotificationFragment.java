package fu.is1304.dv.fptsocial.gui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.business.adapter.NotificationRecyclerAdapter;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.NotificationDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Notification;
import fu.is1304.dv.fptsocial.gui.viewmodel.MainActivityViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ViewModel viewModel;
    private RecyclerView listNotification;
    private NotificationRecyclerAdapter adapter;
    private List<Notification> notifications;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        initComponents(v);
        return v;
    }

    private void initComponents(View view) {
        listNotification = view.findViewById(R.id.recyclerListNotification);

        notifications = new ArrayList<>();
        adapter = new NotificationRecyclerAdapter(getContext(), notifications, new NotificationRecyclerAdapter.OnEventListener() {
            @Override
            public void onClickNotify(Notification notification) {
                clickNotification(notification);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listNotification.setLayoutManager(layoutManager);
        listNotification.setHasFixedSize(true);
        listNotification.setItemViewCacheSize(20);
        listNotification.setAdapter(adapter);
        loadAllNotification();
    }

    private void loadAllNotification() {
        NotificationDAO.getInstance().getAllNotification(AuthController.getInstance().getUID(), new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                List<Notification> list = DatabaseUtils.convertListDocSnapToListNotification(documentSnapshots);
                notifications.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void clickNotification(final Notification notification) {
        final Notification noti = notification;
        noti.setSeen(true);
        NotificationDAO.getInstance().updateNotification(AuthController.getInstance().getUID(), noti, new FirestoreSetCallback() {
            @Override
            public void onSuccess() {
                notifications.set(notifications.indexOf(notification), noti);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }


}