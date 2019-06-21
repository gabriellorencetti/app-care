package app.feminicidio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    private Button bDenuncia, bTelefones, bInfo, bEmergenciaL;
    private TextView nomeApp;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private static String longitude;
    private static String latitude;
    private static int totalEmergencias;
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient fusedLocationClient;
    boolean ativarEnvioLocalizacao = false, modoEmergencia = false;
    private static final int TEMPO_EMERGENCIA_REQUEST = 1;
    private int tempoEmergencia;
    private Timer timerModoEmergencia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializando e referenciando os objetos;
        bDenuncia = (Button) findViewById(R.id.bDenuncia);
        bTelefones = (Button) findViewById(R.id.bTelefones);
        bInfo = (Button) findViewById(R.id.bInfos);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        bEmergenciaL = (Button) findViewById(R.id.bEmergenciaL);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Adicionando a sidebar
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.sidebar);
        setupDrawerContent(nvDrawer);

        // Inicializando o banco de dados Firebase
        FirebaseApp.initializeApp(getApplicationContext());
        firebaseLeTotalEmergencias();

        //Inicializando o sensor que detecta a movimentacao do celular;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        //inicializando o banco de localizacao inicial
        getLocalizacao();

        //solicitando permissoes
        getPermissaoLocalizacao();


        // Listener para detectar movimento;
        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            public void onShake() {
                if(ativarEnvioLocalizacao) enviaLocalizacao();
            }
        });

        // Listener do botao "Fazer denuncia", que abre a activity de denuncia;
        bDenuncia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDenunciaActivity();
            }
        });

        // Listener do botao "Telefones uteis", que abre a activity de telefones;
        bTelefones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTelefonesActivity();
            }
        });

        // Temporizador do modo de emergencia
        timerModoEmergencia = new Timer();
        bEmergenciaL.setEnabled(true);

        // Listener do botao Emergencia, que ativa o envio da localizacao atual ao banco de dados e agenda a tarefa de desligamento automatico;
        bEmergenciaL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ligaDesligaModoEmergencia();
                }
        });
    } // Fim do onCreate

    /**
     * Metodo que envia a localizacao atual do usuario para o banco de dados.
     */
    public void enviaLocalizacao(){

        getPermissaoLocalizacao();
        getLocalizacao();
        escreveLocBD();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrando por 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(1000);
        }

        Toast.makeText(MainActivity.this, "Localização enviada!", Toast.LENGTH_LONG).show();
        ativarEnvioLocalizacao = false;
    }

    /**
     * Funcao para a deteccao continua de movimento.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Funcao para a deteccao continua de movimento.
     */
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    /**
     * Metodo que inicializa e abre a activity de denuncia.
     */
    public void openDenunciaActivity() {
        Intent intent = new Intent(this, DenunciaActivity.class);
        startActivity(intent);
    }

    /**
     * Metodo que inicializa e abre a activity de telefones.
     */
    public void openTelefonesActivity() {
        Intent intent = new Intent(this, TelefonesActivity.class);
        startActivity(intent);
    }

    /**
     * Metodo que inicializa e abre a activity de informacoes.
     */
    public void openInfoActivity() {
        Intent intent = new Intent(this, InformacoesActivity.class);
        startActivity(intent);
    }

    /**
     * Metodo que gerencia o modo de emergencia, assim como a sua activity
     */
    private void ligaDesligaModoEmergencia() {
        //checa se o aparelho esta com o modo emergencia ligado
        //se estiver, ele desliga
        if(modoEmergencia == true){
            Toast.makeText(MainActivity.this, "Modo emergência desligado.", Toast.LENGTH_SHORT).show();
            modoEmergencia = false;
            timerModoEmergencia.cancel();
            timerModoEmergencia = new Timer();
        }else {//liga o modo emergencia caso contrario
            ativarEnvioLocalizacao = true;

            modoEmergencia = true;
            //muda para a activity que permite ao usuario selecionar a duracao do modo emergencia
            Intent escolheTempo = new Intent(MainActivity.this, SetEmergencyTimeActivity.class);
            startActivityForResult(escolheTempo, TEMPO_EMERGENCIA_REQUEST);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.bInfos:
                openInfoActivity();
                return true;
            case R.id.nav_info:
                openInfoActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_button, menu);
        return true;
    }

    public void selectItemDrawer(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_main:
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
            case R.id.nav_emergencia:
                ligaDesligaModoEmergencia();
                break;
            default:
                break;
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

    /**
     * Metodo que sincroniza com a API de localizacao do Google.
     */
    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        @SuppressLint("MissingPermission") Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(l != null) {
            Log.i("LOG", "latitude: "+l.getLatitude());
            Log.i("LOG", "longitude: "+l.getLongitude());

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended("+i+")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed("+connectionResult+")");
    }

    @SuppressLint("MissingPermission")
    public void getLocalizacao(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        }
                    }
                });
    }

    /**
     * Metodo que escreve a localizacao obtida no banco de dados.
     */
    public void escreveLocBD(){

        firebaseLeTotalEmergencias();
        totalEmergencias++;
        Date currentTime = Calendar.getInstance().getTime();
        String data = currentTime.toString();
        // Grava a latitude;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("emergencia/"+data+"/latitude");
        myRef.setValue(latitude);

        // Grava a longitude;
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database.getReference("emergencia/"+data+"/longitude");
        myRef2.setValue(longitude);

        // Atualiza o total de emergencias no banco de dados;
        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database.getReference("total_emergencias");
        myRef3.setValue(""+totalEmergencias);
    }

    /**
     * Metodo que verifica se o aplicativo possui permissao para acessar a localizacao,
     * caso nao possua, pede ao usuario a permissao para conseguir realizar a funcao de
     * emergencia.
     */
    public void getPermissaoLocalizacao(){

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }

        }
    }

    /**
     * Metodo que le do banco de dados, o total atual de emergencias, para
     * manter essa contagem atualizada;
     */
    private void firebaseLeTotalEmergencias() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("total_emergencias");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                totalEmergencias = Integer.parseInt(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    /**
     * Agenda o desligamento automatico do modo de emergencia
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //recupera o valor da duracao do modo emergencia escolhida pelo usuario
        if (requestCode == TEMPO_EMERGENCIA_REQUEST) {

            //se nenhum erro ocorreu
            if(resultCode == Activity.RESULT_OK){
                tempoEmergencia = data.getExtras().getInt("tempo");

                Toast.makeText(MainActivity.this, "Modo  emergência ligado por " + tempoEmergencia/60000 + " minutos", Toast.LENGTH_SHORT).show();

                //agenda o desligamento automatico do modo emergencia para 'tempoEmergencia/60000' minutos
                timerModoEmergencia.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ativarEnvioLocalizacao = false;
                                Toast.makeText(MainActivity.this, "Modo emergência desligado.", Toast.LENGTH_SHORT).show();
                                modoEmergencia = false;
                            }
                        });
                    }
                }, tempoEmergencia);
            }
        }
    }
}
