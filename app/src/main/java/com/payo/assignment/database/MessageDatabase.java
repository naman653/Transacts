package com.payo.assignment.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.payo.assignment.dao.MessageDao;
import com.payo.assignment.data.Message;

@Database(entities = {Message.class}, version = 1, exportSchema = false)
public abstract class MessageDatabase extends RoomDatabase {

    public abstract MessageDao messageDao();

    private static MessageDatabase INSTANCE;

    public static MessageDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MessageDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MessageDatabase.class, "message_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
