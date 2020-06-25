package fu.is1304.dv.fptsocial.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnSuccessListener;

import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;

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
}
