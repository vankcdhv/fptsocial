package fu.is1304.dv.fptsocial.gui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<String> messageID = new MutableLiveData<>();

    public String getMessageID() {
        return messageID.getValue();
    }

    public void setMessageID(String messageID) {
        this.messageID.setValue(messageID);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
