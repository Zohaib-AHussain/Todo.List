package todolist.example.com.todolist.data.db;


import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import todolist.example.com.todolist.data.TodoItem;

public class TodoItemCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public TodoItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TodoItem getTodoItem(){
        String uuidString = getString(getColumnIndex(TodoItemDBSchema.Cols.UUID));
        String name = getString(getColumnIndex(TodoItemDBSchema.Cols.NAME));
        long date = getLong(getColumnIndex(TodoItemDBSchema.Cols.DATE));
        int priority = Integer.valueOf(getString(getColumnIndex(TodoItemDBSchema.Cols.PRIORITY))).intValue();
        boolean completed = Integer.valueOf(getString(getColumnIndex(TodoItemDBSchema.Cols.COMPLETED))).intValue() == 1 ? true:false;

        return new TodoItem(UUID.fromString(uuidString),name,date,priority,completed);
    }

    public String getItemPrimaryKey(){
        this.moveToFirst();
        return getString(0);
    }
}
