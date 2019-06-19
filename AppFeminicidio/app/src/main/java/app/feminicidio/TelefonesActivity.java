package app.feminicidio;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class TelefonesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefones);

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.sidebar);
        setupDrawerContent(nvDrawer);

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
