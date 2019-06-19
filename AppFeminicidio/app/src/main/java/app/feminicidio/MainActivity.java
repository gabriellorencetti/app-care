package app.feminicidio;

import android.content.ClipData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    //protected Location sLatitudeLabel;

    private Button bDenuncia, bTelefones, bInfo;
    private TextView nomeApp;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
   // private FusedLocationProviderClient mFusedLocationClient;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializando e referenciando os objetos;
        bDenuncia = (Button) findViewById(R.id.bDenuncia);
        bTelefones = (Button) findViewById(R.id.bTelefones);
        bInfo = (Button) findViewById(R.id.bInfos);
        nomeApp = (TextView) findViewById(R.id.textLogo);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Adicionando a sidebar
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.sidebar);
        setupDrawerContent(nvDrawer);


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
    }

    /**
     * Metodo que inicializa e abre a activity de denuncia.
     */
    public void openDenunciaActivity(){
        Intent intent = new Intent(this, DenunciaActivity.class);
        startActivity(intent);
    }

    /**
     * Metodo que inicializa e abre a activity de telefones.
     */
    public void openTelefonesActivity(){
        Intent intent = new Intent(this, TelefonesActivity.class);
        startActivity(intent);
    }

    /**
     * Metodo que inicializa e abre a activity de informacoes.
     */
    public void openInfoActivity(){
        Intent intent = new Intent(this, InformacoesActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()){
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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_button, menu);
        return true;
    }

    public void selectItemDrawer(MenuItem menuItem){

        switch (menuItem.getItemId()){
            case R.id.nav_main:
                //something
                break;

            case R.id.nav_denuncia:
                openDenunciaActivity();
                break;
            case R.id.nav_emergencia:
                //something
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

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectItemDrawer(menuItem);
                return true;
            }
        });
    }

   /* private synchronized void callConnection(){
        mGoogleApiClient =
    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended("+i+")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed("+connectionResult+")");
    }
}
