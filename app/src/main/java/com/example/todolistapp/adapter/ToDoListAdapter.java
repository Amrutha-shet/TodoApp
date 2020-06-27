package com.example.todolistapp.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.database.Todo;
import com.example.todolistapp.databinding.ItemTodoListBinding;

import java.util.List;

/**
 * Created by Amrutha on 26/06/20.
 */
public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.TodoViewHolder> {

    private List<Todo> toDoItems;
    private TodoItemClickListener todoItemClickListener;

    public ToDoListAdapter(List<Todo> toDoItems, TodoItemClickListener todoItemClickListener) {
        this.toDoItems = toDoItems;
        this.todoItemClickListener = todoItemClickListener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TodoViewHolder(DataBindingUtil.inflate
                (LayoutInflater.from(parent.getContext()), R.layout.item_todo_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.bind(toDoItems.get(position));
    }

    @Override
    public int getItemCount() {
        return toDoItems.size();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private ItemTodoListBinding binding;

        public TodoViewHolder(@NonNull ItemTodoListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Todo toDoItem) {
            binding.title.setText(toDoItem.getTitle());
            binding.description.setText(toDoItem.getDescription());
            binding.checkbox.setChecked(toDoItem.getIsCompleted() == 1);
            binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    toDoItem.setIsCompleted(isChecked?1:0);
                    todoItemClickListener.onTodoItemClick(toDoItem);
                }
            });
        }
    }

    public interface TodoItemClickListener {
        void onTodoItemClick(Todo toDoItem);
    }

}
