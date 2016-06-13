package todolist.example.com.todolist.data.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoItemBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME= "todoItems.db";

    public TodoItemBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TodoItemDBSchema.TodoItemTable.Name + "("
                + " _id integer primary key autoincrement, "
                + TodoItemDBSchema.Cols.UUID + ", "
                + TodoItemDBSchema.Cols.NAME + ", "
                + TodoItemDBSchema.Cols.DATE + ", "
                + TodoItemDBSchema.Cols.PRIORITY + ", "
                + TodoItemDBSchema.Cols.COMPLETED + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
