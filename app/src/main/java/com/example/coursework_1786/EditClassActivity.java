package com.example.coursework_1786;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditClassActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu cho việc chọn hình ảnh
    private Uri imageUri; // Biến để lưu trữ URI của hình ảnh đã chọn
    private EditText imageEditText, titleEditText, contentEditText; // Biến cho EditText
    private ImageView previewImage;
    private Button editClassBtn;
    private DatabaseHelper databaseHelper; // Thêm biến DatabaseHelper
    FirebaseDatabase database;
    DatabaseReference reference;
    Spinner spinnerTeacher;
    List<String> teacherIds;
    String currentTeacherId;
    String classId;
    String courseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        databaseHelper = new DatabaseHelper(this);
        database = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/");

        titleEditText = findViewById(R.id.edit_class_title);
        contentEditText = findViewById(R.id.edit_class_content);
        imageEditText = findViewById(R.id.edit_class_image);
        previewImage = findViewById(R.id.image_preview);
        editClassBtn = findViewById(R.id.button_edit_class);

        classId = getIntent().getStringExtra("classId");

        loadTeachers();

        spinnerTeacher = findViewById(R.id.spinner_teacher);

        imageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });

        loadClassInfo();

        editClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateClass();
            }
        });

    }
    private void updateClass() {
        // Get the updated values from the EditTexts
        String updatedTitle = titleEditText.getText().toString().trim();
        String updatedContent = contentEditText.getText().toString().trim();
        String updatedImageUrl = imageEditText.getText().toString().trim();

        int selectedPosition = spinnerTeacher.getSelectedItemPosition(); // Get selected position
        int selectedTeacherId = Integer.parseInt(teacherIds.get(selectedPosition)); // Get userId based on selected position

        // Create a Course object to update SQLite
        Lesson lesson = new Lesson(Integer.parseInt(classId), Integer.parseInt(courseId), selectedTeacherId, updatedTitle, updatedContent, updatedImageUrl);

        // Update the class information in SQLite
        int result = databaseHelper.updateLesson(lesson);
        if (result > 0) {
            // If SQLite update is successful, update Firebase
            Map<String, Object> updates = new HashMap<>();
            updates.put("title", updatedTitle);
            updates.put("content", updatedContent);
            updates.put("imageUrl", updatedImageUrl);
            updates.put("userId", selectedTeacherId);

            reference = database.getReference("Classes").child(classId);
            reference.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditClassActivity.this, "Class updated successfully in SQLite and synchronized to Firebase", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                } else {
                    Toast.makeText(EditClassActivity.this, "Failed to update class in Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(EditClassActivity.this, "Failed to update class in SQLite", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadClassInfo(){
        reference = database.getReference("Classes").child(classId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if (snapshot.exists()){
                        String classTitle = snapshot.child("title").getValue(String.class);
                        String classContent = snapshot.child("content").getValue(String.class);
                        currentTeacherId = String.valueOf(snapshot.child("userId").getValue(Long.class));
                        courseId = String.valueOf(snapshot.child("courseId").getValue(Long.class));
                        String classImage = snapshot.child("imageUrl").getValue(String.class);

                        // Cập nhật các trường trong form


                        titleEditText.setText(classTitle); // Điền tiêu đề vào EditText
                        contentEditText.setText(classContent); // Điền nội dung vào EditText
                        imageEditText.setText(classImage); // Điền URL hình ảnh vào EditText

                        if(classImage != null) {
                            File imageFile = new File(classImage);
                            Bitmap myImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            previewImage.setImageBitmap(myImage);
                        }

                    }else {
                        Toast.makeText(EditClassActivity.this, "Class not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditClassActivity.this, "Failed to load class info: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadTeachers() {
        reference = database.getReference("Users");
        reference.orderByChild("role").equalTo("teacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> teacherNames = new ArrayList<>(); // Create a list to hold teacher names
                List<String> teacherIds = new ArrayList<>(); // Create a list to hold teacher IDs
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String fullName = snapshot.child("fullName").getValue(String.class); // Get full name
                    String userId = snapshot.getKey();
                    if (fullName != null) {
                        teacherNames.add(fullName); // Add to the list
                        teacherIds.add(userId); // Add userId to the list
                    }
                }
                // Store the teacherIds in a member variable for later use
                EditClassActivity.this.teacherIds = teacherIds;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditClassActivity.this, android.R.layout.simple_spinner_item, teacherNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTeacher.setAdapter(adapter); // Set adapter to spinner

                int position = teacherIds.indexOf(currentTeacherId);
                spinnerTeacher.setSelection(position);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(EditClassActivity.this, "Failed to load teachers: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
            imageEditText.setText(savedImagePath);

            // Cập nhật hình ảnh xem trước
            Bitmap myImage = BitmapFactory.decodeFile(savedImagePath);
            previewImage.setImageBitmap(myImage); // Hiển thị hình ảnh mới
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
        return savedImagePath;
    }
}