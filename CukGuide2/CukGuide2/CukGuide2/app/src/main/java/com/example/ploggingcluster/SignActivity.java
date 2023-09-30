package com.example.ploggingcluster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class SignActivity extends AppCompatActivity {

    EditText et_name, et_password;
    String name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        et_name = (EditText) findViewById(R.id.et_name);
        et_password = (EditText) findViewById(R.id.et_password);
        Button btn_sign_ok = (Button) findViewById(R.id.btn_sign_ok);
        btn_sign_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인을 눌렀을 때
                signUp();
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

    public void signUp() {
        // 회원가입
        name = et_name.getText().toString();
        password = et_password.getText().toString();
        if (name != "") {
            String url = "http://3.36.207.209:8080/login?name=" + name + "&password=" + password;
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("signUp", "응답 => " + response);
                            Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
                            startActivityForResult(intent, 101);
                            Toast.makeText(SignActivity.this, "가입 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Log.d("signUp", error.getMessage());
                            Toast.makeText(SignActivity.this, "이미 가입된 아이디입니다.", Toast.LENGTH_SHORT).show();
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
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    return params;
                }
            };
            request.setShouldCache(false);
            AppHelper.requestQueue.add(request);
            Log.d("signUp", "요청 보냄");
        }
    }
}