package sg.edu.np.mad.pennywise;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.ProxyFileDescriptorCallback;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditSetLimit extends AppCompatActivity {

    private TextView selectedStartDate;
    private TextView selectedEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_set_limit);

        Button setButton= findViewById(R.id.editsetlimit_setbutton);
        ImageView closeButton = findViewById(R.id.editsetlimit_close);
        selectedStartDate = findViewById(R.id.editsetlimit_startdate);
        selectedEndDate = findViewById(R.id.editsetlimit_enddate);

        selectedStartDate.setText(getTodaysDate("Start"));
        selectedEndDate.setText(getTodaysDate("End"));

        selectedStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("dd-MMM-yyy", Locale.getDefault()).format(new Date(selection));
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });
        selectedEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("dd-MMM-yyy", Locale.getDefault()).format(new Date(selection));
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSetLimit.this,SetLimit.class);
                startActivity(intent);
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSetLimit.this,SetLimit.class);
                startActivity(intent);
            }
        });
    }

    public String getTodaysDate(String dateType) {
        Calendar calendar = Calendar.getInstance(); // Get current date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth;

        if (dateType.equals("Start")) {
            dayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        } else if (dateType.equals("End")) {
            dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            // Handle invalid dateType here
            return "";
        }

        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        return selectedDate;
    }

//    public void showStartDatePicker(View view) {
//        Calendar calendar = Calendar.getInstance(); // Get current date
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//                selectedStartDate.setText(selectedDate);
//            }
//
//        }, year,month, dayOfMonth); // Set initial date (optional)
//        datePickerDialog.show();
//    }

//    public void showEndDatePicker(View view) {
//        Calendar calendar = Calendar.getInstance(); // Get current date
//        int year = calendar.get(Calendar.YEAR) ;
//        int month = calendar.get(Calendar.MONTH);
//        int dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//                selectedEndDate.setText(selectedDate);
//            }
//        }, year, month, dayOfMonth); // Set initial date (optional)
//        datePickerDialog.show();
//    }
}
