package com.example.coursework_1786.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework_1786.CourseDetailActivity;
import com.example.coursework_1786.Models.Course;
import com.example.coursework_1786.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

  private List<Course> courseList;
  private final Context context;

  // Constructor
  public CourseAdapter(Context context, List<Course> courseList) {
    this.context = context;
    this.courseList = courseList;
  }
  public void updateCourseList(List<Course> newCourseList) {
    this.courseList = newCourseList;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
    return new CourseViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
    // Lấy thông tin khóa học từ danh sách
    Course course = courseList.get(position);

    // Gán dữ liệu vào các view
    holder.courseTitle.setText(course.getTitle());
    holder.courseDescription.setText(course.getDescription());
    holder.courseType.setText(course.getType());
    holder.courseDuration.setText(course.getDuration() + " hours");
    holder.coursePrice.setText("$" + course.getPrice());

    // Xử lý sự kiện cho nút Details
    holder.detailsButton.setOnClickListener(v -> {
      // Mở một Activity mới để hiển thị chi tiết khóa học
      Intent intent = new Intent(context, CourseDetailActivity.class);
      intent.putExtra("courseId", String.valueOf(course.getCourseId()));
      context.startActivity(intent);
    });

    // Xử lý sự kiện cho nút Delete
    holder.deleteButton.setOnClickListener(v -> {
      deleteCourse(course);
    });
  }

  @Override
  public int getItemCount() {
    return courseList.size();
  }

  // Lớp ViewHolder để giữ các view trong item_course.xml
  public static class CourseViewHolder extends RecyclerView.ViewHolder {

    ImageView courseImage;
    TextView courseTitle, courseDescription, courseType, courseDuration, coursePrice;
    Button detailsButton, deleteButton;

    public CourseViewHolder(@NonNull View itemView) {
      super(itemView);
      courseTitle = itemView.findViewById(R.id.course_title);
      courseDescription = itemView.findViewById(R.id.course_description);
      courseType = itemView.findViewById(R.id.course_type);
      courseDuration = itemView.findViewById(R.id.course_duration);
      coursePrice = itemView.findViewById(R.id.course_price);
      detailsButton = itemView.findViewById(R.id.btn_details);
      deleteButton = itemView.findViewById(R.id.btn_delete);
    }
  }

  // Phương thức để xóa khóa học
  private void deleteCourse(Course course) {
    DatabaseReference courseRef = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Courses").child(String.valueOf(course.getCourseId()));
    courseRef.removeValue().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        // Xóa thành công, cập nhật danh sách
        courseList.remove(course);
        notifyDataSetChanged();
      } else {
        // Xử lý lỗi nếu cần
        Log.e("CourseAdapter", "Failed to delete course", task.getException());
      }
    });
  }
}
