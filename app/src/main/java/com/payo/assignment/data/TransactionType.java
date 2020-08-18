package com.payo.assignment.data;

import android.util.Log;

import java.lang.reflect.Field;

public enum TransactionType {
    DEBIT("debit"),
    CREDIT("credit"),
    UNDEFINED("undefined");

    private final String TAG = TransactionType.class.getName();

    TransactionType(String name) {
        try {
            Field fieldName = null;
            if (getClass().getSuperclass() != null) {
                fieldName = getClass().getSuperclass().getDeclaredField("name");
            }
            if (fieldName != null) {
                fieldName.setAccessible(true);
                fieldName.set(this, name);
                fieldName.setAccessible(false);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}
