package com.codepath.gridimagesearch.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.models.SettingsResult;


public class SettingsFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private Spinner sSize;
    private Spinner sColor;
    private Spinner sType;
    private EditText etSite;
    private SettingsResult settingsResult;

    public interface SettingsFragmentListener {
        void onFinishEditDialog(SettingsResult settingsResult);
    }

    public SettingsFragment() {
        // Empty constructor required for DialogFragment
        settingsResult = new SettingsResult();
    }

    public static SettingsFragment newInstance(String title) {
        SettingsFragment frag = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container);
        sSize = (Spinner) view.findViewById(R.id.sSize);
        sColor = (Spinner) view.findViewById(R.id.sColor);
        sType = (Spinner) view.findViewById(R.id.sType);
        etSite = (EditText) view.findViewById(R.id.etSite);

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Submit has been pressed - week2 cliff notes -> Basic Event listeners
                // Send back the age data
                // Set the result


                Intent res = new Intent();

                settingsResult.size = sSize.getSelectedItem().toString();
                settingsResult.color = sColor.getSelectedItem().toString();
                settingsResult.type = sType.getSelectedItem().toString();
                settingsResult.site = etSite.getText().toString();

                // Return input text to activity
                SettingsFragmentListener listener = (SettingsFragmentListener) getActivity();
                listener.onFinishEditDialog(settingsResult);
                dismiss();
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    // Fires whenever the textfield has an action performed
    // In this case, when the "Done" button is pressed
    // REQUIRES a 'soft keyboard' (virtual keyboard)
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            SettingsFragmentListener listener = (SettingsFragmentListener) getActivity();
            listener.onFinishEditDialog(settingsResult);
            dismiss();
            return true;
        }
        return false;
    }


}