package app.feminicidio;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class SetEmergencyTimeActivity extends AppCompatActivity {
    private TextView tempEscolhido;
    private SeekBar sliderTempo;
    private Button confirma;
    private int width, height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_emergency_time_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        width = dm.widthPixels;
        height = dm.heightPixels;

        inicializaComponentes();



    }

    private void inicializaComponentes() {
        sliderTempo = (SeekBar) findViewById(R.id.seekBar_tempo);
        sliderTempo.setProgress(0);
        tempEscolhido = (TextView) findViewById(R.id.tv_tmp_escolhido);
        confirma = (Button) findViewById(R.id.bt_confirmar_emergencia);

        sliderTempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempEscolhido.setText(Integer.toString(seekBar.getProgress() + 1) + " min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        confirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("tempo",(sliderTempo.getProgress()+1)*60000);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}

