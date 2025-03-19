package com.saksham.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.view.View;
import android.widget.Toast;

public class PaymentActivity extends Activity {
    private WebView webView;
    private String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get amount from intent
        amount = getIntent().getStringExtra("amount");
        if (amount == null) amount = "0";

        // Create WebView
        webView = new WebView(this);
        
        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        
        // Set custom WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Show the WebView once the page is loaded
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Handle error
                Toast.makeText(PaymentActivity.this, "Error loading payment page. Please try again.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        
        // Add JavaScript interface
        webView.addJavascriptInterface(new WebAppInterface(), "android");
        
        // Set WebView as the activity's content
        setContentView(webView);
        
        // Initially hide the WebView
        webView.setVisibility(View.INVISIBLE);
        
        // Load payment form
        webView.loadUrl("file:///android_asset/payment.html?amount=" + amount);
    }

    private class WebAppInterface {
        @JavascriptInterface
        public void onPaymentSuccess() {
            runOnUiThread(() -> {
                new AlertDialog.Builder(PaymentActivity.this)
                    .setTitle("Payment Successful")
                    .setMessage("Your payment has been processed successfully!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        setResult(Activity.RESULT_OK);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
            });
        }

        @JavascriptInterface
        public void onPaymentCancel() {
            runOnUiThread(() -> {
                setResult(Activity.RESULT_CANCELED);
                finish();
            });
        }
    }
} 