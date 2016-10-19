package com.example.apple.test_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.apple.test_app.data.jsondata.usercrud.UserDeleteRequest;
import com.example.apple.test_app.data.jsondata.usercrud.UserModifyRequest;
import com.example.apple.test_app.manager.networkmanager.NetworkManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HumanInfoUpdate extends AppCompatActivity {
    private static final String KEY_HUMANNAME = "KEY_HUMANNAME";
    private static final String KEY_HUMANID = "KEY_HUMANID";
    private static final String KEY_HUMANIMAGEURL = "KEY_HUMANIMAGEURL";
    private static final String KEY_HUMANDEPARTMENT = "KEY_HUMANDEPARTMENT";
    private static final String KEY_HUMANADDRESS = "KEY_HUMANADDRESS";
    private static final String KEY_HUMANTEL = "KEY_HUMANTEL";

    private static final int RC_SINGLE_IMAGE = 2;

    EditText update_name_edittext;
    EditText update_department_edittext;
    EditText update_tel_edittext;
    EditText update_address_edittext;

    ImageView update_human_imageview;
    ImageButton select_imagebutton;

    boolean is_image = false; //기본은 이미지 적용 안되었다고 가정//
    String human_name;
    String human_id;
    String human_imageurl;
    String human_department;
    String human_address;
    String human_tel;
    /**
     * 네트워크 관련 변수
     **/
    NetworkManager networkManager;
    File uploadFile = null; //이미지도 하나의 파일이기에 파일로 만든다.//
    String path = null;
    private ProgressDialog pDialog;
    private Callback requesthumanaddcallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //네트워크 자체에서의 에러상황.//
            Log.d("ERROR Message : ", e.getMessage());

            if (this != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanInfoUpdate.this);
                        alertDialog.setTitle("People Info Update")
                                .setMessage("요청에러 (네트워크 상태를 점검해주세요.)")
                                .setCancelable(false)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });

                        AlertDialog alert = alertDialog.create();
                        alert.show();
                    }
                });
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseData = response.body().string();

            Log.d("json data", responseData);

            Gson gson = new Gson();

            UserModifyRequest userModifyRequest = gson.fromJson(responseData, UserModifyRequest.class);

            boolean is_success = userModifyRequest.getResult().getIs_save();

            if (is_success == true) {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanInfoUpdate.this);
                            alertDialog.setTitle("People Info Update")
                                    .setMessage("정보수정 완료")
                                    .setCancelable(false)
                                    .setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    finish();
                                                }
                                            });

                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }
                    });
                }
            } else if (is_success == false) {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanInfoUpdate.this);
                            alertDialog.setTitle("People Info Update")
                                    .setMessage("정보수정 실패 (다시 확인해주세요)")
                                    .setCancelable(false)
                                    .setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });

                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }
                    });
                }
            }
        }
    };
    private Callback requestuserlistcallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //네트워크 자체에서의 에러상황.//
            Log.d("ERROR Message : ", e.getMessage());

            if (this != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanInfoUpdate.this);
                        alertDialog.setTitle("People Info Update")
                                .setMessage("요청에러 (네트워크 상태를 점검해주세요.)")
                                .setCancelable(false)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });

                        AlertDialog alert = alertDialog.create();
                        alert.show();
                    }
                });
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseData = response.body().string();

            Log.d("json data", responseData);

            Gson gson = new Gson();

            UserDeleteRequest userDeleteRequest = gson.fromJson(responseData, UserDeleteRequest.class);

            boolean is_success = userDeleteRequest.getResult().getIs_save();

            if (is_success == true) {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanInfoUpdate.this);
                            alertDialog.setTitle("People Info Update")
                                    .setMessage(human_name + "의 정보를 삭제했습니다.")
                                    .setCancelable(false)
                                    .setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            });

                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }
                    });
                }
            } else if (is_success == false) {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanInfoUpdate.this);
                            alertDialog.setTitle("People Info Update")
                                    .setMessage(human_name + "의 정보를 삭제에 실패했습니다. 다시 확인해주세요.")
                                    .setCancelable(false)
                                    .setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });

                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_info_update);

        update_name_edittext = (EditText) findViewById(R.id.name_edittext);
        update_department_edittext = (EditText) findViewById(R.id.departmentname_edittext);
        update_address_edittext = (EditText) findViewById(R.id.address_edittext);
        update_tel_edittext = (EditText) findViewById(R.id.tel_edittext);
        update_human_imageview = (ImageView) findViewById(R.id.humanimage_imageview);
        select_imagebutton = (ImageButton) findViewById(R.id.imageadd_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getResources().getString(R.string.title_activity_human_info_update));

        networkManager = NetworkManager.getInstance();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "수정사항을 올바르게 기입하세요.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //기존 설정된 정보를 셋팅//
        Intent intent = getIntent();

        human_name = intent.getStringExtra(KEY_HUMANNAME);
        human_address = intent.getStringExtra(KEY_HUMANADDRESS);
        human_department = intent.getStringExtra(KEY_HUMANDEPARTMENT);
        human_id = intent.getStringExtra(KEY_HUMANID);
        human_imageurl = intent.getStringExtra(KEY_HUMANIMAGEURL);
        human_tel = intent.getStringExtra(KEY_HUMANTEL);

        Log.d("json data :", human_id);

        Picasso picasso = networkManager.getPicasso(); //피카소의 자원을 불러온다.//

        picasso.load(human_imageurl)
                .transform(new CropCircleTransformation())
                .into(update_human_imageview);

        update_name_edittext.setText(human_name);
        update_tel_edittext.setText(human_tel);
        update_address_edittext.setText(human_address);
        update_department_edittext.setText(human_department);

        //back 버튼 추가//
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                finish();
            }
        });

        select_imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, RC_SINGLE_IMAGE);
            }
        });
    }

    public void Update() {
        //네트워크 작업//
        String update_human_name = update_name_edittext.getText().toString();
        String update_human_tel = update_tel_edittext.getText().toString();
        String update_human_address = update_address_edittext.getText().toString();
        String update_human_department_name = update_department_edittext.getText().toString();

        //파일 전송을 위한 설정.//
        MediaType mediaType = MediaType.parse("image/jpeg");

        networkManager = NetworkManager.getInstance();

        OkHttpClient client = networkManager.getClient();

        /** POST방식의 프로토콜 요청 설정 **/
        /** URL 설정 **/
        HttpUrl.Builder builder = new HttpUrl.Builder();

        builder.scheme("http"); //스킴정의(Http / Https)//
        builder.host(getResources().getString(R.string.server_domain)); //host정의.//
        builder.port(8080);
        builder.addPathSegment("DummyServer_Blog");
        builder.addPathSegment("UserInfo");
        builder.addPathSegment("usermodify.jsp");

        /** 파일 전송이므로 MultipartBody 설정 **/
        MultipartBody.Builder multipart_builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", update_human_name)
                .addFormDataPart("departmentname", update_human_department_name)
                .addFormDataPart("tel", update_human_tel)
                .addFormDataPart("address", update_human_address)
                .addFormDataPart("id", human_id);

        if (is_image == true) {
            //이미지가 여러개일 시 이를 여러번 수행(배열과 반복문 이용)/
            multipart_builder.addFormDataPart("file", uploadFile.getName(),
                    RequestBody.create(mediaType, uploadFile));
        } else if (is_image == false) {
            //서버에서 디폴트 이미지가 적용//
            /** Nothing **/
        }

        /** RequestBody 설정(Multipart로 설정) **/
        RequestBody body = multipart_builder.build();

        /** Request 설정 **/
        Request request = new Request.Builder()
                .url(builder.build())
                .post(body) //POST방식 적용.//
                .tag(this)
                .build();

        /** 비동기 방식(enqueue)으로 Callback 구현 **/
        client.newCall(request).enqueue(requesthumanaddcallback);
    }

    public void Delete() {
        networkManager = NetworkManager.getInstance();

        OkHttpClient client = networkManager.getClient();

        /** POST방식의 프로토콜 요청 설정 **/
        /** URL 설정 **/
        HttpUrl.Builder builder = new HttpUrl.Builder();

        builder.scheme("http"); //스킴정의(Http / Https)//
        builder.host(getResources().getString(R.string.server_domain)); //host정의.//
        builder.port(8080);
        builder.addPathSegment("DummyServer_Blog");
        builder.addPathSegment("UserInfo");
        builder.addPathSegment("userdelete.jsp");

        //Body설정//
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("id", human_id);

        /** RequestBody 설정(파일 전송 시 Multipart로 설정) **/
        RequestBody body = formBuilder.build();

        /** Request 설정 **/
        Request request = new Request.Builder()
                .url(builder.build())
                .post(body) //POST방식 적용.//
                .tag(this)
                .build();

        /** 비동기 방식(enqueue)으로 Callback 구현 **/
        client.newCall(request).enqueue(requestuserlistcallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SINGLE_IMAGE) {
            if (resultCode == RESULT_OK) {
                is_image = true;

                Uri fileUri = data.getData();
                Cursor c = getContentResolver().query(fileUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (c.moveToNext()) {
                    path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    Log.i("json control:", "path : " + path);

                    uploadFile = new File(path);

                    networkManager = NetworkManager.getInstance();

                    Picasso picasso = networkManager.getPicasso(); //피카소의 자원을 불러온다.//

                    picasso.load(uploadFile)
                            .transform(new CropCircleTransformation()) //이미지를 변형(원형자르기)//
                            .into(update_human_imageview);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.humanupdate_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == R.id.update_menubutton) {
            //네트워크 작업(등록)//
            /** 인사정보 등록 **/
            showpDialog();

            Update();
        } else if (item_id == R.id.delete_menubutton) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanInfoUpdate.this);
            alertDialog.setTitle("People Info Update")
                    .setMessage("정말로 정보를 삭제하겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("삭제",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //yes
                                    //네트워크로 데이터를 보낸다.//
                                    showpDialog();

                                    Delete();
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //no
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
