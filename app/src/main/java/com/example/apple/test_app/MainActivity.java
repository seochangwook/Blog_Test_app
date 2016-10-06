package com.example.apple.test_app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.apple.test_app.fragment.HumanResourceFragment;
import com.example.apple.test_app.fragment.OverseasSalesTeamFragment;
import com.example.apple.test_app.fragment.SearchInfoFragment;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_VALUE = "KEY_VALUE";
    private static int flag = 0;
    private static int flag_fragment = 3;

    Button human_list_button;
    Button overseas_sales_team_list_button;
    EditText toolbar_edittext;
    ImageButton app_help_button;

    DrawerLayout drawerLayout;

    Toolbar toolbar; //툴바//

    //검색결과//
    String search_value;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main_activity);

        setTitle("");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        human_list_button = (Button) findViewById(R.id.human_list_button);
        overseas_sales_team_list_button = (Button) findViewById(R.id.overseas_sales_team_list_button);
        toolbar_edittext = (EditText) findViewById(R.id.toolbar_edittext);
        app_help_button = (ImageButton) findViewById(R.id.help_menu_button);

        /** DrawerLayout 설정 **/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar); //툴바 생성.(액션바 -> 툴바)//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //HomeAsUp버튼 설정//
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu);

        human_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 프래그먼트로 변경(replace)해준다.//
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, new HumanResourceFragment()
                ).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

                app_help_button.setVisibility(View.GONE);

                flag_fragment = 3;
            }
        });

        overseas_sales_team_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 프래그먼트로 변경(replace)해준다.//
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, new OverseasSalesTeamFragment()
                ).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

                app_help_button.setVisibility(View.GONE);

                flag_fragment = 4;
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

        /** Toolbar의 EditText 이벤트 처리 **/
        toolbar_edittext.setOnKeyListener(new EditMessageOnKeyListener());

        toolbar_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "입력 후 엔터키를 누르세요", Toast.LENGTH_SHORT).show();
            }
        });

        /** Menu help button **/
        app_help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "도움말 버튼", Toast.LENGTH_SHORT).show();
            }
        });

        /** BackStack 관련 이벤트 처리 **/
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                //BackStack 콜백메소드//
                Log.i("data: ", "back stack changed ");

                if (flag == 1) //검색 프래그먼트일 경우//
                {
                    human_list_button.setVisibility(View.GONE);
                    overseas_sales_team_list_button.setVisibility(View.GONE);
                    toolbar_edittext.setVisibility(View.GONE);
                    app_help_button.setVisibility(View.GONE);

                    flag = 0; //기존 프래그먼트의 플래그로 돌아간다.//
                } else if (flag == 0) //원래의 프래그먼트로 백스택 하였을 경우//
                {
                    //기존 프래그먼트로 변경을 하는데 있어서 툴바에 대한 구분을 해주기 위함//
                    if (flag_fragment == 4) {
                        human_list_button.setVisibility(View.VISIBLE);
                        overseas_sales_team_list_button.setVisibility(View.VISIBLE);
                        toolbar_edittext.setVisibility(View.VISIBLE);
                    } else if (flag_fragment == 3) {
                        human_list_button.setVisibility(View.VISIBLE);
                        overseas_sales_team_list_button.setVisibility(View.VISIBLE);
                        toolbar_edittext.setVisibility(View.VISIBLE);
                        app_help_button.setVisibility(View.VISIBLE);
                    }
                }
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

    /**
     * EditText Key이벤트 등록
     **/
    class EditMessageOnKeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //엔터키 리스너//
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(toolbar_edittext.getWindowToken(), 0);    //hide keyboard

                search_value = toolbar_edittext.getText().toString();
                toolbar_edittext.setText("");

                //검색 프래그먼트로 이동 및 데이터 전달(프래그먼트는 Bundle을 이용)//
                SearchInfoFragment searchInfoFragment = new SearchInfoFragment();

                Bundle args = new Bundle();
                args.putString(KEY_VALUE, search_value);
                searchInfoFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, searchInfoFragment
                ).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null) //백스택 등록//
                        .commit();

                flag = 1; //백스택을 필요로 하는 곳에서 특정 플래그를 준다.//

                return true;
            }

            return false;
        }
    }
}
