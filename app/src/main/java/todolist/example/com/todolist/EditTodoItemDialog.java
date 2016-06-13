package todolist.example.com.todolist;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import todolist.example.com.todolist.data.TodoItem;

public class EditTodoItemDialog extends DialogFragment {

    public static final String EDIT_TODO_ITEM = "edit_item";

    @BindView(R.id.todo_item_name)
    TextInputLayout mTodoName;

    @BindView(R.id.todo_item_date)
    DatePicker mToDoDatePicker;

    @BindView(R.id.priority_container)
    CardView mPriorityContainer;

    @BindView(R.id.cancel)
    Button mCancelButton;

    @BindView(R.id.delete)
    Button mDeleteButton;

    @BindView(R.id.save)
    Button mSaveButton;

    private TodoItem mItem;
    private int mPriority;

    private EditDialogListener mListener;

    public static EditTodoItemDialog newInstance(TodoItem item){
        EditTodoItemDialog dialog = new EditTodoItemDialog();
        Bundle args = new Bundle();
        args.putSerializable(EDIT_TODO_ITEM,item);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof EditDialogListener)
            mListener = (EditDialogListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_todo_item, container, false);
        ButterKnife.bind(this, view);
        view.findViewById(R.id.add_fab).setVisibility(View.GONE);
        view.findViewById(R.id.dialog_buttons).setVisibility(View.VISIBLE);
        mItem = (TodoItem)getArguments().getSerializable(EDIT_TODO_ITEM);
        populateFields(mItem);
        return view;
    }

    private void populateFields(TodoItem todoItem) {
        mTodoName.getEditText().setText(todoItem.getName());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(todoItem.getDueDate());
        mToDoDatePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        mPriorityContainer.setBackgroundColor(getResources().getColor(Utils.getPriorityColor(todoItem.getPriority(), true)));
        mPriority = todoItem.getPriority();
    }

    @OnClick({R.id.cancel, R.id.delete, R.id.save})
    protected void onDialogButtonClicked(View v){
        switch (v.getId()){
            case R.id.cancel:
                break;
            case R.id.delete:
                mListener.deleteTodoItem(mItem);
                break;
            case R.id.save:
                if (!mTodoName.getEditText().getText().toString().equals(""))
                    mListener.updateTodoItem(update(mItem));
                break;
        }
        dismiss();
    }

    @OnClick({R.id.high_priority, R.id.med_priority, R.id.low_priority})
    protected void onPriorityClicked(View v){
        if (v.getId() == mPriority)
            return;
        switch (v.getId()){
            case R.id.high_priority:
                animatePriorityContainerBackground(1);
                break;
            case R.id.med_priority:
                animatePriorityContainerBackground(2);
                break;
            case R.id.low_priority:
                animatePriorityContainerBackground(3);
                break;
        }
        animatePriorityContainerBackground(mPriority);

    }

    private void animatePriorityContainerBackground(final int priority) {
        int colorFrom = getResources().getColor(Utils.getPriorityColor(mPriority, true));
        int colorTo = getResources().getColor(Utils.getPriorityColor(priority, true));
        int duration = 1000;
        ObjectAnimator animation = ObjectAnimator.ofInt(mPriorityContainer, "backgroundColor", colorFrom, colorTo)
                .setDuration(duration);
        animation.setEvaluator(new ArgbEvaluator());
        animation.start();
        mPriority = priority;
    }

    private TodoItem update(TodoItem item) {
        mItem.setName(mTodoName.getEditText().getText().toString());
        Calendar cal = Calendar.getInstance();
        cal.set(mToDoDatePicker.getYear(), mToDoDatePicker.getMonth(), mToDoDatePicker.getDayOfMonth());
        long dateStamp = cal.getTimeInMillis();
        mItem.setDueDate(dateStamp);
        mItem.setPriority(mPriority);
        return mItem;
    }

    public interface EditDialogListener {
        void updateTodoItem(TodoItem todoItem);
        void deleteTodoItem(TodoItem todoItem);
    }
}
