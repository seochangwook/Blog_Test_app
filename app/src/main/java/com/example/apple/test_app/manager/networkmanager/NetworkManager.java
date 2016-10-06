package com.example.apple.test_app.manager.networkmanager;

import android.content.Context;

import com.example.apple.test_app.manager.MyApplication;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by apple on 2016. 10. 5..
 */
public class NetworkManager {
    /**
     * 네트워크 자원
     **/
    //객체를 private로 선언하여 SingleTone Pattern적용//
    private static NetworkManager instance;

    OkHttpClient client;
    Picasso picasso;

    /**
     * 싱글톤 패턴을 적용해야지 여러 네트워크부분에서 같은 쿠키, client값을 공유할 수 있다
     **/
    private NetworkManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Context context = MyApplication.getContext();

        //영속적 쿠키 설정//
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        builder.cookieJar(cookieJar);

        //307에러를 해결하기 위한 설정.//
        builder.followRedirects(true);
        builder.addInterceptor(new RedirectInterceptor());

        //캐시파일을 생성//
        File cacheDir = new File(context.getCacheDir(), "network");

        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        //쿠키의 세션 만료시간 설정//
        Cache cache = new Cache(cacheDir, 10 * 1024 * 1024);
        builder.cache(cache);

        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);

        //disableCertificateValidation(context, builder); //https를 위한 인증서 등록//

        client = builder.build(); //클라이언트를 만든다.//

        //피카소를 okhttp3에 맞추어서 다시 다운로더 작성(이미지 처리 HTTPS issue해결)//
        picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();
    }

    /*static void disableCertificateValidation(Context context, OkHttpClient.Builder builder) {
        //인증서를 등록//
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.site); //인증서 정보를 읽어온다.(raw에 저장)//

            Certificate ca;

            try {
                ca = cf.generateCertificate(caInput); //인증서를 만든다.//
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm(); //기본 알고리즘 적용//
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore); //키스토어 초기값//

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, tmf.getTrustManagers(), null);

            HostnameVerifier hv = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };

            sc.init(null, tmf.getTrustManagers(), null);
            builder.sslSocketFactory(sc.getSocketFactory());
            builder.hostnameVerifier(hv);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }*/

    //싱글톤 적용//
    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }

        return instance;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Picasso getPicasso() {
        return picasso;
    }
}
