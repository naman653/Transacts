package com.payo.assignment.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.payo.assignment.data.Message;
import com.payo.assignment.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MessageRepository {

    private volatile static MessageRepository INSTANCE = null;

    private MutableLiveData<Double> amountCredited;
    private MutableLiveData<Double>  amountDebited;
    private MutableLiveData<Double>  amountUndefined;

    private MessageRepository() {
        amountCredited = new MutableLiveData<>();
        amountCredited.setValue(0.0);
        amountDebited = new MutableLiveData<>();
        amountDebited.setValue(0.0);
        amountUndefined = new MutableLiveData<>();
        amountUndefined.setValue(0.0);
    }

    public static MessageRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (MessageRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MessageRepository();
                }
            }
        }
        return INSTANCE;
    }

    public MutableLiveData<List<Message>> fetchMessages(Context context) {
        MutableLiveData<List<Message>> messages = new MutableLiveData<>();

        getMessages(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msgList -> messages.setValue(msgList));

        return messages;
    }

    private Observable<List<Message>> getMessages(Context context) {
        ArrayList<Message> messageList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"),
                null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    String msgAddress = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    String msgData = cursor.getString(cursor.getColumnIndexOrThrow("body"));

                    if (Utils.isValidMessage(msgData, msgAddress)) {
                        Message message = new Message();
                        message.setSenderId(msgAddress);
                        message.setAmount(Utils.getAmount(msgData));
                        message.setType(Utils.getMessageType(msgData));
                        message.setBody(msgData);
                        messageList.add(message);
                        switch (message.getType()) {
                            case UNDEFINED:
                                amountUndefined.setValue(amountUndefined.getValue() + message.getAmount());
                                break;
                            case DEBIT:
                                amountDebited.setValue(amountDebited.getValue() + message.getAmount());
                                break;
                            case CREDIT:
                                amountCredited.setValue(amountCredited.getValue() + message.getAmount());
                                break;
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return Observable.just(messageList);
    }
    public MutableLiveData<Double> getAmountCredited() {
        return amountCredited;
    }

    public MutableLiveData<Double> getAmountDebited() {
        return amountDebited;
    }

    public MutableLiveData<Double> getAmountUndefined() {
        return amountUndefined;
    }
}
