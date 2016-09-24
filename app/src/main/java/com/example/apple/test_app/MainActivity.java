package com.example.apple.test_app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.apple.test_app.fragment.HumanResourceFragment;

public class MainActivity extends AppCompatActivity {

    Button human_list_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        human_list_button = (Button) findViewById(R.id.human_list_button);

        human_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 프래그먼트로 변경(replace)해준다.//
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, new HumanResourceFragment()
                ).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });
    }
}
