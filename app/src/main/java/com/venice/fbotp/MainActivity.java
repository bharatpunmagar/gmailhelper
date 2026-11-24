package com.venice.fbotp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity {

    private static final String UA_DESKTOP =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final String UA_MOBILE =
        "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";

    private WebView web;
    private TextView status;
    private boolean desktop = true;
    private String inject = "";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_main);

        web = findViewById(R.id.web);
        status = findViewById(R.id.status);

        try {
            inject = readAsset("gmail.js");
        } catch (Exception e) {
            inject = "";
        }

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportMultipleWindows(false);
        s.setBuiltInZoomControls(true);
        s.setDisplayZoomControls(false);
        s.setUserAgentString(UA_DESKTOP);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView v, String url) {
                if (inject.length() > 0) v.evaluateJavascript(inject, null);
            }
        });
        web.setWebChromeClient(new WebChromeClient());

        findViewById(R.id.ua).setOnClickListener(b -> {
            desktop = !desktop;
            web.getSettings().setUserAgentString(desktop ? UA_DESKTOP : UA_MOBILE);
            status.setText(desktop ? "Desktop mode ON" : "Mobile mode");
            web.reload();
        });

        findViewById(R.id.reload).setOnClickListener(b -> web.reload());

        findViewById(R.id.tryAnother).setOnClickListener(b ->
            web.evaluateJavascript(
                "window.__g ? __g.tryAnother() : 'script chhaina'",
                value -> status.setText("नतिजा: " + value)));

        web.loadUrl("https://accounts.google.com/signin/recovery");
    }

    private String readAsset(String name) throws Exception {
        InputStream in = getAssets().open(name);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        in.close();
        return out.toString("UTF-8");
    }

    @Override
    public boolean onKeyDown(int code, KeyEvent e) {
        if (code == KeyEvent.KEYCODE_BACK && web != null && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(code, e);
    }
}
