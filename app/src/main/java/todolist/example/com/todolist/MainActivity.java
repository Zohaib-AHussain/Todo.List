package todolist.example.com.todolist;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import todolist.example.com.todolist.data.TodoItem;
import todolist.example.com.todolist.data.TodoItemsDbManager;
import todolist.example.com.todolist.recycler_helpers.ItemTouchHelperAdapter;
import todolist.example.com.todolist.recycler_helpers.SimpleItemTouchHelperCallback;

public class MainActivity extends AppCompatActivity implements EditTodoItemDialog.EditDialogListener{

    private static final int ADD_ITEM = 101;
    private static final String EDIT_TODO_ITEM_DIALOG = "edit_dialog";

    @BindView(R.id.todo_recyclerview)
    RecyclerView mTodoItemsRecycler;

    @BindView(R.id.root_container)
    RelativeLayout mRootContainer;

    List<TodoItem> mTodoItemsList;
    TodoItemsDbManager mTodoItemsDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTodoItemsDbManager = TodoItemsDbManager.get(this);
        mTodoItemsList = mTodoItemsDbManager.getTodoItemsList();
        mTodoItemsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mTodoItemsRecycler.setAdapter(new TodoListAdapter());
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(((ItemTouchHelperAdapter)mTodoItemsRecycler.getAdapter()));
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mTodoItemsRecycler);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add){
            startActivityForResult(new Intent(this, AddTodoItemActivity.class), ADD_ITEM);
            return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==ADD_ITEM)
            if (resultCode==RESULT_OK) {
                mTodoItemsDbManager.addTodoItem(((TodoItem) data.getSerializableExtra(AddTodoItemActivity.NEW_TODO_ITEM)));
                refreshList();

            }
    }

    private void refreshList() {
        mTodoItemsList = mTodoItemsDbManager.getTodoItemsList();
        if (!mTodoItemsRecycler.isComputingLayout())
            mTodoItemsRecycler.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateTodoItem(TodoItem todoItem) {
        mTodoItemsDbManager.updateTodoItem(todoItem);
        refreshList();
    }

    @Override
    public void deleteTodoItem(TodoItem todoItem) {
        mTodoItemsDbManager.deleteTodoItem(todoItem.getID());
        refreshList();
        showUndoSnack();
    }


    protected class TodoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.item_name)
        TextView name;
        @BindView(R.id.due_date)
        TextView date;
        @BindView(R.id.priority)
        View priority;
        @BindView(R.id.status)
        CheckBox status;

        TodoItem item;

        public TodoListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(TodoItem todoItem) {
            item = todoItem;
            name.setText(todoItem.getName());
            date.setText(Utils.getFormattedDate(todoItem.getDueDate()));
            status.setChecked(todoItem.isCompleted());
            priority.setBackgroundColor(ContextCompat.getColor(MainActivity.this, Utils.getPriorityColor(todoItem.getPriority())));
        }

        @Override
        public void onClick(View v) {
            DialogFragment dialog = EditTodoItemDialog.newInstance(item);
            dialog.show(getSupportFragmentManager(), EDIT_TODO_ITEM_DIALOG);
        }

        @OnCheckedChanged(R.id.status)
        protected void onStatusCheckChanged(){
            boolean completed = status.isChecked();
            item.setCompleted(completed);
            mTodoItemsDbManager.updateTodoItem(item);
            refreshList();
        }
    }

    private void showUndoSnack() {
        Snackbar.make(mRootContainer, "Item Deleted!", Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTodoItemsDbManager.undoLastDelete();
                        refreshList();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.yellow))
                .show();
    }

    protected class TodoListAdapter extends RecyclerView.Adapter<TodoListViewHolder> implements ItemTouchHelperAdapter{

        @Override
        public TodoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TodoListViewHolder(getLayoutInflater().
                    inflate(R.layout.todo_item_recycler_row, parent, false));
        }

        @Override
        public void onBindViewHolder(TodoListViewHolder holder, int position) {
            holder.bind(mTodoItemsList.get(position));
        }

        @Override
        public int getItemCount() {
            return mTodoItemsList.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition){
                for (int i=fromPosition; i<toPosition; i++){
                    Collections.swap(mTodoItemsList, i, i+1);
                }
            }
            else{
                for (int i = fromPosition; i>toPosition; i--){
                    Collections.swap(mTodoItemsList, i, i-1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            mTodoItemsDbManager.updateItemPositions(mTodoItemsList.get(fromPosition), mTodoItemsList.get(toPosition));
            return true;
        }


        @Override
        public void onItemDismiss(int position) {
            mTodoItemsDbManager.deleteTodoItem(mTodoItemsList.get(position).getID());
            refreshList();
            showUndoSnack();
        }}

    }

