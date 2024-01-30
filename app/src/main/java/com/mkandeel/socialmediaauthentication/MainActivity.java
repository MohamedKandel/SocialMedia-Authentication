package com.mkandeel.socialmediaauthentication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mkandeel.socialmediaauthentication.databinding.ActivityMainBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.ParseTwitterUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CallbackManager callbackManager;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginButton = findViewById(R.id.btn_twitter);

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                //TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken token = session.getAuthToken();
                String mtoken = token.token;
                String secret = token.secret;

                login(session);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnTwitter.setOnClickListener(view -> {
            twitterLogin();
        });

        binding.btnLogout.setOnClickListener(view -> {
            ParseUser.logOut();
        });


        callbackManager = CallbackManager.Factory.create();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                        intent.putExtra("google", false);

                        AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        try {
                                            // get response model from the below link
                                            //https://developers.facebook.com/docs/android/graph/
                                            String name = object.getString("name");
                                            String id = object.getString("id");
                                            intent.putExtra("USER_NAME",name);
                                            intent.putExtra("USER_ID",id);
                                            startActivity(intent);
                                            finish();
                                            Log.i("Login With Facebook ", "onCompleted: " + name + "\n" + id);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        // Application code
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        binding.btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
            }
        });

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singInWithGoogle();
            }
        });
    }

    private void login(TwitterSession session) {
        String name = session.getUserName();
        String id = String.valueOf(session.getUserId());
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("USER_NAME",name);
        intent.putExtra("USER_ID",id);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode,resultCode,data);
    }

    private void twitterLogin() {
        ParseTwitterUtils.logIn(MainActivity.this, new LogInCallback() {

            @Override
            public void done(final ParseUser user, ParseException err) {
                if (err != null) {
                    //dlg.dismiss();
                    ParseUser.logOut();
                    Log.e("err", "err", err);
                }
                if (user == null) {
                    //dlg.dismiss();
                    ParseUser.logOut();
                    Toast.makeText(MainActivity.this, "The user cancelled the Twitter login.", Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                } else if (user.isNew()) {
                    //dlg.dismiss();
                    Log.i("Twitter AUTH", "done: "+ParseTwitterUtils.getTwitter().getUserId()+"\n"+
                            ParseTwitterUtils.getTwitter().getScreenName());

                    Toast.makeText(MainActivity.this, "User signed up and logged in through Twitter.", Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "User signed up and logged in through Twitter!");
                    user.setUsername(ParseTwitterUtils.getTwitter().getScreenName());
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (null == e) {
                                //alertDisplayer("First tome login!", "Welcome!");
                            } else {
                                ParseUser.logOut();
                                Toast.makeText(MainActivity.this, "It was not possible to save your username.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Log.i("Twitter AUTH", "done: "+ParseTwitterUtils.getTwitter().getUserId()+"\n"+
                            ParseTwitterUtils.getTwitter().getScreenName());
                    //dlg.dismiss();
                    Toast.makeText(MainActivity.this, "User logged in through Twitter.", Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "User logged in through Twitter!");
                    //alertDisplayer("Oh, you!","Welcome back!");
                }
            }
        });
    }

    private ActivityResultLauncher<Intent> arl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK && o.getData() != null) {
                        Task<GoogleSignInAccount> task =
                                GoogleSignIn.getSignedInAccountFromIntent(o.getData());
                        try {
                            task.getResult(ApiException.class);
                            task.addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                                @Override
                                public void onSuccess(GoogleSignInAccount account) {
                                    System.out.println(account.getId());
                                    System.out.println(account.getDisplayName());

                                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                                    intent.putExtra("google", true);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } catch (Exception e) {
                            //System.out.println(e.getMessage());
                            Log.e("MainActivity error", "onActivityResult: ", e);
                        }
                    }
                }
            }
    );

    private void singInWithGoogle() {
        Intent intent = gsc.getSignInIntent();
        arl.launch(intent);
    }


}