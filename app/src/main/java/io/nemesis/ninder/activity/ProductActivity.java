package io.nemesis.ninder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import io.nemesis.ninder.R;

public class ProductActivity extends Activity {

    public static final String EXTRA_ITEM = "item";
    private ImageButton btnCheckmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        btnCheckmark = (ImageButton) findViewById(R.id.button_checkmark);
        btnCheckmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO is this work?
                finish();
            }
        });

    }
}
