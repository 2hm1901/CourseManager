package com.example.coursework_1786;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coursework_1786.Adapter.CourseAdapter;
import com.example.coursework_1786.Models.Course;
import com.example.coursework_1786.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  BottomNavigationView bottomNavigationView;
  List<Course> courseList;
  CourseAdapter courseAdapter;
  RecyclerView courseRecycle;
  Spinner spinnerSortTime, spinnerSortCategory, spinnerSortPrice;
  DatabaseReference database;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    database = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Courses");

    spinnerSortTime = findViewById(R.id.spinner_sort_time);
    spinnerSortCategory = findViewById(R.id.spinner_sort_category);
    spinnerSortPrice = findViewById(R.id.spinner_sort_price);

    // Adapter cho Spinner
    ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sort_time_options,
            android.R.layout.simple_spinner_item
    );
    timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerSortTime.setAdapter(timeAdapter);

    ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sort_category_options,
            android.R.layout.simple_spinner_item
    );
    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerSortCategory.setAdapter(categoryAdapter);

    ArrayAdapter<CharSequence> priceAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sort_price_options,
            android.R.layout.simple_spinner_item
    );
    priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerSortPrice.setAdapter(priceAdapter);

    // Initialize Bottom Navigation
    BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      if (item.getItemId() == R.id.nav_home) {
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        return true;
      } else if (item.getItemId() == R.id.nav_add) {
        startActivity(new Intent(MainActivity.this, AddCourseActivity.class));
        return true;
      } else if (item.getItemId() == R.id.nav_search) {
        startActivity(new Intent(MainActivity.this, SearchActivity.class));
        return true;
      }
      return false;
    });

    // Initialize RecyclerView
    courseRecycle = findViewById(R.id.recycler_view);
    courseList = new ArrayList<>();
    courseAdapter = new CourseAdapter(this, courseList);

    courseRecycle.setLayoutManager(new GridLayoutManager(this, 1));
    courseRecycle.setAdapter(courseAdapter);

    // Load courses from Firebase
    loadCoursesFromFirebase();

    Button buttonFilter = findViewById(R.id.button_filter);
    buttonFilter.setOnClickListener(v -> applyFilter());
  }

  private void loadCoursesFromFirebase() {
    database.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        courseList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Course course = snapshot.getValue(Course.class);
          courseList.add(course);
        }
        courseAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        // Handle possible errors.
        System.out.println("1");

        Log.e("MainActivity", "Database error: " + databaseError.getMessage());
      }
    });
  }
  private void applyFilter() {

    String selectedTime = spinnerSortTime.getSelectedItem().toString();
    String selectedCategory = spinnerSortCategory.getSelectedItem().toString();
    String selectedPrice = spinnerSortPrice.getSelectedItem().toString();

    // Danh sách đã lọc
    List<Course> filteredList = new ArrayList<>();

    // Lọc theo danh mục
    switch (selectedCategory) {
      case "All Type":
        filteredList = courseList;
        break;
      case "Flow Yoga":
        for (Course course : courseList) {
          if (course.getType().equals("Flow Yoga")) {
            filteredList.add(course);
          }
        }
        break;
      case "Aerial Yoga":
        for (Course course : courseList) {
          if (course.getType().equals("Aerial Yoga")) {
            filteredList.add(course);
          }
        }
        break;
      case "Family Yoga":
        for (Course course : courseList) {
          if (course.getType().equals("Family Yoga")) {
            filteredList.add(course);
          }
        }
        break;
      default:
        break;
    }

    // Sắp xếp theo thời gian
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if (filteredList.size() > 2) {
      switch (selectedTime) {
        case "Most Recent":
          filteredList.sort((course1, course2) -> {
            try {
              Date date1 = dateFormat.parse(course2.getCreationDate());
              Date date2 = dateFormat.parse(course1.getCreationDate());
              return date1.compareTo(date2);
            } catch (ParseException e) {
              e.printStackTrace();
              return 0;
            }
          });
          break;
        case "Oldest":
          filteredList.sort((course1, course2) -> {
            try {
              Date date1 = dateFormat.parse(course1.getCreationDate());
              Date date2 = dateFormat.parse(course2.getCreationDate());
              return date1.compareTo(date2);
            } catch (ParseException e) {
              e.printStackTrace();
              return 0;
            }
          });
          break;
        default:
          break;
      }
      }

    // Sắp xếp theo giá
    switch (selectedPrice) {
      case "Most Expensive":
        filteredList.sort((course1, course2) -> Double.compare(course2.getPrice(), course1.getPrice()));
        break;
      case "Cheapest":
        filteredList.sort((course1, course2) -> Double.compare(course1.getPrice(), course2.getPrice()));
        break;
      default:
        break;
    }

    courseAdapter.updateCourseList(filteredList);
  }
}
