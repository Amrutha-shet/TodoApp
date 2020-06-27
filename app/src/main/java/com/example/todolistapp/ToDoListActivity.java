package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolistapp.adapter.ToDoListAdapter;
import com.example.todolistapp.database.Todo;
import com.example.todolistapp.databinding.ActivityTodoListBinding;
import com.example.todolistapp.viewModel.ToDoViewModel;

import java.util.ArrayList;

public class ToDoListActivity extends AppCompatActivity implements ToDoListAdapter.TodoItemClickListener {
    protected static final int TODO_REQUEST_CODE = 101;
    protected static final String TODO_ITEM = "todo_item";

    private ActivityTodoListBinding activityTodoListBinding;
    private ToDoViewModel toDoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTodoListBinding = DataBindingUtil.setContentView(this, R.layout.activity_todo_list);
        toDoViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(ToDoViewModel.class);
        initView();
        initObserver();
        toDoViewModel.fetchTodoList("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TODO_REQUEST_CODE && data != null) {
            Todo todo = (Todo) data.getSerializableExtra(TODO_ITEM);
            activityTodoListBinding.progressBar.setVisibility(View.VISIBLE);
            toDoViewModel.insertOrUpdate(todo);
        }
    }

    @Override
    public void onTodoItemClick(Todo todo) {
        toDoViewModel.insertOrUpdate(todo);
    }

    private void initView() {
        activityTodoListBinding.toolbar.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToDoListActivity.this, AddTodoActivity.class);
                startActivityForResult(intent, TODO_REQUEST_CODE);
            }
        });

        activityTodoListBinding.toolbar.edtSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                toDoViewModel.fetchTodoList(newText);
                // new Handler().postDelayed(() -> toDoViewModel.fetchTodoList(newText), 300);
                return false;
            }
        });

        activityTodoListBinding.rvTodo.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initObserver() {
        toDoViewModel.getTodoList().observe(this, todos -> {
            setAdapter(todos);
        });

        toDoViewModel.getProgress().observe(this, isProgress -> {
            if (isProgress) activityTodoListBinding.progressBar.setVisibility(View.VISIBLE);
            else activityTodoListBinding.progressBar.setVisibility(View.GONE);
        });

        toDoViewModel.getNoData().observe(this, noData -> {
            if (noData) activityTodoListBinding.tvNoResult.setVisibility(View.VISIBLE);
            else activityTodoListBinding.tvNoResult.setVisibility(View.GONE);
        });
    }

    private void setAdapter(ArrayList<Todo> toDoItems) {
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(toDoItems, this);
        activityTodoListBinding.rvTodo.setAdapter(toDoListAdapter);
    }
}