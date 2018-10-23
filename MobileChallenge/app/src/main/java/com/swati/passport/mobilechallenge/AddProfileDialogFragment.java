package com.swati.passport.mobilechallenge;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class AddProfileDialogFragment extends DialogFragment{
    private static int RESULT_LOAD_IMAGE = 1;
    ImageView mProfileImage;
    EditText mName,mAge,mHobbies;
    RadioButton mGender;
    RadioGroup mRadioGroup;
    Context mContext;

    public AddProfileDialogFragment(){

    }
    static AddProfileDialogFragment newInstance() {
        AddProfileDialogFragment f = new AddProfileDialogFragment();
        return f;
    }

    //Interface when user clicks on the save button on dialog.
    public interface DialogListener {
        public void onDialogSaveClick(DialogFragment dialog, UserVo user, ImageView mProfileImage);
    }

    // The interface to deliver action events to mainactivity
    DialogListener mListener;

   @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       mContext = context;
        try {
            mListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("MainActivity must implement DialogListener");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.dialog_add_profile, container, false);
        mProfileImage = v.findViewById(R.id.userImage);
        mName = v.findViewById(R.id.editTextName);
        mAge = v.findViewById(R.id.editTextAge);
        mHobbies = v.findViewById(R.id.editTextHobbies);
        mProfileImage = v.findViewById(R.id.userImage);
        mRadioGroup = v.findViewById(R.id.rGroupGender);

        //Radio button for gender
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mGender = v.findViewById(checkedId);
            }
        });

        //Creating round image for profile
        Bitmap mbitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.profile)).getBitmap();
        createRoundedImage(mbitmap);

        //Save button dialog box
        Button buttonSave = v.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mGender && !TextUtils.isEmpty(mGender.getText().toString())){
                    UserVo user = validateInputParams(mGender.getText().toString());
                    if(user!=null){
                        mListener.onDialogSaveClick(AddProfileDialogFragment.this,user,mProfileImage);

                    }
                }
                else{
                    Toast.makeText(mContext,"Please select gender.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Cancel button dialog box.
        Button buttonCancel = v.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProfileDialogFragment.this.getDialog().cancel();
            }
        });

        //Image upload button
        ImageButton buttonLoadImage = v.findViewById(R.id.imageButtonProfile);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
                    } else {
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Intent i = new Intent(
//                        Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        return v;
    }

    //Method to validate input params : name,age, hobbies before creating profile.
    private UserVo validateInputParams(String gender) {
        String name = mName.getText().toString();
        String age = mAge.getText().toString();
        String hobbies = mHobbies.getText().toString();
        long userAge = 0;
        if(TextUtils.isEmpty(name)){
            Toast.makeText(mContext,"Please enter name.",Toast.LENGTH_SHORT).show();
            return null;
        }

        if(TextUtils.isEmpty(age)){
            Toast.makeText(mContext,"Please enter age.",Toast.LENGTH_SHORT).show();
            return null;
        } else if(null != age){
            try {
                 userAge = Integer.parseInt(age);
                 if(userAge < 1 || userAge > 1000){
                     Toast.makeText(mContext,"Please enter valid age.",Toast.LENGTH_SHORT).show();
                     return null;
                 }
            } catch (Exception ex){
                Toast.makeText(mContext,"Please enter valid age.",Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        if(TextUtils.isEmpty(hobbies)){
            Toast.makeText(mContext,"Please enter hobbies.",Toast.LENGTH_SHORT).show();
            return null;
        }

        //Creating user object with defaut id and image, id and image will be set in savedialog
        UserVo user = new UserVo(-1,name,userAge,gender,"image",hobbies);
        return user;
    }

    @NonNull
    private void createRoundedImage(Bitmap mbitmap) {
        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), mbitmap.getWidth(), mbitmap.getHeight(), mpaint);
        mProfileImage.setImageBitmap(imageRounded);
        mProfileImage.setDrawingCacheEnabled(true);
        mProfileImage.buildDrawingCache();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE  && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try{
                createRoundedImage(BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage)));
            }catch (FileNotFoundException ex){
                Log.d("Exception",ex.toString());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {
                    Toast.makeText(mContext,"Gallery permission needed.",Toast.LENGTH_SHORT).show();
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }
}
