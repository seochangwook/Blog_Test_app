package com.example.apple.test_app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.apple.test_app.fragment.HumanResourceFragment;

public class MainActivity extends AppCompatActivity {

    Button human_list_button;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main_activity);

        human_list_button = (Button) findViewById(R.id.human_list_button);

        /** DrawerLayout 설정 **/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //HomeAsUp버튼 설정//
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu);

        human_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 프래그먼트로 변경(replace)해준다.//
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, new HumanResourceFragment()
                ).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        /** DrawerLayout Listener **/
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_x_cancel);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        /** HomeAsUp버튼 이벤트 처리 **/
        if (item_id == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu);

                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_x_cancel);

                drawerLayout.openDrawer(GravityCompat.START);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //다른 아이템이 클릭되었을 시 닫아준다.//
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}
