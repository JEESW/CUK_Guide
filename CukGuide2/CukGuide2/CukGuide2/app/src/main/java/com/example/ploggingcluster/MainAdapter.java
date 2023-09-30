package com.example.ploggingcluster;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> implements OnNoteItemClickListener{

    private ArrayList<MainData> items = new ArrayList<MainData>();
    OnNoteItemClickListener listener;

    int layoutType = 0;



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // listView가 처음으로 생성될 때 생명주기
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_plogging, parent, false);

        return new ViewHolder(itemView, this, layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainData item = items.get(position);
        holder.setItem(item);
        holder.setLayoutType(layoutType);
    }

    @Override
    public int getItemCount() {
        return null != items ? items.size() : 0;
    }

    public void addItem(MainData item) {
        items.add(item);
    }

    public void setItems(ArrayList<MainData> items) {
        this.items = items;
    }

    public MainData getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(OnNoteItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void OnItemClick(ViewHolder holder, View view, int position) {
        if(listener != null) {
            listener.OnItemClick(holder, view, position);
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_profile;
        TextView tv_title;
        TextView tv_location;
        TextView tv_coin;
        TextView tv_date;


        public ViewHolder(@NonNull View itemView, final OnNoteItemClickListener listener, int layoutType) {
            super(itemView);

            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_location = itemView.findViewById(R.id.tv_location);
            tv_coin = itemView.findViewById(R.id.tv_coin);
            tv_date = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { // 리스트 클릭!
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.OnItemClick(ViewHolder.this, view, position);
                    }
                }
            });
            setLayoutType(layoutType);
        }

        public void setItem(MainData item) {
            String imagePath = item.getPicture();
            if(imagePath != null && !imagePath.equals("")){

            }
            tv_title.setText(item.getTitle());
            tv_location.setText(item.getAddress());
            int coin = item.getCoin();
            tv_coin.setText(String.valueOf(coin));
            tv_date.setText(item.getCreateDateStr());
            String picturePath = item.getPicture();
            iv_profile.setImageURI(Uri.parse("file://" + picturePath));

        }

        public void setLayoutType(int layoutType) {
            if (layoutType == 0){

            } else if (layoutType == 1){

            }
        }

    }

}
