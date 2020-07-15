package fu.is1304.dv.fptsocial.dao.callback;

public interface FirestorageGetByteCallback {
    public void onStart();

    public void onComplete(byte[] bytes);

    public void onFailed(Exception e);
}
