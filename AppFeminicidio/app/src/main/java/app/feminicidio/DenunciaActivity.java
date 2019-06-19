package app.feminicidio;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class DenunciaActivity extends AppCompatActivity {

    private Spinner selectDenuncia;
    private EditText textoDenuncia;
    private Button bFazerDenuncia;
    private static int totalDenuncias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia);

        // Inicializando e referenciando os objetos;
        selectDenuncia = (Spinner) findViewById(R.id.selectDenuncia);
        textoDenuncia = (EditText) findViewById(R.id.textoDenuncia);
        bFazerDenuncia = (Button) findViewById(R.id.bFazerDenuncia);

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.sidebar);
        setupDrawerContent(nvDrawer);

        inicializaFirebase();
        firebaseLeTotalDenuncias();
        /**
         * Ao tocar no botao, envia a denuncia para o banco de dados do Firebase.
         */
        bFazerDenuncia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentTime = Calendar.getInstance().getTime();
                String data = currentTime.toString();
                //Adiciona a denuncia no banco de dados;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("denuncias/"+selectDenuncia.getSelectedItem().toString()+"/"+data);
                String denuncia = textoDenuncia.getText().toString();
                myRef.setValue(denuncia);

                // Atualiza o total de denuncias no banco de dados;
                FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                DatabaseReference myRef2 = database.getReference("total_denuncias");
                totalDenuncias++;
                myRef2.setValue(""+totalDenuncias);

                // Mostra uma mensagem ao usuario e retorna a activity principal;
                Toast.makeText(DenunciaActivity.this, "Denuncia enviada.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Inicializa o Firebase.
     */
    private void inicializaFirebase(){
        FirebaseApp.initializeApp(getApplicationContext());
    }

    /**
     * Le do banco de dados do Firebase o total de denuncias ja armazenadas, para gerar uma chave nova.
     */
    private void firebaseLeTotalDenuncias() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("total_denuncias");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                totalDenuncias = Integer.parseInt(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
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

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openTelefonesActivity() {
        Intent intent = new Intent(this, TelefonesActivity.class);
        startActivity(intent);
    }

    public void openInfoActivity() {
        Intent intent = new Intent(this, InformacoesActivity.class);
        startActivity(intent);
    }


}
