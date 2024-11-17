package com.example.coursework_1786;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coursework_1786.Database.DatabaseHelper;
import com.example.coursework_1786.Models.Lesson;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddClassActivity extends AppCompatActivity {
  private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu cho việc chọn hình ảnh
  private Uri imageUri; // Biến để lưu trữ URI của hình ảnh đã chọn
  private EditText selectImageClass; // Biến cho EditText
  private DatabaseHelper databaseHelper; // Thêm biến DatabaseHelper
  FirebaseDatabase database;
  DatabaseReference reference;
  Spinner spinnerTeacher;
  List<String> teacherIds;
  String courseId;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_class);
    courseId = getIntent().getStringExtra("courseId");

    spinnerTeacher = findViewById(R.id.spinner_teacher);
    loadTeachers(); // Load teachers name into the spinner

    selectImageClass = findViewById(R.id.edit_class_image);
    selectImageClass.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        openImageChooser();
      }
    });

    databaseHelper = new DatabaseHelper(this);
    Button addClassBtn = findViewById(R.id.button_add_class);
    addClassBtn.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        saveClass();
      }
    });

  }
  private void loadTeachers() {
    database = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/");
    reference = database.getReference("Users");
    reference.orderByChild("role").equalTo("teacher").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        List<String> teacherNames = new ArrayList<>(); // Create a list to hold teacher names
        List<String> teacherIds = new ArrayList<>(); // Create a list to hold teacher IDs
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          String fullName = snapshot.child("fullName").getValue(String.class); // Get full name
          String userId = snapshot.getKey(); // Get userId (assuming it's the key)
          if (fullName != null) {
            teacherNames.add(fullName); // Add to the list
            teacherIds.add(userId); // Add userId to the list
          }
        }
        // Store the teacherIds in a member variable for later use
        AddClassActivity.this.teacherIds = teacherIds;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddClassActivity.this, android.R.layout.simple_spinner_item, teacherNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(adapter); // Set adapter to spinner
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        // Handle possible errors.
        Toast.makeText(AddClassActivity.this, "Failed to load teachers: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }
  private void openImageChooser() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
      imageUri = data.getData();
      // Lưu hình ảnh vào file local và lấy đường dẫn
      String savedImagePath = saveImageToLocal(imageUri);
      // Hiển thị đường dẫn của hình ảnh đã lưu vào EditText
      selectImageClass.setText(savedImagePath);
    }
  }

  private String saveImageToLocal(Uri uri) {
    String savedImagePath = null;
    try {
      InputStream inputStream = getContentResolver().openInputStream(uri);
      // Tạo tên file duy nhất dựa trên thời gian hiện tại
      String fileName = "saved_image_" + System.currentTimeMillis() + ".jpg";
      File localFile = new File(getFilesDir(), fileName); // Đặt tên file và đường dẫn
      FileOutputStream outputStream = new FileOutputStream(localFile);

      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }

      outputStream.close();
      inputStream.close();

      savedImagePath = localFile.getAbsolutePath(); // Lưu đường dẫn của file đã lưu
      Toast.makeText(this, "Image saved to: " + savedImagePath, Toast.LENGTH_LONG).show();
    } catch (Exception e) {
      e.printStackTrace();
      Toast.makeText(this, "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    return savedImagePath; // Trả về đường dẫn của hình ảnh đã lưu
  }
  private void saveClass(){
    String title = ((EditText) findViewById(R.id.edit_class_title)).getText().toString();
    String content = ((EditText)findViewById(R.id.edit_class_content)).getText().toString();

    int selectedPosition = spinnerTeacher.getSelectedItemPosition(); // Get selected position
    String teacherId = teacherIds.get(selectedPosition); // Get userId based on selected position

    String image = selectImageClass.getText().toString();

    long id = databaseHelper.insertLesson(courseId, teacherId, title, content, image);

    if (id != -1) {
      saveClassDataToFirebase((int) id);
    } else {
      Toast.makeText(this, "Failed to add course.", Toast.LENGTH_SHORT).show();
    }
  }

  private void saveClassDataToFirebase(int classId){
    Lesson lesson = databaseHelper.getLesson(classId);
    database = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/");
    reference = database.getReference("Classes");

    reference.child(String.valueOf(classId)).setValue(lesson)
            .addOnSuccessListener(aVoid->{
              Toast.makeText(AddClassActivity.this, "Course add successfully", Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(AddClassActivity.this, CourseDetailActivity.class);
              intent.putExtra("courseId", courseId);
              startActivity(intent);
            })
            .addOnFailureListener(e -> {
              Toast.makeText(AddClassActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
  }
}
