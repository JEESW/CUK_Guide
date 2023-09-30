package com.example.ploggingcluster;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText et_login_name, et_login_password;
    String name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_login_name = (EditText) findViewById(R.id.et_login_name);
        et_login_password = (EditText) findViewById(R.id.et_login_password);

        Button btn_login_ok = (Button) findViewById(R.id.btn_login_ok);
        btn_login_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_login_name.getText().toString();
                password = et_login_password.getText().toString();
                sendRequest();
            }
        });

        ImageView backButon = (ImageView) findViewById(R.id.btn_back);
        backButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
                startActivity(intent);
            }
        });

        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    public void sendRequest(){
        String url = "http://3.36.207.209:8080/login_check?name=" + name + "&password=" + password;
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            Log.d("응답", response);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("name", name);
                            startActivityForResult(intent, 101);
                            Toast.makeText(LoginActivity.this, name + "님 로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "아이디나 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("에러", error.getMessage());
                    }
                }
        ){
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
        Log.d("응답","요청보냄");
    }


}