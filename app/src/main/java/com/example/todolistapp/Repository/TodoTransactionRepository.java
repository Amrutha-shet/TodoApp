package com.example.todolistapp.Repository;

import android.content.Context;
import android.text.TextUtils;

import com.example.todolistapp.database.ToDoDatabaseClient;
import com.example.todolistapp.database.Todo;
import com.example.todolistapp.database.TodoDatabase;

import java.util.Collection;
import java.util.List;

/**
 * Created by Amrutha on 27/06/20.
 */
public class TodoTransactionRepository {
    private TodoDatabase database;

    public TodoTransactionRepository(Context context) {
        database = ToDoDatabaseClient.getInstance(context).getTodoDatabase();
    }

    public void insertOrUpdate(Todo user) {
        if (user.getUid() > 0)  database.toDoDao().update(user);
        else database.toDoDao().insert(user);
    }

    public List<Todo> getTodoList(String searchkey) {
        if (TextUtils.isEmpty(searchkey)) {
            return database.toDoDao().getAll();
        } else  return database.toDoDao().search(searchkey);
    }
}
