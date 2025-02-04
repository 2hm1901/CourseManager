package com.example.coursework_1786;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
  EditText loginEmail, loginPass;
  Button loginBtn;
  TextView signUpRedirectText;
  String userId;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    loginEmail = findViewById(R.id.emailLogin);
    loginPass = findViewById(R.id.passwordLogin);
    signUpRedirectText = findViewById(R.id.registerTextView);
    loginBtn = findViewById(R.id.loginButton);

    loginBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!validateEmail() | !validatePass()){

        }else{
          checkUser();
        }
      }
    });

    signUpRedirectText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
      }
    });
  }

  public  Boolean validateEmail(){
    String val = loginEmail.getText().toString();
    if (val.isEmpty()){
      loginEmail.setError("Email cannot be empty!");
      return false;
    }else{
      loginEmail.setError(null);
      return true;
    }
  }

  public  Boolean validatePass(){
    String val = loginPass.getText().toString();
    if (val.isEmpty()){
      loginPass.setError("Email cannot be empty!");
      return false;
    }else{
      loginPass.setError(null);
      return true;
    }
  }
  public void checkUser(){
    String userEmail = loginEmail.getText().toString().trim();
    String userPass = loginPass.getText().toString().trim();

    DatabaseReference reference = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
    Query isUserExist = reference.orderByChild("email").equalTo(userEmail);

    isUserExist.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()){
          String passwordFromDB;

          loginEmail.setError(null);
          for (DataSnapshot userSnapShot : snapshot.getChildren()){
            userId = userSnapShot.getKey();
          }
          passwordFromDB = snapshot.child(userId).child("password").getValue(String.class);


          if (Objects.equals(passwordFromDB, userPass)){
            loginEmail.setError(null);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
          }else{
            loginPass.setError("Invalid Credentials!");
            loginPass.requestFocus();
          }
        }else{
          loginEmail.setError("User does not exist!");
          loginEmail.requestFocus();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }
}