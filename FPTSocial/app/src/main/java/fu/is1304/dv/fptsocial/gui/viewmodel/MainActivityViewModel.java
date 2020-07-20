package fu.is1304.dv.fptsocial.gui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import fu.is1304.dv.fptsocial.entity.Friend;

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<String> messageID = new MutableLiveData<>();
    private MutableLiveData<List<Friend>> listFriend = new MutableLiveData<>();

    public String getMessageID() {
        return messageID.getValue();
    }

    public void setMessageID(String messageID) {
        this.messageID.setValue(messageID);
    }

    public List<Friend> getListFriend() {
        return listFriend.getValue();
    }

    public void setListFriend(List<Friend> listFriend) {
        this.listFriend.setValue(listFriend);
    }

    public void addFriend(Friend friend) {
        this.listFriend.getValue().add(friend);
    }

    public void cancelFriend(Friend friend) {
        this.listFriend.getValue().remove(friend);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
