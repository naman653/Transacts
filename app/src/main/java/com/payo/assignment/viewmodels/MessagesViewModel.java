package com.payo.assignment.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.payo.assignment.data.Message;
import com.payo.assignment.repository.MessageRepository;

import java.util.List;

public class MessagesViewModel extends ViewModel {

    private MutableLiveData<List<Message>> messages = new MutableLiveData<>();

    public MutableLiveData<List<Message>> getMessages(Context context) {
        messages = MessageRepository.getInstance().fetchMessages(context);
        return messages;
    }

    public MutableLiveData<Double> getAmountCredited() {
        return MessageRepository.getInstance().getAmountCredited();
    }

    public MutableLiveData<Double> getAmountDebited() {
        return MessageRepository.getInstance().getAmountDebited();
    }

    public MutableLiveData<Double> getAmountUndefined() {
        return MessageRepository.getInstance().getAmountUndefined();
    }
}
