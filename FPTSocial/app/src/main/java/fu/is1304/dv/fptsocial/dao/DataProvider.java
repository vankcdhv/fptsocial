package fu.is1304.dv.fptsocial.dao;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataProvider {
    private FirebaseFirestore database;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private static DataProvider instance;

    public static DataProvider getInstance(){
        if(instance==null) instance = new DataProvider();
        return instance;
    }

    public DataProvider() {
        this.database = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.currentUser = firebaseAuth.getCurrentUser();
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }
}
