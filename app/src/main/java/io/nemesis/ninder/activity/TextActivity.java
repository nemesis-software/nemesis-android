package io.nemesis.ninder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.nemesis.ninder.R;

public class TextActivity extends AppCompatActivity {
    public static final String TYPE = "NINDER";
    Toolbar mToolbar;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(mToolbar);
        String type = getIntent().getStringExtra("TEXT_TYPE");
        title = (TextView) findViewById(R.id.title);
        title.setText(type);
        mToolbar.setTitle(type);
        Log.d("TYPE",type);
        if(type.equals(getString(R.string.button_privacy))){
        }
        else if(type.equals(getString(R.string.button_terms))){
        }
        else{
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
