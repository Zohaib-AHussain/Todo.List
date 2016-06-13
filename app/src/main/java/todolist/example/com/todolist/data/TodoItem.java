package todolist.example.com.todolist.data;

import java.io.Serializable;
import java.util.UUID;

public class TodoItem implements Serializable {
    private UUID ID;
    private String name;
    private long dueDate;
    private int priority;
    private boolean completed;

    public TodoItem() {
        this.ID = UUID.randomUUID();
    }

    public TodoItem(String name) {
        this.name = name;
        this.ID = UUID.randomUUID();
    }

    public TodoItem(String name, long dueDate, int priority, boolean completed) {
        this.ID = UUID.randomUUID();
        this.name = name;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
    }

    public TodoItem(UUID ID, String name, long dueDate, int priority, boolean completed) {
        this.ID = ID;
        this.name = name;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
    }

    public UUID getID() {
        return ID;
    }

    public void setID(UUID ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
