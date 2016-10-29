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
import android.widget.Toast;

/**
 * Created by Claudie on 10/28/16.
 */
public class FilterActivity extends AppCompatActivity {
    private SeekBar priceBar, radiusBar;
    private Button resetBtn, applyBtn;
    private int result;
    private static int lastValue;
    private static boolean lastSwitch;
    private static boolean hasChanged;
    private Switch radiusSwitch;
    private boolean switchEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filter");

        radiusBar=(SeekBar)findViewById(R.id.seekBar);

        radiusBar.setProgress(lastValue);

        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                lastValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(FilterActivity.this, "Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
                result = progressChangedValue;
            }
        });

        radiusSwitch = (Switch) findViewById(R.id.switch1);
        if(hasChanged){
            radiusSwitch.setChecked(lastSwitch);
        }


        resetBtn = (Button) findViewById(R.id.reset_button);
        applyBtn = (Button) findViewById(R.id.apply_button);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radiusBar.setProgress(600);
                radiusSwitch.setChecked(true);

                lastValue = radiusBar.getProgress();
                lastSwitch = radiusSwitch.isChecked();

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasChanged = true;
                lastSwitch = radiusSwitch.isChecked();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("radius",lastValue);
                returnIntent.putExtra("switch", radiusSwitch.isChecked());
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
