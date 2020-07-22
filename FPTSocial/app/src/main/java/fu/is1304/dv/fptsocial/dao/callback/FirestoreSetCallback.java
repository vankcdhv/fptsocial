package fu.is1304.dv.fptsocial.dao.callback;

public interface FirestoreSetCallback {
    public void onSuccess(String id);

    public void onFailure(Exception e);
}
