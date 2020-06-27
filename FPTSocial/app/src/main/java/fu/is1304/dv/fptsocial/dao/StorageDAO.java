package fu.is1304.dv.fptsocial.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageUploadCallback;

public class StorageDAO {
    private static StorageDAO instance;

    public static StorageDAO getInstance() {
        if (instance == null) instance = new StorageDAO();
        return instance;
    }

    public void getImage(String path, final FirestorageGetByteCallback callback) {
        DataProvider.getInstance()
                .getStorage()
                .getReference()
                .child(path)
                .getBytes(1024 * 1024 * 20)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        callback.onComplete(bytes);
                    }
                });
    }

    public void upImage(String path, Uri file, final FirestorageUploadCallback callback) {
        StorageReference reference = DataProvider.getInstance().getStorage().getReference().child(path);
        UploadTask uploadTask = reference.putFile(file);
        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        callback.onComplete(taskSnapshot);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });

    }
}
