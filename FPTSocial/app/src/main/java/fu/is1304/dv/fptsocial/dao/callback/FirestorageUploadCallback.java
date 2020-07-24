package fu.is1304.dv.fptsocial.dao.callback;

import android.net.Uri;

import com.google.firebase.storage.UploadTask;

public interface FirestorageUploadCallback {
    public void onComplete(Uri uri);

    public void onFailure(Exception e);
}
