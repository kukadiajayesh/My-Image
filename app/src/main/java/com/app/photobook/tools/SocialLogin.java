/*
package com.app.photobook.tools;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.app.photobook.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

*/
/**
 * Created by MNS on 15-10-2016.
 *//*



public class SocialLogin {

    public static String TAG = SocialLogin.class.getSimpleName();

    Context context;
    Activity activity;
    SocialResponse socialResponse;

*/
/*
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     *//*



    GoogleApiClient mGoogleApiClient;
    CallbackManager fbCallbackManager;

    private static final int RC_SIGN_IN_GOOGLE_PLUS = 9001;
    private static final int RC_SIGN_IN_FACEBOOK = 64206;

    public SocialLogin(Context context, SocialResponse socialResponse) {

        this.context = context;
        this.socialResponse = socialResponse;
        activity = (Activity) context;

        //GetKey();

        FacebookSdk.sdkInitialize(context);
        AppEventsLogger.activateApp(context);
    }

    public void ConnectFb() {

        if (!Utils.isOnline(getApplicationContext())) {
            Toast.makeText(activity, context.getString(R.string.message_no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        fbCallbackManager = CallbackManager.Factory.create();

        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(fbCallbackManager, facebookCallback);
        loginManager.logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
    }

    public void ConnectGooglePlus() {

        if (!Utils.isOnline(getApplicationContext())) {
            Toast.makeText(activity, context.getString(R.string.message_no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) activity, onConnectionFailedListener
*/
/* OnConnectionFailedListener *//*

)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE_PLUS);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN_GOOGLE_PLUS) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        } else if (requestCode == RC_SIGN_IN_FACEBOOK) {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            socialResponse.onSocialSuccess(personName, personEmail, "", "", personId);

            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);

        } else {
            // Signed out, show unauthenticated UI.
        }
    }


    FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("onSuccess", loginResult.toString());
            getUserInfo(loginResult);
        }

        @Override
        public void onCancel() {
            Log.e("fb connect", "canceled");
        }

        @Override
        public void onError(FacebookException error) {
            error.printStackTrace();
            Log.e("fb error", error.getMessage());
        }
    };

    protected void getUserInfo(LoginResult login_result) {

        GraphRequest data_request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),

                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        try {

                            String email = json_object.getString("email");
                            if (email.length() == 0) {
                                Toast.makeText(activity,
                                        "Your account did not share email id", Toast.LENGTH_LONG)
                                        .show();

                                return;
                            }

                            String first_name = json_object
                                    .getString("first_name");
                            String last_name = json_object
                                    .getString("last_name");

                            JSONObject jsonPic = json_object.getJSONObject("picture");
                            JSONObject jsondata = jsonPic.getJSONObject("data");
                            String profile = jsondata.getString("url");
                            String social_id = json_object.getString("id");

                            socialResponse.onSocialSuccess(first_name + " " + last_name,
                                    email, profile,
                                    social_id, "");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,first_name,last_name,email,picture.width(256).height(256)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(activity, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
            Log.e("Login Error", connectionResult.getErrorMessage());
        }

    };


    // Retrieve some profile information to personalize our app for the
    // user.


*/
/*

GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

    if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

        Person currentPerson = Plus.PeopleApi
                .getCurrentPerson(mGoogleApiClient);

        String name = currentPerson.getDisplayName();
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        String profile = currentPerson.getImage().getUrl();
        String g_id = currentPerson.getId();

        if (email != null || email.length() == 0) {
            Toast.makeText(activity, "We need your Email id.", Toast.LENGTH_LONG).show();
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        profile = profile.substring(0, profile.length() - 2) + 200;
        socialResponse.onSocialSuccess(name, email, profile, "", g_id);
    }
*//*




    public interface SocialResponse {
        void onSocialSuccess(String name, String email, String profile, String fb_id, String g_id);
    }

    public static void GetKey(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}

*/
