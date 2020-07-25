package fu.is1304.dv.fptsocial.gui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.business.adapter.NewFeedRecylerAdapter;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.common.StorageUtils;
import fu.is1304.dv.fptsocial.dao.CountDAO;
import fu.is1304.dv.fptsocial.dao.NotificationDAO;
import fu.is1304.dv.fptsocial.dao.PostDAO;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageUploadCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreDeleteDocCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Friend;
import fu.is1304.dv.fptsocial.entity.Notification;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.gui.PostDetailActivity;
import fu.is1304.dv.fptsocial.gui.WallActivity;
import fu.is1304.dv.fptsocial.gui.service.NotifyService;
import fu.is1304.dv.fptsocial.gui.viewmodel.MainActivityViewModel;

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
    private NewFeedRecylerAdapter newFeedAdapter;
    private RecyclerView lvNewFeed;
    private TextView labelStatus;
    private List<Post> listPost;
    private SwipeRefreshLayout refreshLayout;

    private int currentPage, countPage, countPost, countNotification;
    private Map<Integer, DocumentSnapshot> position;

    private MainActivityViewModel viewModel;

    private TextView btnNextPage, btnPrevPage, labelPaging;


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
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        lvNewFeed = v.findViewById(R.id.recyclerListPost);
        labelStatus = v.findViewById(R.id.labelStatus);
        btnNextPage = v.findViewById(R.id.labelNextNewfeedPage);
        btnPrevPage = v.findViewById(R.id.labelPreNewfeedPage);
        labelPaging = v.findViewById(R.id.labelPagingNewfeed);
        refreshLayout = v.findViewById(R.id.swipeRefreshNewFeed);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                refreshLayout.setRefreshing(false);
            }
        });

        labelStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPostDialog();
            }
        });

        listPost = new ArrayList<>();

        newFeedAdapter = new NewFeedRecylerAdapter(getContext(), listPost, new NewFeedRecylerAdapter.EventListener() {
            @Override
            public void onClickEdit(final Post post) {
                editPostDialog(post);
            }

            @Override
            public void onClickDelete(final Post post) {
                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(getContext());
                confirmDialog.setMessage("Bạn chắc chắn muốn xóa?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PostDAO.getInstance().deleteStatus(post, new FirestoreDeleteDocCallback() {
                                    @Override
                                    public void onComplete() {
                                        Toast.makeText(getActivity(), "Đã xóa!", Toast.LENGTH_SHORT).show();
                                        countPost--;
                                        refreshList();
                                        changePaging();
                                    }

                                    @Override
                                    public void onFailed(Exception e) {
                                        Toast.makeText(getActivity(), "Xóa không thành công!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                confirmDialog.show();
            }

            @Override
            public void onClickTitle(Post post) {
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("post", post);
                startActivity(intent);
            }

            @Override
            public void onAuthorClick(String uid) {
                Intent intent = new Intent(getActivity(), WallActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        lvNewFeed.setLayoutManager(layoutManager);
        lvNewFeed.setHasFixedSize(true);
        lvNewFeed.setItemViewCacheSize(20);
        lvNewFeed.setAdapter(newFeedAdapter);

        initDialog();

        //Add event and init paging
        currentPage = 1;
        position = new HashMap<>();
        position.put(1, null);
        btnPrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearListPost();
                getAllPost(Const.PREV_PAGE_CASE);
            }
        });
        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearListPost();
                getAllPost(Const.NEXT_PAGE_CASE);
            }
        });
        initPaging();

        //Get list post the first time (page 1)
        getAllPost(Const.RELOAD_PAGE_CASE);
    }

    private void initPaging() {
        CountDAO.getInstance().getCount(Const.POST_COLLECTION, new CountDAO.GetCountCallback() {
            @Override
            public void onComplete(long count) {
                countPost = (int) count;
                countPage = (int) (count / Const.NUMBER_ITEMS_OF_NEW_FEED + (count % Const.NUMBER_ITEMS_OF_NEW_FEED == 0 ? 0 : 1));
                labelPaging.setText(currentPage + "/" + countPage);
                if (currentPage >= countPage) btnNextPage.setVisibility(View.INVISIBLE);
                else btnNextPage.setVisibility(View.VISIBLE);
                if (currentPage <= 1) btnPrevPage.setVisibility(View.INVISIBLE);
                else btnPrevPage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(getContext(), "False", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePaging() {
        countPage = countPost / Const.NUMBER_ITEMS_OF_NEW_FEED + (countPost % Const.NUMBER_ITEMS_OF_NEW_FEED == 0 ? 0 : 1);
        labelPaging.setText(currentPage + "/" + countPage);
        if (currentPage >= countPage) btnNextPage.setVisibility(View.INVISIBLE);
        else btnNextPage.setVisibility(View.VISIBLE);
        if (currentPage <= 1) btnPrevPage.setVisibility(View.INVISIBLE);
        else btnPrevPage.setVisibility(View.VISIBLE);
    }

    private void refreshList() {
        clearListPost();
        getAllPost(Const.RELOAD_PAGE_CASE);
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
    }

    //Open post status dialog
    private void openPostDialog() {
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStatus();
            }
        });
        postDialog.show();
    }

    //Open post status dialog to edit post
    private void editPostDialog(final Post post) {
        txtPostTitle.setText(post.getTitle());
        txtPostContent.setText(post.getContent());
        if (post.getImage() != null) {
            Glide.with(getActivity()).load(post.getImage()).into(imgPostImage);
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
            uploadImage(post, Const.MODE_CREATE_STATUS);
        } else {
            createStatus(post);
        }
    }

    public void editPost(Post post) {
        String oldImage = post.getImage();
        String title = txtPostTitle.getText().toString();
        String content = txtPostContent.getText().toString();
        String uid = AuthController.getInstance().getUID();
        Date postDate = post.getPostDate();
        if (statusImage != null && statusImage != oldImage) {
            post = new Post(post.getId(), uid, title, content, statusImage, postDate);
            uploadImage(post, Const.MODE_UPDATE_STATUS);
        } else {
            post = new Post(post.getId(), uid, title, content, post.getImage(), postDate);
            updateStatus(post);
        }
    }

    //Upload image if user selected one image
    private void uploadImage(final Post post, final int mode) {
        StorageDAO.getInstance().upImage(statusImage, imageUri, new FirestorageUploadCallback() {
            @Override
            public void onComplete(Uri uri) {
                String url = uri.toString();
                post.setImage(url);
                if (mode == Const.MODE_CREATE_STATUS)
                    createStatus(post);
                else
                    updateStatus(post);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Update your image failed, your post will not create", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Create post on database
    private void createStatus(Post post) {
        PostDAO.getInstance().postStatus(post, new FirestoreSetCallback() {
            @Override
            public void onSuccess(String postID) {
                Toast.makeText(getActivity(), "Đã đăng bài thành công", Toast.LENGTH_SHORT).show();
                postDialog.dismiss();
                countPost++;
                //CountDAO.getInstance().setCount(Const.POST_COLLECTION, countPost);
                refreshList();
                changePaging();
                initDialog();
                makeNotify(postID);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Có lỗi xảy ra! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Make notification after post
    private void makeNotify(String postID) {
        String title = Const.POST_NOTIFICATION_TITLE;
        String message = "Vừa đăng một trạng thái mới!";
        Date time = new Date();
        String uid = AuthController.getInstance().getUID();
        Boolean seen = false;

        Notification notification = new Notification(title, message, time, uid, postID, seen);
        List<Friend> list = viewModel.getListFriend();
        for (final Friend friend : list) {
            NotificationDAO.getInstance().createNotification(friend.getUid(), notification, new FirestoreSetCallback() {
                @Override
                public void onSuccess(String notiID) {
                    CountDAO.getInstance().getCountNotificationByUID(friend.getUid(), new CountDAO.GetCountCallback() {
                        @Override
                        public void onComplete(long count) {
                            countNotification = (int) count;
                            countNotification++;
                            CountDAO.getInstance().setCountNotify(friend.getUid(), countNotification);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            countNotification = 1;
                            CountDAO.getInstance().setCountNotify(friend.getUid(), countNotification);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }

    //Update post on database
    private void updateStatus(Post post) {
        PostDAO.getInstance().updatePost(post, new FirestoreSetCallback() {
            @Override
            public void onSuccess(String postID) {
                Toast.makeText(getActivity(), "Đã cập nhật bài thành công", Toast.LENGTH_SHORT).show();
                postDialog.dismiss();
                refreshList();
                initDialog();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Có lỗi xảy ra! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearListPost() {
        int size = listPost.size();
        listPost.clear();
        newFeedAdapter.notifyItemRangeRemoved(0, size);
        newFeedAdapter.notifyDataSetChanged();
    }

    //Get all post and show posts to listview
    private void getAllPost(int mode) {
        DocumentSnapshot lastPost = null;
        switch (mode) {
            case Const.NEXT_PAGE_CASE:
                currentPage++;
                break;
            case Const.PREV_PAGE_CASE:
                currentPage--;
                break;
        }
        if (currentPage > countPage) currentPage = 1;
        if (currentPage < 1) currentPage = countPage;
        lastPost = position.get(currentPage);
        final DocumentSnapshot finalLastPost = lastPost;
        PostDAO.getInstance().getListPost(lastPost, new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                initPaging();
                int index = listPost == null ? 0 : listPost.size();
                List<Post> list = DatabaseUtils.convertListDocSnapToListPost(documentSnapshots);
                //Set last post for next page
                if (currentPage < countPage) {
                    position.put(currentPage + 1, documentSnapshots.get(list.size() - 1));
                }

                listPost.addAll(list);
                newFeedAdapter.notifyItemRangeInserted(index, list.size());
                newFeedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(getActivity(), "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
            }
        });

    }
}