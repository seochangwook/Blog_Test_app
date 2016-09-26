package com.example.apple.test_app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.apple.test_app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Option2Fragment extends Fragment {
    Button click_button;

    public Option2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_option2, container, false);

        click_button = (Button) view.findViewById(R.id.click_button);

        click_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "버튼 2 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
