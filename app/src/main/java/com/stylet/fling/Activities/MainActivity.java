package com.stylet.fling.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.stylet.fling.R;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();



        FloatingActionButton fab2 = findViewById(R.id.add_status_btn);
        fab2.setVisibility(View.GONE);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newStatus = new Intent(MainActivity.this, NewStatusActivity.class);
                startActivity(newStatus);

            }
        });

        enablePersistence();

        updateNavHeader();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_flings,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();*/
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        if (user.getDisplayName() == null || user.getPhotoUrl() == null){
            Intent toSetup = new Intent(MainActivity.this, SetUpActivity.class);
            startActivity(toSetup);
            finish();
        }






    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                break;
            case R.id.action_signout:

                    FirebaseAuth.getInstance().signOut();
                    Intent login = new Intent(MainActivity.this, LoginActivity.class);
                    //login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(login);
                    finish();

                break;
        }

        return false;
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavHeader(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView navUserName = header.findViewById(R.id.userName);
        TextView navUserEmail = header.findViewById(R.id.userEmail);
        ImageView navUserImage = header.findViewById(R.id.userImageView);
        LinearLayout user_info = header.findViewById(R.id.userinfo_header);

        user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setup = new Intent(MainActivity.this, SetUpActivity.class);
                startActivity(setup);
            }
        });

        navUserName.setText(currentUser.getDisplayName());
        navUserEmail.setText(currentUser.getEmail());

        Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(navUserImage);
    }

    private void enablePersistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
