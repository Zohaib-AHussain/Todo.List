package todolist.example.com.todolist.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import todolist.example.com.todolist.data.db.TodoItemBaseHelper;
import todolist.example.com.todolist.data.db.TodoItemCursorWrapper;
import todolist.example.com.todolist.data.db.TodoItemDBSchema.*;

public class TodoItemsDbManager {
    private static TodoItemsDbManager sTodoItemsDbManager;
    private Context mContext;
    private SQLiteDatabase mDb;
    private TodoItem mLastDeletedItem;

    public static TodoItemsDbManager get(Context context){
        if (sTodoItemsDbManager != null)
            return sTodoItemsDbManager;
        sTodoItemsDbManager = new TodoItemsDbManager(context);
        return sTodoItemsDbManager;
    }

    private TodoItemsDbManager(Context context) {
        mContext = context.getApplicationContext();
        mDb = new TodoItemBaseHelper(mContext).getWritableDatabase();
    }

    public List<TodoItem> getTodoItemsList() {
        List<TodoItem> todoItems = new ArrayList<>();
        TodoItemCursorWrapper cursor = queryTodoItems(null, null);
        cursor.moveToFirst();
        try {
            while (!cursor.isAfterLast()) {
                todoItems.add(cursor.getTodoItem());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return todoItems;
    }

    public void addTodoItem(TodoItem item){
        mDb.insert(TodoItemTable.Name, null, getContentValues(item));
    }

    public TodoItem getTodoItem(UUID ID){
        TodoItemCursorWrapper cursor = queryTodoItems(Cols.UUID + " = ?", new String[]{ID.toString()});
        try{
            if (cursor.getCount() == 0) return null;
            cursor.moveToFirst();
            return cursor.getTodoItem();
        }finally {
            cursor.close();
        }
    }

    public void updateTodoItem(TodoItem todoItem){
        String uuidString = todoItem.getID().toString();
        ContentValues cv = getContentValues(todoItem);
        mDb.update(TodoItemTable.Name, cv, Cols.UUID+" = ?", new String[]{uuidString});
    }

    public void swapItem(String primaryKeyItem1, TodoItem item2){
        mDb.update(TodoItemTable.Name, getContentValues(item2), "_id = ?", new String[]{primaryKeyItem1});
    }

    public void deleteTodoItem(UUID ID){
        mLastDeletedItem = getTodoItem(ID);
        mDb.delete(TodoItemTable.Name, Cols.UUID+" = ?", new String[]{ID.toString()});
    }
    private TodoItemCursorWrapper queryTodoItems(String where, String[] whereArgs) {
        Cursor cursor = mDb.query(
                TodoItemTable.Name,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null
        );
        return new TodoItemCursorWrapper(cursor);
    }

    private ContentValues getContentValues(TodoItem todoItem) {
        ContentValues cv = new ContentValues();
        cv.put(Cols.UUID, todoItem.getID().toString());
        cv.put(Cols.NAME, todoItem.getName());
        cv.put(Cols.DATE, String.valueOf(todoItem.getDueDate()));
        cv.put(Cols.PRIORITY, String.valueOf(todoItem.getPriority()));
        cv.put(Cols.COMPLETED, todoItem.isCompleted()? 1 : 0);
        return cv;
    }


    public void updateItemPositions(TodoItem todoItem, TodoItem todoItem2) {
        TodoItemCursorWrapper cursor = queryTodoItems(Cols.UUID + " = ?", new String[]{todoItem.getID().toString()});
        TodoItemCursorWrapper cursorItem2 = queryTodoItems(Cols.UUID + " = ?", new String[]{todoItem2.getID().toString()});
        String primaryKeyItem1 = cursor.getItemPrimaryKey();
        String primaryKeyItem2 = cursorItem2.getItemPrimaryKey();

        try{
            swapItem(primaryKeyItem1, todoItem2);
            swapItem(primaryKeyItem2, todoItem);
        }finally {
            cursor.close();
        }
    }

    public void undoLastDelete() {
        addTodoItem(mLastDeletedItem);
    }

}
