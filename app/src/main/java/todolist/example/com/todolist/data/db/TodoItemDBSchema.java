package todolist.example.com.todolist.data.db;


public class TodoItemDBSchema {
    public static final class TodoItemTable{
        public static final String Name = "todoItems";
    }

    public static final class Cols{
        public static final String UUID = "uuid";
        public static final String NAME = "name";
        public static final String DATE = "date";
        public static final String PRIORITY = "priority";
        public static final String COMPLETED = "completed";
    }

}
