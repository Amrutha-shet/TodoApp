package com.example.todolistapp.viewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todolistapp.Repository.TodoTransactionRepository;
import com.example.todolistapp.database.Todo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amrutha on 26/06/20.
 */
public class ToDoViewModel extends AndroidViewModel {

    private TodoTransactionRepository todoTransactionRepository = new TodoTransactionRepository(getApplication());
    private ArrayList<Todo> toDoItemList = new ArrayList<>();

    private MutableLiveData<ArrayList<Todo>> todoLivedata = new MutableLiveData<>();

    public LiveData<ArrayList<Todo>> getTodoList() {
        return todoLivedata;
    }

    private MutableLiveData<Boolean> progress = new MutableLiveData<>();

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    private MutableLiveData<Boolean> nodata = new MutableLiveData<>();

    public LiveData<Boolean> getNoData() {
        return nodata;
    }

    public ToDoViewModel(@NonNull Application application) {
        super(application);
    }

    public void insertOrUpdate(Todo todo) {
        new InsertOrUpdateTodoTask().execute(todo);
    }

    public void fetchTodoList(String searchKey) {
        new FetchTodoItemsTask().execute(searchKey);
    }

    private class InsertOrUpdateTodoTask extends AsyncTask<Todo, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgress();
        }

        @Override
        protected Void doInBackground(Todo... todos) {
            todoTransactionRepository.insertOrUpdate(todos[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void todo) {
            super.onPostExecute(todo);
            new FetchTodoItemsTask().execute("");
        }
    }

    private class FetchTodoItemsTask extends AsyncTask<String, Void, List<Todo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgress();
        }

        @Override
        protected List<Todo> doInBackground(String... searchParam) {
            return todoTransactionRepository.getTodoList(searchParam[0]);
        }

        @Override
        protected void onPostExecute(List<Todo> todos) {
            toDoItemList.clear();
            toDoItemList.addAll(todos);
            handleNoDataFound();
        }
    }

    private void setProgress() {
        if (progress.getValue() != null) {
            if (!progress.getValue())
                progress.postValue(true);
        } else  progress.postValue(true);
    }

    private void handleNoDataFound() {
        if (toDoItemList.isEmpty()) {
            nodata.postValue(true);
        } else {
            nodata.postValue(false);
        }
        todoLivedata.postValue(toDoItemList);
        progress.postValue(false);
    }
}
