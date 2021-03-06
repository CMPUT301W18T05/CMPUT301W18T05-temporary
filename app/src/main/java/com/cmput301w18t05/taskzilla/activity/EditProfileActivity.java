/*
 * Copyright 2018 (c) Andy Li, Colin Choi, James Sun, Jeremy Ng, Micheal Nguyen, Wyatt Praharenka
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.cmput301w18t05.taskzilla.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cmput301w18t05.taskzilla.AppColors;
import com.cmput301w18t05.taskzilla.EmailAddress;
import com.cmput301w18t05.taskzilla.PhoneNumber;
import com.cmput301w18t05.taskzilla.Photo;
import com.cmput301w18t05.taskzilla.R;
import com.cmput301w18t05.taskzilla.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    private EditText NameText;
    private EditText EmailText;
    private EditText PhoneText;
    private ImageView profilePicture;
    private Integer maxSize;

    private User user = new User(); //dummy

    /**
     *
     * this runs when activity created, setting the default fields for the user
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit Profile");
        setContentView(R.layout.activity_edit_profile);

        AppColors appColors = AppColors.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(appColors.getActionBarColor())));
        actionBar.setTitle(Html.fromHtml("<font color='"+ appColors.getActionBarTextColor() +
                "'>Taskzilla</font>"));

        NameText = findViewById(R.id.NameField);
        EmailText = findViewById(R.id.EmailField);
        PhoneText = findViewById(R.id.Phone);
        profilePicture = findViewById(R.id.ProfilePictureView);


        String userName = getIntent().getStringExtra("Name");
        String userEmail = getIntent().getStringExtra("Email");
        String userPhone = getIntent().getStringExtra("Phone");
        String userPicture = getIntent().getStringExtra("Photo");
        user.setName(userName);
        user.setEmail(new EmailAddress(userEmail));
        user.setPhone(new PhoneNumber(userPhone));
        NameText.setText(user.getName());
        EmailText.setText(user.getEmail().toString());
        PhoneText.setText(user.getPhone().toString());
        try {
            user.setPhoto(new Photo(userPicture));
            profilePicture.setImageBitmap(user.getPhoto().StringToBitmap());
        }
        catch (Exception e){
            Photo defaultPhoto = new Photo("");
            profilePicture.setImageBitmap(defaultPhoto.StringToBitmap());
        }

        profilePicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                profilePictureClicked();
            }
        });
    }

    /**
     *
     * when listener detects that profile picture clicked,
     * switch activity to ProfileActivity
     *
     */
    public void profilePictureClicked() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 5);
    }

    /**
     *
     * Cancel out of editing profile
     *
     * @param view
     */
    public void ProfileCancelButton(View view){
        finish();
    }

    public void ProfileSaveButton(View view){
        if(validateInformation()){
            user.setName(NameText.getText().toString());
            user.setEmail(new EmailAddress(EmailText.getText().toString()));
            user.setPhone(new PhoneNumber(PhoneText.getText().toString()));
            Intent returnIntent = new Intent();
            returnIntent.putExtra("Name", user.getName());
            returnIntent.putExtra("Email", user.getEmail().toString());
            returnIntent.putExtra("Phone",user.getPhone().toString());
            returnIntent.putExtra("Photo", user.getPhoto().toString());
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    /**
     * checks if the information that was inputted for contact information and such are
     * allowed, if not return false showing that the user needs to change info
     *
     * @return a boolean specifying whether the information is correct or not
     */
    public boolean validateInformation() {
        // Taken from https://stackoverflow.com/questions/18463848/how-to-tell-if-a-random-string-is-an-email-address-or-something-else
        // 2018/03/18

        if(TextUtils.isEmpty(NameText.getText())) {
            NameText.requestFocus();
            NameText.setError("Name required!");
            return false;
        } else if(NameText.getText().length() > 30) {
            NameText.requestFocus();
            NameText.setError("Name too long!");
            return false;
        } else {
            String nameTemp = NameText.getText().toString();
            Pattern nameConstraint = Pattern.compile("[^a-zA-Z ]");
            boolean hasChar = nameConstraint.matcher(nameTemp).find();
            if(hasChar == true) {
                NameText.requestFocus();
                NameText.setError("Name contains illegal character!");
                return false;
            }
        }

        if(TextUtils.isEmpty(EmailText.getText())) {
            EmailText.requestFocus();
            EmailText.setError("Email required!");
            return false;
        } else if(EmailText.getText().length() > 30) {
            EmailText.requestFocus();
            EmailText.setError("Email too long!");
            return false;
        } else {
            String emailTemp = EmailText.getText().toString();

            Pattern p = Pattern.compile("[a-zA-z0-9._%+-]{1,}+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9]{1,}");
            Matcher m = p.matcher(emailTemp);

            boolean matchFound = m.matches();
            if(matchFound) {
                // valid email do nothing
            }
            else {
                EmailText.requestFocus();
                EmailText.setError("Email invalid!");
                return false;
            }
        }
        if(TextUtils.isEmpty(PhoneText.getText())) {
            PhoneText.requestFocus();
            PhoneText.setError("Phone number required!");
            return false;
        } else if(PhoneText.getText().length() != 10) {
            PhoneText.requestFocus();
            PhoneText.setError("Phone number not of length 10!");
            return false;
        } else {
            String phoneTemp  = PhoneText.getText().toString();
            Pattern phoneConstraint = Pattern.compile("[^0-9]");
            boolean hasChar = phoneConstraint.matcher(phoneTemp).find();
            if(hasChar == true) {
                PhoneText.requestFocus();
                PhoneText.setError("Phone contains illegal character!");
                return false;
            }
        }

        return true; // todo
    }

    /**
     *
     * When a photo is selected, return to the activity setting the photo
     * for the current user
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = this.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                File file = new File(imageUri.getPath());

                // taken from https://stackoverflow.com/questions/2407565/bitmap-byte-size-after-decoding
                // 2018-04-03
                maxSize = 65536;
                Log.i("ACTUAL SIZE", String.valueOf(selectedImage.getByteCount()));
                Integer width = 1200;
                Integer height = 1200;
                Bitmap resizedImage;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG,50,stream);
                Log.i("size",String.valueOf(stream.size()));
                while(stream.size()>maxSize){
                    width = width - 200;
                    height = height - 200;
                    stream = new ByteArrayOutputStream();
                    resizedImage = Bitmap.createScaledBitmap(selectedImage, width, height, false);
                    resizedImage.compress(Bitmap.CompressFormat.JPEG,50,stream);
                    Log.i("size",String.valueOf(stream.size()));
                }

                byte byteImage[];
                byteImage = stream.toByteArray();
                String image = Base64.encodeToString(byteImage, Base64.DEFAULT);
                user.setPhoto(new Photo(image));
                profilePicture.setImageBitmap(user.getPhoto().StringToBitmap());
                Log.i("test",user.getPhoto().toString());

            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "You haven't picked a photo", Toast.LENGTH_LONG).show();
        }
    }

}