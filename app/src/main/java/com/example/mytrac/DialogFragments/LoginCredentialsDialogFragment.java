package com.example.mytrac.DialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.mytrac.Constants;
import com.example.mytrac.MainActivity;
import com.example.mytrac.R;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;

import static android.content.Context.MODE_PRIVATE;

public class LoginCredentialsDialogFragment extends DialogFragment {

    private Button loginCredentialsBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_credentials_dialog, container, false);

        // Do all the stuff to initialize your custom view

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().setCanceledOnTouchOutside(false);
        }

        loginCredentialsBtn = (Button) v.findViewById(R.id.loginCredentialsBtn);
        loginCredentialsBtn.setOnClickListener(new View.OnClickListener() { // login by entering credentials, to be implemented later
            @Override
            public void onClick(View v) {
                RestTask restTask = new RestTask(MainActivity.mainActivity, Constants.LOGIN_URL);
                restTask.execute();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {

            Window window = getDialog().getWindow();
            Point size = new Point();

            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);

            int width = size.x;
            int height = size.y;

            // set percentage of width and height screen the dialog is supposed to take
            window.setLayout((int) (width * 0.95), (int) (height * 0.75));
            window.setGravity(Gravity.CENTER);

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() { // on back pressed go to previous (initial) login dialog
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("login_dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                DialogFragment dialogFragment = new LoginDialogFragment();
                dialogFragment.show(ft, "login_dialog");
            }
        };
    }

    private class RestTask extends AsyncTask<Void, Void, Hashtable<String, String>> {
        private Context mContext;
        private String mUrl;
        private static final String TAG = "RestTask";
        private static final String PROFILETAG = "profileId";

        public RestTask(Context context, String url) {
            mContext = context;
            mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Hashtable doInBackground(Void... params) {
            Hashtable ht = null;
            String data = null;
            try {
                data = getJSON(mUrl);
                if (data != null) {
                    JSONObject json = (JSONObject) new JSONTokener(data).nextValue();
                    MainActivity.mainActivity.userCategory = Integer.parseInt((String)json.get(PROFILETAG));
                    SharedPreferences.Editor editor = MainActivity.mainActivity.getSharedPreferences("mytrac.user.settings", MODE_PRIVATE).edit();
                    editor.putInt("userCategory", MainActivity.mainActivity.userCategory);
                    editor.putString("uID", MainActivity.mainActivity.uID);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.i(TAG, "Exception due to unreachable Server");
                //e.printStackTrace();
            }
            return ht;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Hashtable result) {
            if (MainActivity.mainActivity.uID!=null && (MainActivity.mainActivity.userCategory == Constants.DEFAULT_USER || MainActivity.mainActivity.userCategory == Constants.LOW_VISION_USER)) {
                dismiss();
                Constants.theme_selected = MainActivity.mainActivity.userCategory;
                MainActivity.mainActivity.recreate();

            }
        }

        // HTTP Get based on HttpURLConnection class to retrieve the JSON text from the server
        public String getJSON(String url) {
            HttpURLConnection conn = null;
            try {
                String username = "test";
                String password = "test";
                HashMap<String, String> mapData = new HashMap<String, String>();
                mapData.put("username",username);
                mapData.put("password",password);
                JSONObject postData = null;
                if (mapData != null) {
                    postData = new JSONObject(mapData);
                }

                URL u = new URL(url);
                conn = (HttpURLConnection) u.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                if (postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }
                int status = conn.getResponseCode();
                MainActivity.mainActivity.uID = conn.getHeaderField("Authorization");
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb2 = new StringBuilder();
                        String line2;
                        while ((line2 = br2.readLine()) != null) {
                            sb2.append(line2+"\n");
                        }
                        br2.close();
                        return sb2.toString();
                }

            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            } finally {
                if (conn != null) {
                    try {
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

    }

}
