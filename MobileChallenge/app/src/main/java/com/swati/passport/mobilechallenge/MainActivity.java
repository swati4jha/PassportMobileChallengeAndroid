package com.swati.passport.mobilechallenge;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AddProfileDialogFragment.DialogListener, View.OnClickListener{

    private ArrayList<UserVo> userList;
    private HashMap<String,String> filters;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private Query mUSerQuery;
    private static String downloadUrl;
    private ValueEventListener mUserListener;
    FloatingActionButton btnAddProfile;
    CheckBox cbMale,cbFemale;

    ProgressDialog pd;
    private static String GENDER_KEY = "GENDER";
    private static String GENDER_M = "Male";
    private static String GENDER_F = "Female";
    private static String GENDER_ALL = "ALL";
    private static String SORT_ORDER_KEY = "SORT_ORDER";
    private static String SORT_BY_KEY = "SORT_BY";
    private static String SORT_ORDER_ASC = "SORT_ORDER_ASC";
    private static String SORT_ORDER_DESC = "SORT_ORDER_DESC";
    private static String SORT_BY_ID = "SORT_BY_ID";
    private static String SORT_BY_AGE = "SORT_BY_AGE";
    private static String SORT_BY_NAME = "SORT_BY_NAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mStorage = FirebaseStorage.getInstance();


        filters = new HashMap<>();
        filters.put(GENDER_KEY,GENDER_ALL);
        filters.put(SORT_ORDER_KEY,SORT_ORDER_ASC);
        filters.put(SORT_BY_KEY,SORT_BY_ID);

        btnAddProfile = findViewById(R.id.btnAddProfile);
        btnAddProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        cbFemale = findViewById(R.id.checkBoxF);
        cbMale = findViewById(R.id.checkBoxM);
        cbFemale.setOnClickListener(this);
        cbMale.setOnClickListener(this);
        userList = new ArrayList<UserVo>();
        mRecyclerView = (RecyclerView) findViewById(R.id.userRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d("item"+position, (String) parent.getItemAtPosition(position));
                switch (position){
                    case 0:
                        filters.put(SORT_BY_KEY,SORT_BY_ID);
                        filters.put(SORT_ORDER_KEY,SORT_ORDER_ASC);
                        break;
                    case 1:
                        filters.put(SORT_BY_KEY,SORT_BY_NAME);
                        filters.put(SORT_ORDER_KEY,SORT_ORDER_ASC);
                        break;
                    case 2:
                        filters.put(SORT_BY_KEY,SORT_BY_NAME);
                        filters.put(SORT_ORDER_KEY,SORT_ORDER_DESC);
                        break;
                    case 3:
                        filters.put(SORT_BY_KEY,SORT_BY_AGE);
                        filters.put(SORT_ORDER_KEY,SORT_ORDER_ASC);
                        break;
                    case 4:
                        filters.put(SORT_BY_KEY,SORT_BY_AGE);
                        filters.put(SORT_ORDER_KEY,SORT_ORDER_DESC);
                        break;

                }
                sortUserList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
}

    private void filterUser() {
        if(!filters.get(GENDER_KEY).equalsIgnoreCase(GENDER_ALL)){
            mUSerQuery = mDatabase.orderByChild("gender").equalTo(filters.get(GENDER_KEY));
        } else{
            mUSerQuery = mDatabase.orderByChild("_id");
        }

        mUSerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList = new ArrayList<UserVo>();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    UserVo user = noteDataSnapshot.getValue(UserVo.class);
                    userList.add(user);
                    Log.d("PRR",user.toString());
                }
                sortUserList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("filterUser:onCancelled", databaseError.toException().toString());
            }
        });
    }

    private void sortUserList() {
        if(filters.get(SORT_ORDER_KEY).equalsIgnoreCase(SORT_ORDER_ASC)){
            if(filters.get(SORT_BY_KEY).equalsIgnoreCase(SORT_BY_NAME)){
                Collections.sort(userList, UserVo.UserNameComparatorAsc);
            } else if(filters.get(SORT_BY_KEY).equalsIgnoreCase(SORT_BY_AGE)){
                Collections.sort(userList, UserVo.UserAgeComparatorAsc);
            } else{
                Collections.sort(userList, UserVo.UserIDComparator);
            }

        } else{
            if(filters.get(SORT_BY_KEY).equalsIgnoreCase(SORT_BY_NAME)){
                Collections.sort(userList, UserVo.UserNameComparatorDesc);
            } else if(filters.get(SORT_BY_KEY).equalsIgnoreCase(SORT_BY_AGE)){
                Collections.sort(userList, UserVo.UserAgeComparatorDesc);
            }
        }

        mAdapter =  new UserListAdapter(userList);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList = new ArrayList<UserVo>();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    UserVo user = noteDataSnapshot.getValue(UserVo.class);
                    if(!filters.get(GENDER_KEY).equalsIgnoreCase(GENDER_ALL)){
                        if(filters.get(GENDER_KEY).equalsIgnoreCase(GENDER_F) && user.getGender().equalsIgnoreCase(GENDER_F)){
                            userList.add(user);
                        } else if(filters.get(GENDER_KEY).equalsIgnoreCase(GENDER_M) && user.getGender().equalsIgnoreCase(GENDER_M)){
                            userList.add(user);
                        }
                    }else{
                        userList.add(user);
                    }
                }
                sortUserList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getUserList:onCancelled", databaseError.toException().toString());

            }

        };
        mDatabase.addValueEventListener(userListener);
        mUserListener = userListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mUserListener != null) {
            mDatabase.removeEventListener(mUserListener);
        }
    }

    void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        FragmentManager fm = getSupportFragmentManager();
        AddProfileDialogFragment addProfileDialogFragment = AddProfileDialogFragment.newInstance();
        addProfileDialogFragment.show(fm, "dialog");


    }

    @Override
    public void onDialogSaveClick(DialogFragment dialog, final UserVo user, final ImageView mProfileImage) {
        Log.d("User to abe added:",user.toString());
        Query lastQuery = mDatabase.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            long key = 1;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    UserVo userVo = noteDataSnapshot.getValue(UserVo.class);
                    key = userVo.get_id() + 1;
                }
                user.set_id(key);
                addUserImage(mProfileImage,  user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getLastUser:onCancelled", databaseError.toException().toString());
            }
        });

        dialog.dismiss();
    }

    private void addUserImage(ImageView mProfileImage, final UserVo user) {
        Bitmap bitmap = mProfileImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data1 = baos.toByteArray();
        StorageReference storageRef = mStorage.getReference();
        final StorageReference imagesRef = storageRef.child(user.get_id()+"_photo.jpg");

        UploadTask uploadTask = imagesRef.putBytes(data1);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUrl = String.valueOf(task.getResult());
                    user.setUserImage(downloadUrl.toString());
                    Log.d("Image",downloadUrl.toString());

                    mDatabase.child(String.valueOf(user.get_id())).setValue(user);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if(cbMale.isChecked() && cbFemale.isChecked()){
            filters.put(GENDER_KEY,GENDER_ALL);
        } else if(cbMale.isChecked()){
            filters.put(GENDER_KEY,GENDER_M);
        }else if(cbFemale.isChecked()){
            filters.put(GENDER_KEY,GENDER_F);
        }else{
            filters.put(GENDER_KEY,GENDER_ALL);
        }
        Log.d("Gender",filters.get(GENDER_KEY));
        filterUser();
    }


}
