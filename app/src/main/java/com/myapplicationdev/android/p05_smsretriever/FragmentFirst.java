package com.myapplicationdev.android.p05_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
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

public class FragmentFirst extends Fragment {

    Button btnAddTextFrag1, btnEmail;
    EditText etNum;
    TextView tvFrag1, tvFrag2;
    String smsBody;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        tvFrag1 = view.findViewById(R.id.tvFrag1);
        tvFrag2 = view.findViewById(R.id.tvFrag2);
        etNum = view.findViewById(R.id.etNum);
        btnAddTextFrag1 = view.findViewById(R.id.btnRetrieveTextFrag1);
        btnEmail = view.findViewById(R.id.btnEmail);

        btnAddTextFrag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }
                //Create all messages URI
                Uri uri = Uri.parse("content://sms");

                // The columns we want
                // date is when the message took place
                // address is the number of the other party
                // body is message content
                // type 1 is received, type 2 sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                //Get Content Resolver object from which to
                // query the content provider
                ContentResolver cr = getActivity().getContentResolver();
                // The filter String
                String filter="address LIKE ?";
                // The matches for the ?
                String[] filterArgs = {"%" + etNum.getText().toString() +"%"};
                // Fetch SMS Message from Built-in Content Provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                smsBody = "";
                if(cursor.moveToFirst()){
                    do{
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")){
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";

                    } while (cursor.moveToNext());
                }
                tvFrag2.setText(smsBody);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"alicialim0123@gmail.com"});
                email.putExtra(Intent.EXTRA_TEXT, smsBody);
                email.setType("message/rfc822");
                startActivity(email);
                startActivity(Intent.createChooser(email, "Send Email"));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}