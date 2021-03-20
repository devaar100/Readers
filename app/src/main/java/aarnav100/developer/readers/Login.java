package aarnav100.developer.readers;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {
    private FirebaseAuth mAuth;
    private Button resend,login,register;
    private EditText email,password;
    private View.OnClickListener ocl;
    public static final String TAG="tag";
    private Context context;
    private android.support.v7.app.AlertDialog dialog;
    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v=inflater.inflate(R.layout.fragment_login, container, false);
        context=getActivity();
        DrawerLayout drawerLayout=(DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mAuth=FirebaseAuth.getInstance();
        email=(EditText)v.findViewById(R.id.email);
        password=(EditText)v.findViewById(R.id.password);
        resend=(Button)v.findViewById(R.id.resend);
        login=(Button)v.findViewById(R.id.login);
        register=(Button)v.findViewById(R.id.register);
        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        View dialogView=((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loader_dialog,null);
        dialogBuilder.setView(dialogView);
        dialog=dialogBuilder.create();
        dialog.getWindow().getDecorView().getBackground().setAlpha(0);
        dialog.setCancelable(false);
        ocl=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.resend:
                        FirebaseUser user=mAuth.getCurrentUser();
                        if(user!=null) {
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Toast.makeText(context, "Verification email sent to your account", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(context, "There was some error Please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case R.id.login:
                        if(!email.getText().toString().equals("")&&!password.getText().toString().equals("")) {
                            dialog.show();
                            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                    .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                            // If sign in fails, display a message to the user. If sign in succeeds
                                            // the auth state listener will be notified and logic to handle the
                                            // signed in user can be handled in the listener.
                                            dialog.dismiss();
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(context, task.getException().toString().split(":")[1],
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (!user.isEmailVerified())
                                                    Toast.makeText(context, "Please verify your account first", Toast.LENGTH_SHORT).show();
                                                else {
                                                    FragmentManager man = Login.this.getFragmentManager();
                                                    man.beginTransaction().replace(R.id.fragment, new Profile()).commit();
                                                }
                                            }
                                            // ...
                                        }
                                    });
                        }
                        else
                            Toast.makeText(context,"Either email or password field is empty", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.register:
                        final View v2=inflater.inflate(R.layout.dialog_new_user,null);
                        final android.support.v7.app.AlertDialog dialog2=dialogBuilder.setView(v2).create();
                        dialog2.show();
                        ((Button)v2.findViewById(R.id.new_create)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String temp_email,temp_pass,temp_pass2;
                                temp_email=((EditText)v2.findViewById(R.id.new_un)).getText().toString();
                                temp_pass=((EditText)v2.findViewById(R.id.new_up)).getText().toString();
                                temp_pass2=((EditText)v2.findViewById(R.id.re_new_up)).getText().toString();
                                if(temp_pass.equals(temp_pass2)){
                                    dialog2.dismiss();
                                    if (!temp_email.equals("") && !temp_pass.equals("")) {
                                        dialog.show();
                                        mAuth.createUserWithEmailAndPassword(temp_email, temp_pass)
                                                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        dialog.dismiss();
                                                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                                        if (!task.isSuccessful()) {
                                                            {
                                                                Toast.makeText(context, task.getException().toString().split(":")[1],
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                        Toast.makeText(context, "Confirmation email has been sent to your account", Toast.LENGTH_SHORT).show();
                                                                    else
                                                                        Toast.makeText(context, "There was some error sending confirmation Please try Resend confimartion mail", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                    } else
                                        Toast.makeText(context, "Either email or password field is empty", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(context,"Passwords don't match", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        };
        resend.setOnClickListener(ocl);
        login.setOnClickListener(ocl);
        register.setOnClickListener(ocl);
        return v;
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.setGroupVisible(R.id.mygroup,false);
    }
}
