package com.share.jack.customviewpath;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.share.jack.customviewpath.widget.ProgressStatusView;

/**
 *
 */

public class StatusActivity extends Activity implements View.OnClickListener {

    private ProgressStatusView progressStatusView;
//    private CustomStatusView customStatusView2;
//    private CustomStatusView customStatusView3;

    private Button btnSuccess;
    private Button btnFailure;
    private Button resetBtn;
    private Button waitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        progressStatusView = (ProgressStatusView) findViewById(R.id.as_status);
//        customStatusView2 = (CustomStatusView) findViewById(R.id.as_status2);
//        customStatusView3 = (CustomStatusView) findViewById(R.id.as_status3);
        btnSuccess = (Button) findViewById(R.id.as_btn_success);
        btnFailure = (Button) findViewById(R.id.as_btn_failure);
        resetBtn = (Button) findViewById(R.id.as_btn_reset);
        waitBtn = (Button) findViewById(R.id.as_btn_waiting);
        progressStatusView.loadWaiting();
//        customStatusView2.loadLoading();
//        customStatusView3.loadLoading();
        btnSuccess.setOnClickListener(this);
        btnFailure.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        waitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.as_btn_success:
                progressStatusView.loadSuccess();
//                customStatusView2.loadSuccess();
//                customStatusView3.loadSuccess();
                break;
            case R.id.as_btn_failure:
                progressStatusView.loadFailure();
//                customStatusView2.loadFailure();
//                customStatusView3.loadFailure();
                break;
            case R.id.as_btn_reset:
                progressStatusView.loadLoading();
                break;
            case R.id.as_btn_waiting:

                progressStatusView.loadWaiting();
                break;
        }
    }
}