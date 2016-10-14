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
import android.widget.TextView;

import com.example.apple.test_app.data.jsondata.usercrud.UserAddRequest;
import com.example.apple.test_app.manager.networkmanager.NetworkManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HumanAddActivity extends AppCompatActivity {
    private final static String KEY_DEPARTMENTNAME = "KEY_DEPARTMENTNAME";
    private static final int RC_SINGLE_IMAGE = 2;

    TextView department_name_textview;
    EditText name_edittext;
    EditText job_edittext;
    EditText tel_edittext;
    EditText emailaddress_edittext;
    EditText address_edittext;
    EditText age_edittext;
    EditText etcinfo_edittext;
    EditText introduction_edittext;
    ImageView humanimage_imageview;
    ImageButton getImage_button;

    String department_name = "";

    boolean is_image = false; //기본은 이미지 적용 안되었다고 가정//
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

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanAddActivity.this);
                        alertDialog.setTitle("People Enroll")
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

            UserAddRequest userAddRequest = gson.fromJson(responseData, UserAddRequest.class);

            boolean is_save = userAddRequest.getResult().getIs_save();

            if (is_save == true) //등록에 성공//
            {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanAddActivity.this);
                            alertDialog.setTitle("People Enroll")
                                    .setMessage("사원등록 완료")
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
            } else if (is_save == false) //등록에 실패(중복등록)//
            {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanAddActivity.this);
                            alertDialog.setTitle("People Enroll")
                                    .setMessage("사원등록 실패(이미 등록된 사원입니다.)")
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
        setContentView(R.layout.activity_human_add);

        setTitle(getResources().getString(R.string.title_activity_human_add));

        department_name_textview = (TextView) findViewById(R.id.department_name_textview);
        name_edittext = (EditText) findViewById(R.id.name_edittext);
        job_edittext = (EditText) findViewById(R.id.job_edittext);
        tel_edittext = (EditText) findViewById(R.id.tel_edittext);
        emailaddress_edittext = (EditText) findViewById(R.id.emailaddress_edittext);
        address_edittext = (EditText) findViewById(R.id.address_edittext);
        age_edittext = (EditText) findViewById(R.id.age_edittext);
        etcinfo_edittext = (EditText) findViewById(R.id.etcinfo_edittext);
        introduction_edittext = (EditText) findViewById(R.id.introduction_edittext);
        humanimage_imageview = (ImageView) findViewById(R.id.humanimage_imageview);
        getImage_button = (ImageButton) findViewById(R.id.imageadd_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        //back 버튼 추가//
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //등록하지 않고 종료//
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "사진은 선택입니다. 사진을 선택하지 않을 시 기본 사진이 적용됩니다. 한번 등록한 사진은 취소할 수" +
                        "없으니 신중히 선택하세요.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();

        department_name = intent.getStringExtra(KEY_DEPARTMENTNAME);

        department_name_textview.setText(department_name.toString() + " 인사등록");

        /** 이미지 등록 **/
        getImage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, RC_SINGLE_IMAGE);
            }
        });
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
                            .into(humanimage_imageview);
                }
            }
        }
    }

    public void Enroll() {
        //이름을 가지고 예외처리//
        if (name_edittext.getText().toString().equals("")) {
            hidepDialog();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HumanAddActivity.this);
            alertDialog.setTitle("People Search")
                    .setMessage("이름을 입력하지 않았습니다. (필수입력)")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

            AlertDialog alert = alertDialog.create();
            alert.show();
        } else {
            //등록할 값을 얻어온다.//
            String enroll_humanname = name_edittext.getText().toString();
            String enroll_humanjob = job_edittext.getText().toString();
            String enroll_humantel = tel_edittext.getText().toString();
            String enroll_humanemailaddress = emailaddress_edittext.getText().toString();
            String enroll_humanaddress = address_edittext.getText().toString();
            String enroll_humanage = age_edittext.getText().toString();
            String enroll_humanetcinfo = etcinfo_edittext.getText().toString();
            String enroll_humanintroduction = introduction_edittext.getText().toString();

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
            builder.addPathSegment("useradd.jsp");

            /** 파일 전송이므로 MultipartBody 설정 **/
            MultipartBody.Builder multipart_builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("human_name", enroll_humanname)
                    .addFormDataPart("human_etcinfo", enroll_humanetcinfo)
                    .addFormDataPart("human_departmentname", department_name)
                    .addFormDataPart("human_introduction", enroll_humanintroduction)
                    .addFormDataPart("human_age", enroll_humanage)
                    .addFormDataPart("human_tel", enroll_humantel)
                    .addFormDataPart("human_job", enroll_humanjob)
                    .addFormDataPart("human_address", enroll_humanaddress)
                    .addFormDataPart("human_emailaddress", enroll_humanemailaddress);

            //파일선택 유무에 따른 값 지정 판단//
            if (is_image == true) //이미지를 선택한 경우//
            {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.humanadd_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == R.id.enroll_menubutton) {
            //네트워크 작업(등록)//
            /** 인사정보 등록 **/

            showpDialog();

            Enroll();
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
