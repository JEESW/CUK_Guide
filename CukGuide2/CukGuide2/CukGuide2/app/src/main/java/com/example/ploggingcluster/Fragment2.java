package com.example.ploggingcluster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class Fragment2 extends Fragment {
    TextView title, content, address, time, coin;
    ImageView imageView;
    MainData item;
    Bitmap resultPhotoBitmap;

    Context context;
    OnTabItemSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        // 프래그먼트가 액티비티 위에 올라갈 때
        super.onAttach(context);

        this.context = context;

        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        // 프래그먼트가 액티비티에서 내려올 때 호출
        super.onDetach();
        if(context != null) {
            context = null;
            listener = null;
        }
    }


    public void setItem(MainData item){
        this.item = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_2, container, false);

        apply(rootView);
        return rootView;
    }

    public void apply(ViewGroup rootView) {
        title = rootView.findViewById(R.id.title);
        content = rootView.findViewById(R.id.content);
        address = rootView.findViewById(R.id.address);
        time = rootView.findViewById(R.id.time);
        imageView = rootView.findViewById(R.id.image);
        coin = rootView.findViewById(R.id.setCoin);

        Button btn_chat = (Button) rootView.findViewById(R.id.btn_chat);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅 구현
                chatting();
            }
        });

        if (item != null) {
            title.setText(item.getTitle());
            content.setText(item.getContent());
            address.setText(item.getAddress());
            time.setText(item.getCreateDateStr());
            coin.setText(String.valueOf(item.getCoin()));
            String picturePath = item.getPicture();

            if (picturePath == null || picturePath.equals("")) {
                imageView.setImageResource(R.drawable.noimagefound);
            } else {
                setPicture(item.getPicture(), 3);
            }

        }
    }

    public void setPicture(String picturePath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        resultPhotoBitmap = BitmapFactory.decodeFile(picturePath, options);

        imageView.setImageBitmap(resultPhotoBitmap);

    }

    public void chatting() {
        if (listener != null) {
            listener.onTabSelected(5);
        }
    }

}