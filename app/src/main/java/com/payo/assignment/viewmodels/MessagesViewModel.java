package com.payo.assignment.viewmodels;

import android.content.Context;

import androidx.core.graphics.drawable.IconCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.payo.assignment.data.Message;
import com.payo.assignment.repository.MessageRepository;

import java.util.List;

public class MessagesViewModel extends ViewModel {

    private MutableLiveData<List<Message>> messages = new MutableLiveData<>();

    public LiveData<List<Message>> getMessages(Context context) {
        return MessageRepository.getInstance(context).fetchMessages(context);
    }

    public MutableLiveData<Double> getAmountCredited(Context context) {
        return MessageRepository.getInstance(context).getAmountCredited();
    }

    public MutableLiveData<Double> getAmountDebited(Context context) {
        return MessageRepository.getInstance(context).getAmountDebited();
    }

    public MutableLiveData<Double> getAmountUndefined(Context context) {
        return MessageRepository.getInstance(context).getAmountUndefined();
    }
}
