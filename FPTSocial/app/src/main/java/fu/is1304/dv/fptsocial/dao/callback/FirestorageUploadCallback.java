package fu.is1304.dv.fptsocial.dao.callback;

import com.google.firebase.storage.UploadTask;

public interface FirestorageUploadCallback {
    public void onComplete(UploadTask.TaskSnapshot taskSnapshot);

    public void onFailure(Exception e);
}
