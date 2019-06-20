package app.feminicidio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TelefonesActivity extends AppCompatActivity {
    private static final int REQUEST_CALL = 1;
    private Button telefoneDelegaciaMulher, telefoneDireitosHumanos, telefoneCVV, telefoneSAMU;
    private int foneDelMulher = 180, foneSamu = 192, foneCVV = 188, foneDirHum = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefones);

        //inicializando os botões
        telefoneDelegaciaMulher = (Button) findViewById(R.id.btDelegaciaMulher);
        telefoneDireitosHumanos = (Button)  findViewById(R.id.btDireitosHumanos);
        telefoneCVV= (Button)  findViewById(R.id.btCVV);
        telefoneSAMU = (Button)  findViewById(R.id.btSAMU);

        inicializaBotoes();

        // Inicializa o sidebar menu;
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.sidebar);
        setupDrawerContent(nvDrawer);

    }

    private void inicializaBotoes() {
        telefoneDelegaciaMulher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telefonar(foneDelMulher);
            }
        });
//
        telefoneDireitosHumanos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telefonar(foneDirHum);
            }
        });

        telefoneCVV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telefonar(foneCVV);
            }
        });

        telefoneSAMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telefonar(foneSamu);
            }
        });

    }

    private void telefonar(int opt) {
        Intent intent = new Intent(Intent.ACTION_CALL);

        if(ContextCompat.checkSelfPermission(TelefonesActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( TelefonesActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL );
        }else{
            intent.setData(Uri.parse("tel:" + opt));
            Toast.makeText(this,"Ligando...", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Você precisa permitir que o app faça ligações.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Permissões concedidas.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectItemDrawer(menuItem);
                return true;
            }
        });
    }

    public void selectItemDrawer(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_main:
                openMainActivity();
                break;

            case R.id.nav_denuncia:
                openDenunciaActivity();
                break;

            case R.id.nav_telefones:
                break;

            case R.id.nav_info:
                openInfoActivity();
                break;
            default:
                break;
        }
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openInfoActivity() {
        Intent intent = new Intent(this, InformacoesActivity.class);
        startActivity(intent);
    }

    public void openDenunciaActivity() {
        Intent intent = new Intent(this, DenunciaActivity.class);
        startActivity(intent);
    }
}
