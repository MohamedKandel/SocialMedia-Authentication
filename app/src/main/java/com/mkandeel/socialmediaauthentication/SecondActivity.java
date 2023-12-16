package com.mkandeel.socialmediaauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mkandeel.socialmediaauthentication.databinding.ActivitySecondBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity {

    private ActivitySecondBinding binding;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            boolean withGoogle = bundle.getBoolean("google",false);
            if (withGoogle) {
                Toast.makeText(SecondActivity.this, "with google", Toast.LENGTH_SHORT).show();
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail().build();
                gsc = GoogleSignIn.getClient(this,gso);
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if (account != null) {
                    binding.txtName.setText(account.getDisplayName()+"\n"+account.getId());
                    Log.d("Google success", "onCreate: "+account.getId());
                    Log.d("Google success", "onCreate: "+account.getDisplayName());
                    Log.d("Google success", "onCreate: "+account.getEmail());
                } else {
                    Log.d("Google success", "onCreate: null");
                }
                binding.btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signOut();
                    }
                });
            } else {
                Toast.makeText(SecondActivity.this, "with facebook", Toast.LENGTH_SHORT).show();
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
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
                                    binding.txtName.setText(object.getString("name"));
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

                binding.btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(SecondActivity.this, MainActivity.class));
                        finish();
                    }
                });
                //System.out.println("Error occurred");
            }
        }
    }

    private void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    revokeAccess();
                    startActivity(new Intent(SecondActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(SecondActivity.this, "task failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void revokeAccess() {
        gsc.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SecondActivity.this, "access revoked from app for this account", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}