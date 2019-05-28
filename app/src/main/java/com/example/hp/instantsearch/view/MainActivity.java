package com.example.hp.instantsearch.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.hp.instantsearch.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {


    Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_local_search)
    public void openLocalSearch(){
     startActivity(new Intent(MainActivity.this,LocalSearchActivity.class));
    }

    @OnClick(R.id.btn_remote_search)
    public void openRemoteSearch(){
        startActivity(new Intent(MainActivity.this,RemoteSearchActivity.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
