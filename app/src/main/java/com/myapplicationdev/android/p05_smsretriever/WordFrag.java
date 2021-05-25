package com.myapplicationdev.android.p05_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WordFrag extends Fragment {

    Button btnRetrieveSec;
    TextView tvShowWordFragSec;
    EditText etWordFragSec;
    Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);
        btnRetrieveSec = view.findViewById(R.id.btnRetrieveSecond);
        tvShowWordFragSec = view.findViewById(R.id.tvShowWordSms);
        etWordFragSec = view.findViewById(R.id.etWord);

        btnRetrieveSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }
                String getInput = etWordFragSec.getText().toString();
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[] {"date", "address", "body", "type"};
                ContentResolver cr = getActivity().getContentResolver();
                if(getInput.matches("")) {
                    cursor = cr.query(uri, reqCols, null,null, null);
                }
                else {
                    String[] separated = getInput.split(" ");
                    String filtering = "";
                    for(int i=0; i<separated.length; i++) {
                        filtering += "%" + separated[i] + "%";
                    }
                    String filter = "body LIKE ?";
                    String[] filterArgs = {filtering};
                    cursor = cr.query(uri, reqCols, filter,filterArgs, null);
                }
                String smsBody = "";
                if(cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);

                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if(type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        }
                        else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date + "\n" + body + "\n\n";
                    } while (cursor.moveToNext());
                }
                tvShowWordFragSec.setText(smsBody);
            }
        });
        return view;
    }
}