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

    private WebView mWebView, mWebView2;

    private String mUPC, mURL;

    public String mProductName;

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
        mWebView2 = (WebView) view.findViewById(R.id.webView2);

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                // Extract the product name from the string
                int i = 0;
                mProductName = "";
                String unWantedString = "";
                while(title.charAt(i) != ':'){

                    unWantedString += title.charAt(i);
                    i++;
                }

                for(int j = i+2; j<title.length(); j++){

                    mProductName += title.charAt(j);
                }

                ScanActivity.productNameTextView.setText(mProductName);

                ScanActivity.mName = mProductName;
                ScanActivity.mLastDatePurchased="NEVER";

            }
        });

        mWebView.loadUrl(mURL);

        mURL = "";

        // Try to get the price info from Amazon.com

        mURL = "https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=";
        mURL += mUPC;

        mWebView2.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);


            }
        });

        mWebView2.loadUrl(mURL);


        return view;
    }
}
