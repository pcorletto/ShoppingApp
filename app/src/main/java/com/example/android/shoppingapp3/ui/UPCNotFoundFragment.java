package com.example.android.shoppingapp3.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.android.shoppingapp3.R;

/**
 * Created by hernandez on 12/28/2016.
 */
public class UPCNotFoundFragment extends Fragment {

    private WebView mWebView;

    private String mUPC, mURL;

    public UPCNotFoundFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.upc_not_found_fragment, container, false);

        mUPC = ScanActivity.mUPC;
        mURL = "http://www.digit-eyes.com/cgi-bin/digiteyes.cgi?upcCode=";
        mURL += mUPC;
        mURL += "&action=lookupUpc&go=Go%21";
        mWebView = (WebView) view.findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mURL);

        return view;
    }
}
