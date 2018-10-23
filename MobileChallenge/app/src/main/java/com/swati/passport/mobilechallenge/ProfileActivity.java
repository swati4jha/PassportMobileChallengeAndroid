package com.swati.passport.mobilechallenge;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{
    private DatabaseReference mDatabase;
    private ValueEventListener mUserListener;
    private Query mUSerQuery;
    private String userID;
    TextView name,gender,age;
    EditText hobbies;
    Button buttonDelete, buttonUpdate;
    ImageView imageViewProfile;
    UserVo mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userID = getIntent().getStringExtra(UserListAdapter.USER_ID);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        name = findViewById(R.id.textViewUName);
        age = findViewById(R.id.textViewUAge);
        gender = findViewById(R.id.textViewUGender);
        imageViewProfile = findViewById(R.id.imageViewProfile);

        hobbies = findViewById(R.id.editTextUHobbies);
        hobbies.setOnTouchListener(this);
        hobbies.setKeyListener(null);
        hobbies.setBackgroundResource(android.R.color.transparent);

        buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(this);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(this);

    }


    //Hobbies to be enabled for editing on touch of the view.
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        EditText hobbies = v.findViewById(v.getId());
        hobbies.setCursorVisible(true);
        hobbies.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        hobbies.setSelection(hobbies.getText().length());
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    mUser = noteDataSnapshot.getValue(UserVo.class);
                }
                if(null != mUser){
                    // Setting background color based on gender
                    if(mUser.getGender().equalsIgnoreCase("Female")){
                        findViewById(R.id.profileView).setBackgroundResource(R.drawable.female);
                    }else{
                        findViewById(R.id.profileView).setBackgroundResource(R.drawable.male);
                    }
                    name.setText(mUser.getUserName());
                    age.setText(String.valueOf(mUser.getAge()));
                    gender.setText(mUser.getGender());
                    hobbies.setText(mUser.getHobbies());
                    //Loading profile image using Picasso library
                    Picasso.with(imageViewProfile.getContext()).load(mUser.getUserImage()).transform(new Utils.CircleTransform()).into(imageViewProfile);

                }else{
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getUser:onCancelled", databaseError.toException().toString());

            }

        };
        mUSerQuery = mDatabase.orderByChild("_id").equalTo(Integer.parseInt(userID));
        mUSerQuery.addValueEventListener(userListener);
        mUserListener = userListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mUserListener != null) {
            mUSerQuery.removeEventListener(mUserListener);
        }
    }

    @Override
    public void onClick(View v) {
        final Context _this = this;
        switch (v.getId()){
            //case to delete profile
            case R.id.buttonDelete:
                new AlertDialog.Builder(v.getContext())
                        .setMessage("Do you want to delete the profile?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDatabase.child(userID).removeValue();
                                Toast.makeText(_this,"Profile deleted.",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                break;
                // case to update profile.
            case R.id.buttonUpdate:
                String userHobbies = hobbies.getText().toString();
                if(TextUtils.isEmpty(userHobbies)){
                    Toast.makeText(this,"Please enter hobbies.",Toast.LENGTH_SHORT).show();
                } else if(!mUser.getHobbies().equals(userHobbies)){
                    mUser.setHobbies(userHobbies);
                    mDatabase.child(userID).setValue(mUser);
                    hobbies.setCursorVisible(false);
                    hobbies.setKeyListener(null);
                    Toast.makeText(this,"Profile updated.",Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }

    }
}
