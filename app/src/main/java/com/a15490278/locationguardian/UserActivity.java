package com.a15490278.locationguardian;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("ApplySharedPref")
public class UserActivity extends AppCompatActivity {

    SharedPreferences sp;

    EditText editTextUser, editTextPassword, editTextEmail, editTextPhone;
    Button buttonDestroy;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences(SharedKeys.Preferences, MODE_PRIVATE);

        loadViews();

        setOperation();

    }

    private void loadViews(){
        editTextUser = findViewById(R.id.user);
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        editTextPhone = findViewById(R.id.phone);

        buttonDestroy = findViewById(R.id.buttonDestroy);

        fab = findViewById(R.id.fab);
    }

    private void setOperation(){
        if(!sp.contains(SharedKeys.User))
            setForCreate();
        else if(!sp.getBoolean(SharedKeys.Logged, false))
            setForLogin();
        else
            setForEdit();
    }

    private void setForCreate(){
        setTitle("Create User");

        buttonDestroy.setVisibility(View.INVISIBLE);

        setButtons("create");
    }

    private void setForLogin(){
        setTitle("Login");

        editTextEmail.setVisibility(View.INVISIBLE);
        editTextPhone.setVisibility(View.INVISIBLE);
        buttonDestroy.setVisibility(View.INVISIBLE);

        setButtons("login");
    }

    private void setForEdit(){
        setTitle("Edit User");

        editTextUser.setText(sp.getString(SharedKeys.User, ""));
        editTextEmail.setText(sp.getString(SharedKeys.Email, ""));
        editTextPhone.setText(sp.getString(SharedKeys.Phone, ""));

        setButtons("edit");
    }

    private void setButtons(String operation){
        switch (operation){
            case "create":
                fab.setImageResource(android.R.drawable.ic_menu_save);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String user = editTextUser.getText().toString();
                        String password = editTextPassword.getText().toString();
                        String email = editTextEmail.getText().toString();
                        String phone = editTextPhone.getText().toString();

                        if(user.length() > 0 && password.length() > 0 && email.length() > 0 && phone.length() > 0){
                            SharedPreferences.Editor e = sp.edit();
                            e.putString(SharedKeys.User, user);
                            e.putString(SharedKeys.Password, password);
                            e.putString(SharedKeys.Email, email);
                            e.putString(SharedKeys.Phone, phone);
                            e.putBoolean(SharedKeys.Logged, true);
                            e.commit();

                            showToast("User created");

                            finish();
                        }
                        else
                            showToast("All fields are required");
                    }
                });
                break;
            case "login":
                fab.setImageResource(android.R.drawable.ic_menu_directions);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String user = editTextUser.getText().toString();
                        String password = editTextPassword.getText().toString();
                        if(user.length() > 0 &&
                            password.length() > 0 &&
                            user.equals(sp.getString(SharedKeys.User, "")) &&
                            password.equals(sp.getString(SharedKeys.Password, ""))
                        ){
                            SharedPreferences.Editor e = sp.edit();
                            e.putBoolean(SharedKeys.Logged, true);
                            e.commit();
                            finish();
                        }
                        else {
                            showToast("Wrong username or password");
                        }
                    }
                });
                break;
            case "edit":
                fab.setImageResource(android.R.drawable.ic_menu_save);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String user = editTextUser.getText().toString();
                        String password = editTextPassword.getText().toString();
                        String email = editTextEmail.getText().toString();
                        String phone = editTextPhone.getText().toString();

                        if(user.length() > 0 && password.length() > 0 && email.length() > 0 && phone.length() > 0){
                            SharedPreferences.Editor e = sp.edit();
                            e.putString(SharedKeys.User, user);
                            e.putString(SharedKeys.Password, password);
                            e.putString(SharedKeys.Email, email);
                            e.putString(SharedKeys.Phone, phone);
                            e.putBoolean(SharedKeys.Logged, true);
                            e.commit();

                            showToast("User data changed");

                            finish();
                        }
                        else
                            showToast("All fields are required");
                    }
                });

                buttonDestroy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor e = sp.edit();

                        e.clear();

                        e.commit();

                        MainActivity.fa.finish();
                        finish();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        showToast("All data cleared");
                    }
                });
                break;
        }
    }

    @Override
    public void onBackPressed(){
        if(sp.getBoolean(SharedKeys.Logged, false))
            super.onBackPressed();
        else{
            MainActivity.fa.finish();
            finish();
        }
    }

    public void showToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
