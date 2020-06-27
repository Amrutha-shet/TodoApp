package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolistapp.database.Todo;
import com.example.todolistapp.databinding.ActivityAddTodoBinding;
import com.example.todolistapp.viewModel.ToDoViewModel;

import static com.example.todolistapp.ToDoListActivity.TODO_ITEM;

public class AddTodoActivity extends AppCompatActivity {

    private ActivityAddTodoBinding activityAddTodoBinding;
    private ToDoViewModel toDoViewModel;
    private Todo toDoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddTodoBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_todo);
        toDoViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(ToDoViewModel.class);
        initView();
    }

    private void initView() {
        toDoItem = new Todo();
        activityAddTodoBinding.btnDone.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(TODO_ITEM, toDoItem);
            setResult(RESULT_OK, intent);
            finish();
        });
        activityAddTodoBinding.btnCancel.setOnClickListener(v -> {
            finish();
            setResult(RESULT_CANCELED);
        });
        activityAddTodoBinding.todoTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (!TextUtils.isEmpty(input)) {
                    toDoItem.setTitle(input);
                    activityAddTodoBinding.btnDone.setEnabled(true);
                } else activityAddTodoBinding.btnDone.setEnabled(false);
            }
        });

        activityAddTodoBinding.todoCta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (!TextUtils.isEmpty(input)) {
                    toDoItem.setDescription(input);
                }
            }
        });
    }
}
