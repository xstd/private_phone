package com.xstd.pirvatephone;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PrivatePhoneActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_phone);
        
        initView();
    }

    private void initView() {
		// TODO Auto-generated method stub
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.private_phone, menu);
        return true;
    }

}
