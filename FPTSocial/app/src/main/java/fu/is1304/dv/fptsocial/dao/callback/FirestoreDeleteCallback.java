package fu.is1304.dv.fptsocial.dao.callback;

public interface FirestoreDeleteCallback {
    public void onComplete();

    public void onFailed(Exception e);
}
