package com.example.coursework_1786;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework_1786.Adapter.ClassAdapter;
import com.example.coursework_1786.Database.DatabaseHelper;
import com.example.coursework_1786.Models.Lesson;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {
  RecyclerView classRecyclerView;
  ClassAdapter classAdapter;
  List<Lesson> classList;
  String courseId;
  EditText searchEditText;
  DatabaseHelper databaseHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_course_detail);

    courseId = getIntent().getStringExtra("courseId");

    databaseHelper = new DatabaseHelper(this);

    // Initialize RecyclerView
    classRecyclerView = findViewById(R.id.recycler_view);
    classRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    classList = new ArrayList<>();
    classAdapter = new ClassAdapter(this, classList);
    classRecyclerView.setAdapter(classAdapter);

    loadClassesForCourse(Integer.parseInt(courseId));

    searchEditText = findViewById(R.id.edit_text_search); // Khởi tạo EditText tìm kiếm
    Button searchButton = findViewById(R.id.btn_search); // Khởi tạo Button tìm kiếm

    searchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        searchClasses(searchEditText.getText().toString());
      }
    });
  }
  public void addClass(View view) {
    Intent intent = new Intent(CourseDetailActivity.this, AddClassActivity.class);
    intent.putExtra("courseId", courseId); // Pass courseId to AddClassActivity
    startActivity(intent); // Start AddClassActivity
  }

  private void loadClassesForCourse(int courseId) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Classes");
    Query query = databaseReference.orderByChild("courseId").equalTo(courseId); // Filter by courseId

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        classList.clear(); // Clear the list before adding new data
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Lesson lesson = snapshot.getValue(Lesson.class);
          if (lesson != null) {
            classList.add(lesson);
          } else {
            Log.e("FirebaseData", "Lesson is null for snapshot: " + snapshot);
          }
        }
        classAdapter.notifyDataSetChanged(); // Notify adapter of data changes
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e("FirebaseData", "Error fetching data: " + databaseError.getMessage());
      }
    });
  }
  private void searchClasses(String query) {
    List<Lesson> filteredList = new ArrayList<>();

    // Lặp qua classList để tìm kiếm theo teacherId
    for (Lesson lesson : classList) {
      int teacherId = lesson.getUserId();
      System.out.println(teacherId);
      String teacherName = databaseHelper.getUser(teacherId).getFullName();
      System.out.println(teacherName);
      if (teacherName != null && teacherName.toLowerCase().contains(query.toLowerCase())) {
        filteredList.add(lesson);
      }
    }

    classAdapter.updateClassList(filteredList);
  }
}