package com.example.caketrack.Menu;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.example.caketrack.LoginActivity;
import com.example.caketrack.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

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
            else if (id == R.id.nav_log_out_production) {
                logout();
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
    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut(); // Cierra sesión
                    Intent intent = new Intent(this, LoginActivity.class); // Reemplaza con tu login
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Elimina historial
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}