package fu.is1304.dv.fptsocial.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StorageUtils {
    public static Bitmap bytesToBitMap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
