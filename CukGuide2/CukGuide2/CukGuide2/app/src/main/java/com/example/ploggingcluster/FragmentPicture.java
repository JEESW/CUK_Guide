package com.example.ploggingcluster;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FragmentPicture extends Fragment {
    private static final String TAG = "FragmentPicture";

    int _id = -1;
    MainData item;
    String name;

    Context context;
    OnTabItemSelectedListener listener;
    OnRequestListener requestListener;

    ImageView exit;
    ImageView save;

    TextView dateTextView;
    TextView locationTextView;

    ImageView set_image;
    EditText set_title;
    EditText set_coin;
    EditText set_content;

    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;

    int selectedPhotoMenu;

    Uri uri;
    File file;
    Bitmap resultPhotoBitmap;

    @Override
    public void onAttach(@NonNull Context context) {
        // 프래그먼트가 올라갔을때
        super.onAttach(context);
        this.context = context;

        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }

        if (context instanceof OnRequestListener) {
            requestListener = (OnRequestListener) context;
        }
        // 이 프래그먼트에서 액비티티에 정의된 onRequest 메서드를 호출할 수 있도록 해야함
        // 그래서 전달받은 액티비티 객체가 OnRequestListener 인터페이스를 구현하고 있는 경우
        // requestListener 변수에 할당하도록 합니다.
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (context != null) {
            context = null;
            listener = null;
            requestListener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_picture, container, false);
        initUI(rootView);

        if (requestListener != null) {
            requestListener.onRequest(("getCurrentLocation"));
        } // OnRequestListener 객체의 onRequest 메서드를 호출
        // 이렇게하면 입력 화면이 보일 때마다 현재 위치를 확인할 수 있도록 MainActivity 클래스의 OnRequest 메서드가 호출됨

        return rootView;
    }

    private void initUI(ViewGroup rootView) {

        exit = rootView.findViewById(R.id.btn_exit);
        save = rootView.findViewById(R.id.btn_save);

        set_image = rootView.findViewById(R.id.set_image);
        set_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        set_title = rootView.findViewById(R.id.set_title);
        set_coin = rootView.findViewById(R.id.set_coin);
        set_content = rootView.findViewById(R.id.set_content);

        dateTextView = rootView.findViewById(R.id.dateTextView);
        locationTextView = rootView.findViewById(R.id.locationTextView);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onTabSelected(0);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
                if (listener != null) {
                    Toast.makeText(getContext(), "새로운 리퀘스트가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                    listener.onTabSelected(0);
                    set_title.setText("");
                    set_coin.setText("");
                    set_content.setText("");
                }
            }
        });
    }

    public void setAddress(String data) {
        locationTextView.setText(data);
    }
    public void setName(String name){
        this.name = name;
    }

    public void setDateString(String dateString) {
        dateTextView.setText(dateString);
    }

    public void setItem(MainData item) {
        this.item = item;
    }


    public void showDialog() {
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(context);

        builder.setTitle("사진 메뉴 선택");
        builder.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                selectedPhotoMenu = whichButton;
            }
        });
        builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (selectedPhotoMenu == 0) {
                    showPhotoCaptureActivity(); // 사진찍기
                } else if (selectedPhotoMenu == 1) {
                    showPhotoSelectionActivity(); // 앨범선택
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showPhotoCaptureActivity() { // 사진찍기 선택
        try {
            file = createFile();
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this.context, BuildConfig.APPLICATION_ID, file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 단말에서 사진을 찍었을 때 어떤 파일로 저장할 지 지정
        startActivityForResult(intent, AppConstants.REQ_PHOTO_CAPTURE);
    }

    private File createFile() { // file 만들기
        String filename = createFilename();
        File outFile = new File(context.getFilesDir(), filename);
        Log.d("Main", "File path : " + outFile.getAbsolutePath());

        return outFile;
    }

    public void showPhotoSelectionActivity() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, AppConstants.REQ_PHOTO_SELECTION);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (intent != null) {
            switch (requestCode) {
                case AppConstants.REQ_PHOTO_CAPTURE:  // 사진 찍는 경우
                    Log.d(TAG, "onActivityResult() for REQ_PHOTO_CAPTURE.");

                    Log.d(TAG, "resultCode : " + resultCode);

//                    setPicture(file.getAbsolutePath(), 8);
                    resultPhotoBitmap = decodeSampledBitmapFromResource(file, set_image.getWidth(), set_image.getHeight());
                    set_image.setImageBitmap(resultPhotoBitmap);

                    break;

                case AppConstants.REQ_PHOTO_SELECTION:  // 사진을 앨범에서 선택하는 경우
                    Log.d(TAG, "onActivityResult() for REQ_PHOTO_SELECTION.");

                    Uri fileUri = intent.getData();

                    ContentResolver resolver = context.getContentResolver();

                    try {
                        InputStream instream = resolver.openInputStream(fileUri);
                        resultPhotoBitmap = BitmapFactory.decodeStream(instream);
                        set_image.setImageBitmap(resultPhotoBitmap);

                        instream.close();

                        isPhotoCaptured = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

            }
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(File res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(res.getAbsolutePath(), options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height;
            final int halfWidth = width;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private String createFilename() { // 파일 이름 만들기
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }

    private String savePicture() {
        if (resultPhotoBitmap == null) {
            AppConstants.println("No picture to be saved.");
            return "";
        }

        File photoFolder = new File(AppConstants.FOLDER_PHOTO);

        if (!photoFolder.isDirectory()) {
            Log.d(TAG, "creating photo folder : " + photoFolder);
            photoFolder.mkdirs();
        }

        String photoFilename = createFilename();
        String picturePath = photoFolder + File.separator + photoFilename;

        try {
            FileOutputStream outstream = new FileOutputStream(picturePath);
            resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picturePath;
    }

    public void addItem() {
        String contents = set_content.getText().toString();
        String picturePath = savePicture();
        String title = set_title.getText().toString();
        Editable coin = set_coin.getText();
        String address = locationTextView.getText().toString();
        String date = String.valueOf(new Date());
        String url = "http://3.36.207.209:8080/item/add?name=" + name + "&title=" + title + "&address=" +  address + "&content=" + contents + "&coin=" + coin + "&picture=" + picturePath + "&date=" + date;
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("addItem", response);
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

    // room id = 1
    // db - msg
    // 1 - db

}