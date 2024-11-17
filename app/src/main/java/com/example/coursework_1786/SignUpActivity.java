package com.example.coursework_1786;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coursework_1786.Database.DatabaseHelper;
import com.example.coursework_1786.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

  EditText signUpName, signUpEmail, signUpPass, signUpConfirmPass;
  TextView loginRedirectText;
  Button signUpBtn;
  FirebaseDatabase database;
  DatabaseReference reference;
  DatabaseHelper dbHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);

    signUpName = findViewById(R.id.nameRegister);
    signUpEmail = findViewById(R.id.emailRegister);
    signUpPass = findViewById(R.id.passwordRegister);
    signUpConfirmPass = findViewById(R.id.confirmPasswordRegister);
    loginRedirectText = findViewById(R.id.loginTextView);
    signUpBtn = findViewById(R.id.registerButton);

    dbHelper = new DatabaseHelper(this);

    signUpBtn.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view){

        String fullName = signUpName.getText().toString();
        String email = signUpEmail.getText().toString();
        String password = signUpPass.getText().toString();
        String confirmPassword = signUpConfirmPass.getText().toString();

        // Validate input fields
        if (!validateInput(fullName, email, password, confirmPassword)) {
          return; // Stop further execution if validation fails
        }

        long userId = dbHelper.insertUser(fullName, email, password,"", "");
        if (userId != -1) {
          // Save user in Firebase using the same userId from SQLite
          saveUserDataToFirebase((int) userId);
        } else {
          Toast.makeText(SignUpActivity.this, "Failed to register user locally", Toast.LENGTH_SHORT).show();
        }
      }
    });

    loginRedirectText.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
      }
    });
  }

  private void saveUserDataToFirebase(int userId) {
    User user = dbHelper.getUser(userId);
    database = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/");
    reference = database.getReference("Users");

    reference.child(String.valueOf(userId)).setValue(user)
            .addOnSuccessListener(aVoid -> {
              // Data saved successfully
              Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
              startActivity(intent);
            })
            .addOnFailureListener(e -> {
              // Failed to save data
              Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
  }

  private boolean validateInput(String fullName, String email, String password, String confirmPassword) {
    if (fullName.isEmpty()) {
      signUpName.setError("Name cannot be empty!");
      return false;
    }
    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      signUpEmail.setError("Email cannot be empty!");
      return false;
    }
    if (password.isEmpty() || password.length() < 6) {
      signUpPass.setError("Password must be at least 6 characters");
      return false;
    }
    if (!password.equals(confirmPassword)) {
      signUpConfirmPass.setError("Passwords do not match");
      return false;
    }
    return true; // All validations passed
  }
}
