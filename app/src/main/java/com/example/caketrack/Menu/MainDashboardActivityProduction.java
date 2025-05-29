package com.example.caketrack.Menu;

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
import com.example.caketrack.Admin.Fragments.FragmentReservasListas;
import com.example.caketrack.Admin.Fragments.FragmentReservasPendientes;
import com.example.caketrack.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainDashboardActivityProduction extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_dashboard_production);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Fragmento inicial
        loadFragment(new FragmentReservasPendientes());

        // Navegación entre fragmentos
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_pasteles_pendiente) {
                fragment = new FragmentReservasPendientes();
            } else if (id == R.id.nav_pasteles_listas) {
                fragment = new FragmentReservasListas();
            }
            return loadFragment(fragment);
        });

        bottomNavigation.setSelectedItemId(R.id.nav_pasteles_pendiente);
    }

    // Cargar el fragmento
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerSeller, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}