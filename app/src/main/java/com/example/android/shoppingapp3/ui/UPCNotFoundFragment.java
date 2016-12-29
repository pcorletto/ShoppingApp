package com.example.android.shoppingapp3.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

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
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                // Extract the product name from the string
                int i = 0;
                String productName = "";
                String unWantedString = "";
                while(title.charAt(i) != ':'){

                    unWantedString += title.charAt(i);
                    i++;
                }

                for(int j = i+1; j<title.length(); j++){

                    productName += title.charAt(j);
                }

                ScanActivity.productNameTextView.setText(productName);
                ScanActivity.mName = productName;
            }
        });
        mWebView.loadUrl(mURL);

        return view;
    }
}
