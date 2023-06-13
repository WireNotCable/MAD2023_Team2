package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class EditSetLimit extends AppCompatActivity {

    private TextView selectedStartDate;
    private TextView selectedEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_set_limit);

        selectedStartDate = findViewById(R.id.editsetlimit_startdate);
        selectedEndDate = findViewById(R.id.editsetlimit_enddate);
    }

    public void showStartDatePicker(View view) {
        Calendar calendar = Calendar.getInstance(); // Get current date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                selectedStartDate.setText(selectedDate);
            }

        }, year,month, dayOfMonth); // Set initial date (optional)
        datePickerDialog.show();
    }

    public void showEndDatePicker(View view) {
        Calendar calendar = Calendar.getInstance(); // Get current date
        int year = calendar.get(Calendar.YEAR) ;
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                selectedEndDate.setText(selectedDate);
            }
        }, year, month, dayOfMonth); // Set initial date (optional)
        datePickerDialog.show();
    }
}
