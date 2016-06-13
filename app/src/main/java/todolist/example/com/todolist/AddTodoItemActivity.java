package todolist.example.com.todolist;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import todolist.example.com.todolist.data.TodoItem;

public class AddTodoItemActivity extends AppCompatActivity {

    public static final String NEW_TODO_ITEM = "new_todo_item";

    @BindView(R.id.todo_item_name)
    TextInputLayout mTodoName;

    @BindView(R.id.todo_item_date)
    DatePicker mToDoDatePicker;

    @BindView(R.id.priority_container)
    CardView mPriorityContainer;

    private int mPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_item);
        setTitle(getString(R.string.add_item));
        ButterKnife.bind(this);
    }

    @OnClick(R.id.add_fab)
    protected void onAddFabClick(){
        if (validate())
        {
            String name = mTodoName.getEditText().getText().toString();
            Calendar cal = Calendar.getInstance();
            cal.set(mToDoDatePicker.getYear(), mToDoDatePicker.getMonth(), mToDoDatePicker.getDayOfMonth());
            long dateStamp = cal.getTimeInMillis();
            Intent resultData = new Intent();
            resultData.putExtra(NEW_TODO_ITEM, new TodoItem(name, dateStamp, mPriority, false));
            setResult(RESULT_OK, resultData);
            finish();
        }
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
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mPriority = priority;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();
    }

    private boolean validate() {
        if (mTodoName.getEditText().getText().toString().equals("") || mPriority == 0){
            Toast.makeText(this, getString(R.string.enter_all_info), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
