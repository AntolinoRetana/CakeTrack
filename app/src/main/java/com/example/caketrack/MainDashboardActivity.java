package com.example.caketrack;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.caketrack.Admin.Fragments.FragmentCliente;
import com.example.caketrack.Admin.Fragments.FragmentHome;
import com.example.caketrack.Admin.Fragments.FragmentPastel;
import com.example.caketrack.Admin.Fragments.FragmentReserva;
import com.example.caketrack.Admin.Fragments.FragmentUsuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainDashboardActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Fragmento inicial
        loadFragment(new FragmentHome());

        // Navegación entre fragmentos
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_usuario) {
                fragment = new FragmentUsuario();
            } else if (id == R.id.nav_clientes) {
               fragment = new FragmentCliente();
            } else if (id == R.id.nav_estado) {
               fragment = new FragmentHome();
            }
            else if (id == R.id.nav_pasteles) {
                fragment = new FragmentPastel();
            }
            else if (id == R.id.nav_reservas) {
                fragment = new FragmentReserva();
            }
            return loadFragment(fragment);
        });

        bottomNavigation.setSelectedItemId(R.id.nav_usuario);
    }

    // Cargar el fragmento
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}