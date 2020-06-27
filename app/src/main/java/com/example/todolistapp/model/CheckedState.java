package com.example.todolistapp.model;

/**
 * Created by Amrutha on 27/06/20.
 */
public enum CheckedState {
    CHECKED(1), UN_CHECKED(0);

    private int checked;

    CheckedState(int checked) {
        this.checked = checked;
    }
    public int getChecked(CheckedState checkedState){
        return CheckedState.CHECKED.checked;
    }
}
