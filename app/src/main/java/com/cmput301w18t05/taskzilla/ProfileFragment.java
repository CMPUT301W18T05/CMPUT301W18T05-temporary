package com.cmput301w18t05.taskzilla;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView nameField;
    private TextView emailField;
    private TextView phoneField;
    private TextView numRequestsField;
    private TextView numTasksDoneField;
    private TextView providerRatingField;
    private TextView requesterRatingField;

    private String name;
    private String email;
    private String phone;
    private String numRequests;
    private String numTasksDone;
    private Button logOut;
    private User user;
    private ProfileController profileController;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final RelativeLayout mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_profile,
                container, false);

        ImageButton mButton = (ImageButton) mRelativeLayout.findViewById(R.id.EditButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        return mRelativeLayout;
    }


    /**
     * Set fields in the fragment_profile.xml
     * using the currentUser object which stores
     * all necessary information of the current user that
     * is logged in
     *
     * @author Micheal-Nguyen
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nameField = view.findViewById(R.id.NameField);
        emailField = view.findViewById(R.id.EmailField);
        phoneField = view.findViewById(R.id.PhoneField);
        providerRatingField = view.findViewById(R.id.ProviderRatingField);
        requesterRatingField = view.findViewById(R.id.RequesterRatingField);
        numRequestsField = view.findViewById(R.id.NumRequestsField);
        numTasksDoneField = view.findViewById(R.id.NumTasksDoneField);
        logOut = view.findViewById(R.id.LogOutButton);
        user = currentUser.getInstance();

        name = user.getName();
        email = user.getEmail().toString();
        phone = user.getPhone().toString();
        numRequests = "69";
        numTasksDone = "69";

        nameField.setText(name);
        emailField.setText(email);
        phoneField.setText(phone);
        numRequestsField.setText(numRequests);
        numTasksDoneField.setText(numTasksDone);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutClicked();
            }
        });

    }
    /**
     * Switch to EditProfile Activity
     * Send users information to the activity
     */
    public void editProfile(){

        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        intent.putExtra("name","Name goes here" );
        startActivity(intent);
    }

    public void logOutClicked(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        //Delete User from gson
        startActivity(intent);
    }
    public void notifyChange() {
        // update fields
        providerRatingField.setText(user.getProviderRating().toString());
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProfileController(ProfileController profileController) {
        this.profileController = profileController;
    }

    public TextView getProviderRatingField() {
        return providerRatingField;
    }
}
