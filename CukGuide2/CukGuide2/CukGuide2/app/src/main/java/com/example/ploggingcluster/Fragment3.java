package com.example.ploggingcluster;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.channguyen.rsv.RangeSliderView;

public class Fragment3 extends Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView =  (ViewGroup) inflater.inflate(R.layout.fragment_3, container, false);
        return rootView;
    }


}