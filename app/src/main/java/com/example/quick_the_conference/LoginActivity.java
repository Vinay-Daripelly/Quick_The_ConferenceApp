package com.example.quick_the_conference;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
public class LoginActivity extends AppCompatActivity {

    EditText emailBox, passwordBox;
    Button loginBtn, signupBtn;
    public static String PREFS_NAME="MyprefsFile";
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    TextView psReset;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth =FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Just a moment...");

        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);

        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.createBtn);
        psReset=findViewById(R.id.fgpassword);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();


                SharedPreferences sharedPreferences=getSharedPreferences(LoginActivity.PREFS_NAME,0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("hasLoggedIn",true);
                editor.commit();

                String email,password;
                email=emailBox.getText().toString().trim();
                password=passwordBox.getText().toString().trim();
                if(email.length()>0 && password.length()>0) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful() && auth.getCurrentUser().isEmailVerified()) {
                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));

                            } else if (task.isSuccessful() && !(auth.getCurrentUser().isEmailVerified())){
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, verification.class));}
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Above fields can't be empty!", Toast.LENGTH_LONG).show();
                }

            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
        psReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail=new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog =new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter your Email to recieve reset link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes",((dialog, which) -> {
                    String mail=resetMail.getText().toString();
                    auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(LoginActivity.this, "Reset link sent to your Email.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Error! reset link not sent!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }));
                passwordResetDialog.setNegativeButton("No",((dialog, which) -> {

                }));
                passwordResetDialog.create().show();
            }
        });
    }
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AppExit();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
    public void AppExit()
    {
        this.finish();
        Intent intent =new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}