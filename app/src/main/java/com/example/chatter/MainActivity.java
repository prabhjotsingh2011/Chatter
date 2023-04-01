package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatter.Adapters.FragmentAdapter;
import com.example.chatter.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
//                Toast.makeText(this, "Settings is clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
            case R.id.groupChat:
//                Toast.makeText(this, "Group chat is clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,GroupChatActivity.class));
                break;
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,SignInActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}