package com.example.coursework_1786;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework_1786.Database.DatabaseHelper;
import com.example.coursework_1786.Models.Course;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddCourseActivity extends AppCompatActivity {

  private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu cho việc chọn hình ảnh
  private Uri imageUri; // Biến để lưu trữ URI của hình ảnh đã chọn
  private EditText editCourseThumbnail; // Biến cho EditText
  private DatabaseHelper databaseHelper; // Thêm biến DatabaseHelper
  FirebaseDatabase database;
  DatabaseReference reference;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_add_course);

    Spinner spinnerCourseType = findViewById(R.id.spinner_course_type);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.course_types, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerCourseType.setAdapter(adapter);

    // Khởi tạo EditText
    editCourseThumbnail = findViewById(R.id.edit_course_thumbnail);

    // Thêm xử lý sự kiện cho EditText để chọn hình ảnh
    editCourseThumbnail.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openImageChooser();
      }
    });

    databaseHelper = new DatabaseHelper(this); // Khởi tạo DatabaseHelper

    Button buttonAddCourse = findViewById(R.id.button_add_course);
    buttonAddCourse.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        saveCourse(); // Gọi phương thức để lưu khóa học
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
      editCourseThumbnail.setText(savedImagePath);
    }
  }

  private String saveImageToLocal(Uri uri) {
    String savedImagePath = null;
    try {
      InputStream inputStream = getContentResolver().openInputStream(uri);
      // Tạo tên file duy nhất dựa trên thời gian hiện tại
      String fileName = "saved_image_" + System.currentTimeMillis() + ".jpg";
      File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
      File localFile = new File(publicDir, fileName); // Lưu vào thư mục Pictures
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

  private void saveCourse() {
    String title = ((EditText) findViewById(R.id.edit_course_title)).getText().toString();
    String description = ((EditText) findViewById(R.id.edit_course_description)).getText().toString();
    double price = Double.parseDouble(((EditText) findViewById(R.id.edit_course_price)).getText().toString());
    int duration = Integer.parseInt(((EditText) findViewById(R.id.edit_course_duration)).getText().toString());
    String type = ((Spinner) findViewById(R.id.spinner_course_type)).getSelectedItem().toString();
    String thumbnail = editCourseThumbnail.getText().toString();
    String creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

    long id = databaseHelper.insertCourse(title, description, price, duration, type, thumbnail, creationDate);
    System.out.println(id);

    if (id != -1) {
      saveCourseDataToFirebase((int) id);
    } else {
      Toast.makeText(this, "Failed to add course.", Toast.LENGTH_SHORT).show();
    }
  }

  private void saveCourseDataToFirebase(int courseId){
    Course course = databaseHelper.getCourse(courseId);
    database = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/");
    reference = database.getReference("Courses");


    reference.child(String.valueOf(courseId)).setValue(course)
            .addOnSuccessListener(aVoid -> {
              // Data saved successfully
              Toast.makeText(AddCourseActivity.this, "Course add successfully", Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(AddCourseActivity.this, MainActivity.class);
              startActivity(intent);
            })
            .addOnFailureListener(e -> {
              // Failed to save data
              Toast.makeText(AddCourseActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
  }
}
