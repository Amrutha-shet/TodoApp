package com.example.todolistapp.database;

import android.content.Context;

import androidx.room.Room;

public class ToDoDatabaseClient {
    private static final String TODO_DATABASE = "todo_database";

    private static ToDoDatabaseClient instance;

    private TodoDatabase todoDatabase;

    private ToDoDatabaseClient(Context context) {
        todoDatabase = Room.databaseBuilder(context, TodoDatabase.class, TODO_DATABASE).build();
    }

    public static synchronized ToDoDatabaseClient getInstance(Context mCtx) {
        if (instance == null) {
            instance = new ToDoDatabaseClient(mCtx);
        }
        return instance;
    }

    public TodoDatabase getTodoDatabase() {
        return todoDatabase;
    }
}