package fu.is1304.dv.fptsocial.dao.callback;

public interface FirestoreDeleteDocCallback {
    public void onComplete();

    public void onFailed(Exception e);
}
