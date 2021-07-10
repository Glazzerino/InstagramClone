package com.codepath.instagramclone;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.instagramclone.fragments.ComposeFragment;
import com.codepath.instagramclone.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomMenu;
    FragmentManager fragmentManager;
    Fragment fragment;
    ImageButton btnLogout;
    ImageView ivTitle;
    Context context;
    public static final int POST_ACTIVITY_CODE = 1337;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        btnLogout = findViewById(R.id.btnLogout);
        ivTitle = findViewById(R.id.ivTitle);
        
        bottomMenu = findViewById(R.id.bottomMenu);
        // fragment manager
        fragmentManager = getSupportFragmentManager();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                finish();
            }
        });

        fragment = HomeFragment.newInstance(context);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_home:
                        fragment = HomeFragment.newInstance(context);
                        break;

                    case R.id.item_create:
                        fragment = ComposeFragment.newInstance(context);
                        break;

                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }

        });
    }
}