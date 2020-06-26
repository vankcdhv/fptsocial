package fu.is1304.dv.fptsocial.dao.callback;

public interface FirebaseAuthActionCallBack {
    public void onComplete();

    public void onFailure(Exception e);
}
