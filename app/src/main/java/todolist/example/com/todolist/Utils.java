package todolist.example.com.todolist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static String getFormattedDate(long milliseconds){
        return new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(milliseconds));
    }

    public static int getPriorityColor(int priority) {
        return getPriorityColor(priority, false);
    }


        public static int getPriorityColor(int priority, boolean forBackground) {
        if (priority==1) {
            if (forBackground)
                return R.color.faded_red;
            return R.color.red;
        }

        else if (priority==2) {
            if (forBackground)
                return R.color.faded_yellow;
            return R.color.yellow;
        }


        else if (priority==3) {
            if (forBackground)
                return R.color.faded_green;
            return R.color.green;
        }

        else
            return R.color.white;
    }
}
