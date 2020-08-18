package com.payo.assignment.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.payo.assignment.data.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("DELETE FROM message")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Message msg);

    @Query("SELECT * FROM Message")
    LiveData<List<Message>> getAllMessages();

    @Query("SELECT * FROM Message WHERE type = :type")
    LiveData<List<Message>> getMessages(String type);
}
