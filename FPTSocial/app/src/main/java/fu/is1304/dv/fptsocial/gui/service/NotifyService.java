package fu.is1304.dv.fptsocial.gui.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.CountDAO;
import fu.is1304.dv.fptsocial.dao.NotificationDAO;
import fu.is1304.dv.fptsocial.dao.PostDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.Notification;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;
import fu.is1304.dv.fptsocial.gui.MainActivity;
import fu.is1304.dv.fptsocial.gui.PostDetailActivity;
import fu.is1304.dv.fptsocial.gui.fragment.MessengerFragment;

public class NotifyService extends Service {
    private int notificationCount = -1;
    private int countNewNotify = 0;

    public NotifyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        if (notificationCount == -1) {
            CountDAO.getInstance().getCountNotificationByUID(AuthController.getInstance().getUID(), new CountDAO.GetCountCallback() {
                @Override
                public void onComplete(long count) {
                    notificationCount = (int) count;
                    CountDAO.getInstance().getRealtimeNotificationByUID(AuthController.getInstance().getUID(), new CountDAO.OnDataChangeEvent() {
                        @Override
                        public void onChange(long count) {
                            countNewNotify = (int) count - notificationCount;
                            notificationCount = (int) count;
                            NotificationDAO.getInstance().getNotifyByUID(AuthController.getInstance().getUID(), countNewNotify, new FirebaseGetCollectionCallback() {
                                @Override
                                public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                                    List<Notification> list = DatabaseUtils.convertListDocSnapToListNotification(documentSnapshots);
                                    for (Notification notification : list) {
                                        createNotify(notification);
                                    }
                                }

                                @Override
                                public void onFailed(Exception e) {

                                }
                            });
                        }
                    });
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotify(final Notification notification) {
        UserDAO.getInstance().getUserByUID(notification.getUid(), new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                PostDAO.getInstance().getPostByID(notification.getPostID(), new FirestoreGetCallback() {
                    @Override
                    public void onComplete(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() == null) return;
                        Post post = DatabaseUtils.convertDocumentSnapshotToPost(documentSnapshot);
                        Intent intent;
                        if (notification.getTitle().equals(Const.POST_NOTIFICATION_TITLE)) {
                            intent = new Intent(NotifyService.this, PostDetailActivity.class);
                            intent.putExtra("post", post);
                        } else {
                            intent = new Intent(NotifyService.this, MessengerFragment.class);
                        }

                        PendingIntent pendingIntent = PendingIntent.getActivity(NotifyService.this, 0, intent, 0);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotifyService.this, Const.CHANEL_ID)
                                .setSmallIcon(R.drawable.ic_action_notification_important)
                                .setContentTitle(notification.getTitle())
                                .setContentText(user.getLastName() + " " + notification.getMessage())
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        builder.setSound(alarmSound);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotifyService.this);
                        notificationManager.notify(Const.POST_NOTIFY_ID, builder.build());
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Const.CHANEL_NAME;
            String description = Const.CHANEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Const.CHANEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
