package com.weget.fuyan.fyp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Claudie on 10/28/16.
 */
public class FilterActivity extends AppCompatActivity {
    private SeekBar priceBar, radiusBar;
    private Button resetBtn, applyBtn;
    private int result, result1;
    private static int lastValueRadius, lastValuePrice;
    private static boolean lastSwitchRadius;
    private static boolean hasChanged;
    private Switch radiusSwitch;
    private boolean switchEnabled;
    private TextView dynamicPrice;
    private TextView dynamicRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filter");
        dynamicPrice = (TextView) findViewById(R.id.priceRange);
        dynamicRadius = (TextView) findViewById(R.id.radiusRange);
        /*
         * Configures the radius filter as selected by users
         * Radius Switch disables the radius displayed in home screen
         */
        if(!hasChanged){
            lastValueRadius = 500;
            lastValuePrice = 10;
        }

        radiusBar=(SeekBar)findViewById(R.id.radiusBar);

        radiusBar.setProgress(lastValueRadius);

        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                lastValueRadius = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(FilterActivity.this, "Radius set at " + progressChangedValue + "m",
//                        Toast.LENGTH_SHORT).show();
                int val = (progressChangedValue * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                dynamicRadius.setVisibility(View.VISIBLE);
                dynamicRadius.setText(progressChangedValue+"m");
                dynamicRadius.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
                result = progressChangedValue;
            }
        });

        radiusSwitch = (Switch) findViewById(R.id.switch1);
        if(hasChanged){
            radiusSwitch.setChecked(lastSwitchRadius);
        }


        priceBar = (SeekBar) findViewById(R.id.priceBar);
        priceBar.setProgress(lastValuePrice);
        priceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                lastValuePrice = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                int val = (progressChangedValue * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                dynamicPrice.setVisibility(View.VISIBLE);
                dynamicPrice.setText("$" + progressChangedValue);
                dynamicPrice.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
                result1 = progressChangedValue;
            }
        });


        resetBtn = (Button) findViewById(R.id.reset_button);
        applyBtn = (Button) findViewById(R.id.apply_button);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radiusBar.setProgress(500);
                radiusSwitch.setChecked(true);
                priceBar.setProgress(priceBar.getMax());

                lastValueRadius = radiusBar.getProgress();
                lastSwitchRadius = radiusSwitch.isChecked();
                lastValuePrice = priceBar.getProgress();

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasChanged = true;
                lastSwitchRadius = radiusSwitch.isChecked();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("radius",lastValueRadius);
                returnIntent.putExtra("switch", radiusSwitch.isChecked());
                returnIntent.putExtra("price", lastValuePrice);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }



    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
