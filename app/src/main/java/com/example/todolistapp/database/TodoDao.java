package com.example.todolistapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {

    @Query("SELECT * FROM todo ORDER BY isCompleted ASC")
    List<Todo> getAll();

    @Query("SELECT * FROM todo WHERE title LIKE '%' || :searchKey || '%'")
    List<Todo> search(String searchKey);

    @Insert
    void insert(Todo todo);

    @Update
    void update(Todo todo);

    @Delete
    void delete(Todo todo);
}