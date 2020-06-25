package fu.is1304.dv.fptsocial.dao.callback;

public interface FirestoreSetCallback {
    public void onSuccess();
    public void onFailure(Exception e);
}
