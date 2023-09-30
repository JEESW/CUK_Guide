package com.example.ploggingcluster;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fragment1 extends Fragment {

    RecyclerView recyclerView;
    MainAdapter adapter;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        프래그먼트가 자신의 인터페이스를 처음 그리기 위해 호출합니다. View를 반환해야 합니다.
//        이 메서드는 프래그먼트의 레이아웃 루트이기 때문에 UI를 제공하지 않는 경우에는 null을 반환하면 됩니다.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_1, container, false);
        initUI(rootView);

//        loadNoteListData();
        sendRequest();

        return rootView;
    }

    private void initUI(ViewGroup rootView){

        Button btn_add = (Button) rootView.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onTabSelected(3);
                }
            }
        });

        Button switchMultiButton = (Button) rootView.findViewById(R.id.switchButton);
        switchMultiButton.setSelected(false);
        switchMultiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onTabSelected(4);
                }
            }

        });

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MainAdapter();

        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void OnItemClick(MainAdapter.ViewHolder holder, View view, int position) {

                MainData item = adapter.getItem(position);

                if (listener != null) {
                    listener.showFragmentItem(item);
                }

            }
        });

    }

    public void sendRequest() {
        String url = "http://3.36.207.209:8080/item/all";
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override //response를 UTF8로 변경해주는 소스코드
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8String = new String(response.data, "UTF-8");
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    // log error
                    return Response.error(new ParseError(e));
                } catch (Exception e) {
                    // log error
                    return Response.error(new ParseError(e));
                }
            }
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
        Log.d("응답", "요청보냄");
    }

    public void processResponse(String response){
        ArrayList<MainData> items = new ArrayList<MainData>();
        Log.d("json", response);
        try {
            JSONArray jsonItem = new JSONArray(response);
            for (int i = jsonItem.length() - 1; i >= 0 ; i--) {
                Gson gson = new Gson();
                MainData mainData = gson.fromJson(jsonItem.get(i).toString(), MainData.class);
                items.add(mainData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
//            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        recyclerView.setAdapter(adapter);
    }
}