package com.payo.assignment.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.payo.assignment.dao.MessageDao;
import com.payo.assignment.data.Message;
import com.payo.assignment.data.TransactionType;
import com.payo.assignment.database.MessageDatabase;
import com.payo.assignment.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MessageRepository {

    private volatile static MessageRepository INSTANCE = null;

    private boolean cursorFetched;

    private MutableLiveData<Double> amountCredited;
    private MutableLiveData<Double>  amountDebited;
    private MutableLiveData<Double>  amountUndefined;
    private MessageDao msgDao;

    private MessageRepository(Context context) {
        amountCredited = new MutableLiveData<>();
        amountCredited.setValue(0.0);
        amountDebited = new MutableLiveData<>();
        amountDebited.setValue(0.0);
        amountUndefined = new MutableLiveData<>();
        amountUndefined.setValue(0.0);
        cursorFetched = false;
        msgDao = MessageDatabase.getDatabase(context).messageDao();
    }

    public static MessageRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MessageRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MessageRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<Message>> fetchMessages(Context context) {

        MutableLiveData<List<Message>> messages = new MutableLiveData<>();

        if (!cursorFetched) {
            getMessages(context)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(msgList -> {
                        messages.setValue(msgList);
                        cursorFetched = true;
                    });
        }

        return msgDao.getAllMessages();
    }

    public LiveData<List<Message>> fetchTypeMessages(String type) {
        return msgDao.getMessages(type);
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
                    String msgId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

                    if (Utils.isValidMessage(msgData, msgAddress)) {
                        Message message = new Message();
                        message.setMsgId(msgId);
                        message.setSenderId(msgAddress);
                        message.setAmount(Utils.getAmount(msgData));
                        message.setType(Utils.getMessageType(msgData));
                        message.setBody(msgData);
                        messageList.add(message);
                        switch (TransactionType.valueOf(message.getType())) {
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
                        new InsertAsyncTask(msgDao).execute(message);
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

    private static class InsertAsyncTask extends AsyncTask<Message, Void, Void> {

        private MessageDao mAsyncTaskDao;

        InsertAsyncTask(MessageDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Message... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
