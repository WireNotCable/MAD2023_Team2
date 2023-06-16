package sg.edu.np.mad.pennywise.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import sg.edu.np.mad.pennywise.R;


public class Mobile extends Fragment {
    EditText MobileNumber;
    EditText Amount;
    EditText Comment;
    Button Transfer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobile, container, false);
    }

}