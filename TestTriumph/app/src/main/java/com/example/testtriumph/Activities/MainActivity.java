package com.example.testtriumph.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.testtriumph.BookmarksActivity;
import com.example.testtriumph.DbQuery;
import com.example.testtriumph.Fragments.AccountFragment;
import com.example.testtriumph.Fragments.CategoryFragment;
import com.example.testtriumph.Fragments.LeaderBoardFragment;
import com.example.testtriumph.Fragments.MyTestFragment;
import com.example.testtriumph.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private TextView drawerProfileName,drawerProfileText;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.navigation_home) {
                        setFragment(new CategoryFragment());
                        return true;
                    } else if (itemId == R.id.navigation_leaderboard) {
                        setFragment(new LeaderBoardFragment());
                        return true;
                    } else if (itemId == R.id.navigation_account) {
                        setFragment(new AccountFragment());
                        return true;
                    } else if (itemId == R.id.navigation_mytest) {
                    setFragment(new MyTestFragment());
                    return true;
                }
                    return false;

                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      //  getSupportActionBar().setDisplayShowTitleEnabled(true);
       // getSupportActionBar().setTitle("Categories");

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Hiển thị cho Teacher
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("USERS").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("ROLE");
                            if ("Teacher".equals(role)) {
                                // Show the My Test menu item in both NavigationView and BottomNavigationView
                                navigationView.getMenu().findItem(R.id.nav_mytest).setVisible(true);
                                bottomNavigationView.getMenu().findItem(R.id.navigation_mytest).setVisible(true);
                            } else {
                                // Hide the My Test menu item in both NavigationView and BottomNavigationView
                                navigationView.getMenu().findItem(R.id.nav_mytest).setVisible(false);
                                bottomNavigationView.getMenu().findItem(R.id.navigation_mytest).setVisible(false);
                            }
                        } else {
                            Toast.makeText(MainActivity.this,  "No such document",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this,  "get failed with ",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        ////////////////////////
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        drawerProfileName=navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_name);
        drawerProfileText=navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_text_img);
        String name= DbQuery.myProfile.getName();
        drawerProfileName.setText(name);
        drawerProfileText.setText(name.toUpperCase().substring(0,1));
        setFragment(new CategoryFragment());
    }

    public void onBackPressed(){
        DrawerLayout drawer=(DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item){

        int id=item.getItemId();

        if(id==R.id.nav_home){
            setFragment(new CategoryFragment());
        }else if (id == R.id.nav_account) {
            setFragment(new AccountFragment());
        } else if (id == R.id.nav_leaderboard) {
            setFragment(new LeaderBoardFragment());
        }else if (id == R.id.nav_mytest) {
            setFragment(new MyTestFragment());
        }else if(id==R.id.nav_bookmark){
            Intent intent=new Intent(MainActivity.this, BookmarksActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(main_frame.getId(),fragment);
        transaction.commit();
    }


}