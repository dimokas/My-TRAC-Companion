package com.example.mytrac.DialogFragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import com.example.mytrac.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;

import static android.content.ContentValues.TAG;

public class LoginDialogFragment extends DialogFragment {

    private Button twitterLoginBtn, emailLogin, loginBtn;
    //twitter auth client required for custom login
    private TwitterAuthClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_dialog, container, false);

        // Do all the stuff to initialize your custom view

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().setCanceledOnTouchOutside(false);
        }

        //initialize twitter auth client
        client = new TwitterAuthClient();

        twitterLoginBtn = (Button) view.findViewById(R.id.twitterLoginBtn);
        twitterLoginBtn.setOnClickListener(new View.OnClickListener() { // login via twitter (atm simply starts a twitter session and gets profile data)
            @Override
            public void onClick(View v) {
                customLoginTwitter(view);
            }
        });

        emailLogin = (Button) view.findViewById(R.id.emailLoginBtn);
        emailLogin.setOnClickListener(new View.OnClickListener() { // login via email, to be implemented later
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        loginBtn = (Button) view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() { // go to next dialog to login by entering credentials
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("login_credentials_dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                DialogFragment dialogFragment = new LoginCredentialsDialogFragment();
                dialogFragment.show(ft, "login_credentials_dialog");

                dismiss();
            }
        });

        return view;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the client.
        if (client != null)
            client.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                getActivity().finish();
            }
        };
    }

    public void customLoginTwitter(View view) {
        //check if user is already authenticated or not
        if (getTwitterSession() == null) {

            //if user is not authenticated start authenticating
            client.authorize(getActivity(), new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // Do something with result, which provides a TwitterSession for making API calls
                    TwitterSession twitterSession = result.data;
                    fetchTwitterAccount();
                    dismiss();
                }

                @Override
                public void failure(TwitterException e) {
                    // Do something on failure
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //if user is already authenticated direct call fetch twitter email api
            Toast.makeText(getActivity().getApplicationContext(), "User already authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * call Verify Credentials API when Twitter Auth is successful else it will go in exception block
     * this method will provide you User model which contain all user information
     */
    public void fetchTwitterAccount() {
        //check if user is already authenticated or not
        if (getTwitterSession() != null) {

            Log.d(TAG, "Testing5");

            //fetch twitter image with other information if user is already authenticated

            //initialize twitter api client
            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();

            //Link for Help : https://developer.twitter.com/en/docs/accounts-and-users/manage-account-settings/api-reference/get-account-verify_credentials

            //pass includeEmail : true if you want to fetch Email as well
            Call<User> call = twitterApiClient.getAccountService().verifyCredentials(true, false, true);
            call.enqueue(new Callback<User>() {
                @Override
                public void success(Result<User> result) {
                    User user = result.data;

                    Log.d(TAG, "Testing6 " + user.toString());

                    String imageProfileUrl = user.profileImageUrl;

                    //NOTE : User profile provided by twitter is very small in size i.e 48*48
                    //Link : https://developer.twitter.com/en/docs/accounts-and-users/user-profile-images-and-banners
                    //so if you want to get bigger size image then do the following:
                    imageProfileUrl = imageProfileUrl.replace("_normal", "");

                    String twitter_username, twitter_email, twitter_image;

                    twitter_username = user.name;
                    twitter_email = user.email;
                    twitter_image = imageProfileUrl;

                    Log.d("Twitter user data", "Data : " + twitter_image + ", " + twitter_username + ", " + twitter_email);
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d(TAG, "Testing7");

                    Toast.makeText(getContext(), "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d(TAG, "Testing8");

            //if user is not authenticated first ask user to do authentication
            Toast.makeText(getContext(), "First to Twitter auth to Verify Credentials.", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * get authenticates user session
     *
     * @return twitter session
     */
    private TwitterSession getTwitterSession() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        //NOTE : if you want to get token and secret too use uncomment the below code
        /*TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;*/

        return session;
    }

}




