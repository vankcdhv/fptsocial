package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.adapter.SearchPeopleAdapter;
import fu.is1304.dv.fptsocial.business.adapter.SearchPostAdapter;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.PostDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;

public class SearchActivity extends AppCompatActivity {

    private Button btnPeople, btnPost;
    private TextView labelTitle;
    private RecyclerView listResult;
    private SearchPostAdapter searchPostAdapter;
    private SearchPeopleAdapter searchPeopleAdapter;
    private List<User> userList;
    private List<Post> postList;
    private Intent data;
    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initComponents();
    }

    private void initComponents() {
        getSupportActionBar().hide();
        btnPeople = findViewById(R.id.btnPeople);
        btnPost = findViewById(R.id.btnPost);
        labelTitle = findViewById(R.id.labelSearchTitle);
        listResult = findViewById(R.id.recyclerListResult);

        btnPeople.setTextColor(Color.BLACK);
        btnPost.setTextColor(Color.GRAY);
        labelTitle.setText(R.string.people);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPost.setTextColor(Color.BLACK);
                btnPeople.setTextColor(Color.GRAY);
                labelTitle.setText(R.string.post);
                searchPost();
            }
        });

        btnPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPost.setTextColor(Color.GRAY);
                btnPeople.setTextColor(Color.BLACK);
                labelTitle.setText(R.string.people);
                searchPeople();
            }
        });

        userList = new ArrayList<>();
        postList = new ArrayList<>();

        searchPeopleAdapter = new SearchPeopleAdapter(this, userList, new SearchPeopleAdapter.OnEventListener() {
            @Override
            public void onClickPeople(User user) {
                openWallActivity(user);
            }
        });

        searchPostAdapter = new SearchPostAdapter(this, postList, new SearchPostAdapter.OnEventListener() {
            @Override
            public void onClickPost(Post post) {
                openPostDetail(post);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listResult.setLayoutManager(layoutManager);
        listResult.setHasFixedSize(true);
        listResult.setAdapter(searchPeopleAdapter);
        loadData();
    }

    private void loadData() {
        data = getIntent();
        keyWord = data.getStringExtra("keyword");
        searchPeople();
    }

    private void searchPeople() {
        listResult.setAdapter(searchPeopleAdapter);
        userList.clear();
        searchPeopleAdapter.notifyDataSetChanged();
        UserDAO.getInstance().searchUserByName(keyWord, new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                List<User> list = DatabaseUtils.convertListDocumentSnapshotToListUser(documentSnapshots);
                userList.addAll(list);
                searchPeopleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void searchPost() {
        listResult.setAdapter(searchPostAdapter);
        postList.clear();
        searchPostAdapter.notifyDataSetChanged();
        PostDAO.getInstance().searchPost(keyWord, new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                List<Post> list = DatabaseUtils.convertListDocSnapToListPost(documentSnapshots);
                postList.addAll(list);
                searchPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void openWallActivity(User user) {
        Intent intent = new Intent(this, WallActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    private void openPostDetail(Post post) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }
}