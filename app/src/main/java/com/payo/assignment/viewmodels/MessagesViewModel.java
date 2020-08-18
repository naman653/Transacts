package com.payo.assignment.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.payo.assignment.data.Message;
import com.payo.assignment.repository.MessageRepository;

import java.util.List;

public class MessagesViewModel extends ViewModel {

    public LiveData<List<Message>> getMessages(Context context) {
        return MessageRepository.getInstance(context).fetchMessages(context);
    }

    public LiveData<List<Message>> filterMessages(Context context, String type) {
        return MessageRepository.getInstance(context).fetchTypeMessages(type);
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
