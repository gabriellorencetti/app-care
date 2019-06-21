package app.feminicidio;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Classe criada para implementar a tela de seleção do modo de emergência
 */
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

        // Inicializa sidebar menu;
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.sidebar);
        setupDrawerContent(nvDrawer);

        inicializaComponentes();
    }

    /**
     * Inicializa os componentes da activity
     */
    private void inicializaComponentes() {
        //link de componentes com a view
        sliderTempo = (SeekBar) findViewById(R.id.seekBar_tempo);
        sliderTempo.setProgress(0);
        tempEscolhido = (TextView) findViewById(R.id.tv_tmp_escolhido);
        confirma = (Button) findViewById(R.id.bt_confirmar_emergencia);
        //evento para o slider de selecao de tempo
        sliderTempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //muda o conteudo do textview simultaneamente a alteracao do slider
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempEscolhido.setText(Integer.toString(seekBar.getProgress() + 1) + " min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //botao que confirma o tempo escolhido pelo usuário e finaliza a activity
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

    /**
     * Inicializa os componentes do drawer
     * @param navigationView objeto referente ao drawer
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectItemDrawer(menuItem);
                return true;
            }
        });
    }

    /**
     * Verifica o item do menu selecionado pelo usuario e abre a activity desejada
     * @param menuItem item selecionado pelo usuario
     */
    public void selectItemDrawer(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_main:
                openMainActivity();
                break;

            case R.id.nav_denuncia:
                openDenunciaActivity();
                break;

            case R.id.nav_telefones:
                openTelefonesActivity();
                break;

            case R.id.nav_info:
                openInfoActivity();
                break;
            default:
                break;
        }
    }

    /**
     * Abre a activity principal
     */
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Abre TelefonesActivity
     */
    public void openTelefonesActivity() {
        Intent intent = new Intent(this, TelefonesActivity.class);
        startActivity(intent);
    }

    /**
     * Abre DenunciaActivity
     */
    public void openDenunciaActivity() {
        Intent intent = new Intent(this, DenunciaActivity.class);
        startActivity(intent);
    }

    /**
     * Abre InfoActivity
     */
    public void openInfoActivity() {
        Intent intent = new Intent(this, InformacoesActivity.class);
        startActivity(intent);
    }


}

