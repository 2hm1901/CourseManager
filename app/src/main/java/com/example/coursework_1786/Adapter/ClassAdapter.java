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

import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework_1786.EditClassActivity;
import com.example.coursework_1786.Models.Course;
import com.example.coursework_1786.Models.Lesson;
import com.example.coursework_1786.R;

import java.io.File;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// ClassAdapter.java
public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
  private List<Lesson> classList;
  private final Context context;

  public ClassAdapter(Context context, List<Lesson> classList) {
    this.context = context;
    this.classList = classList;
  }
  public void updateClassList(List<Lesson> newClassList) {
    this.classList = newClassList;
    notifyDataSetChanged();
  }

  @Override
  public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
    return new ClassViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ClassViewHolder holder, int position) {
    Lesson lesson = classList.get(position);
    holder.lessonTitle.setText(lesson.getTitle());
    holder.lessonContent.setText(lesson.getContent());

    getUserNameFromFirebase(String.valueOf(lesson.getUserId()), holder.lessonTeacherName);

    File imageFile = new File(lesson.getImageUrl());
    Bitmap myImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    holder.lessonImage.setImageBitmap(myImage);

    holder.btnEdit.setOnClickListener(view -> {
      Intent intent = new Intent(context, EditClassActivity.class);
      intent.putExtra("classId", String.valueOf(lesson.getLessonId()));
      context.startActivity(intent);
    });

    holder.btnDelete.setOnClickListener(view -> {
      deleteClass(lesson);
    });
  }

  private void getUserNameFromFirebase(String userId, TextView textView) {
    DatabaseReference databaseReference = FirebaseDatabase
                                          .getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                          .getReference("Users").child(userId);
    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          String userName = dataSnapshot.child("fullName").getValue(String.class);
          textView.setText("Teacher: " + userName);
        } else {
          textView.setText("Unknown User");
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        textView.setText("Error fetching name");
      }
    });
  }

  private void deleteClass(Lesson lesson){
    DatabaseReference lessonRef = FirebaseDatabase.getInstance("https://coursework-1786-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Classes").child(String.valueOf(lesson.getLessonId()));
    lessonRef.removeValue().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        classList.remove(lesson);
        notifyDataSetChanged();
      } else {
        Log.e("ClassAdapter", "Failed to delete class", task.getException());
      }
    });
  }

  @Override
  public int getItemCount() {
    return classList.size();
  }

  public static class ClassViewHolder extends RecyclerView.ViewHolder {
    TextView lessonTitle, lessonContent, lessonTeacherName;
    ImageView lessonImage;
    Button btnEdit, btnDelete;

    public ClassViewHolder(View itemView) {
      super(itemView);
      lessonTitle = itemView.findViewById(R.id.lessonTitle);
      lessonContent = itemView.findViewById(R.id.lessonContent);
      lessonTeacherName = itemView.findViewById(R.id.lessonTeacherName);
      lessonImage = itemView.findViewById(R.id.lessonImage);
      btnDelete = itemView.findViewById(R.id.btnDelete);
      btnEdit = itemView.findViewById(R.id.btnEdit);
    }
  }
}

