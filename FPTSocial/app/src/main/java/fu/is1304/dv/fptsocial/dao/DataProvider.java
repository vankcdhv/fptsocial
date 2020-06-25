package fu.is1304.dv.fptsocial.dao;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class DataProvider {
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private static DataProvider instance;

    public static DataProvider getInstance() {
        if (instance == null) instance = new DataProvider();
        return instance;
    }

    public DataProvider() {
        this.database = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }
}
