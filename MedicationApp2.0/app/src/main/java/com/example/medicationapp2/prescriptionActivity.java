package com.example.medicationapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class prescriptionActivity extends AppCompatActivity implements SingleMedicineActivity {

    RecyclerView recyclerView;
    List<medicines> list;
    medicineAdapter medicineAdapter;
    SearchView searchView;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        searchView = findViewById(R.id.searchView);
        linearLayout = findViewById(R.id.listMainLayout);

        // To display item in the list
        displayItems();

        // To search a medicine from the list
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        // Call The Delete Function
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.percription);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.homePage:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0 );
                        return true;

                    case R.id.percription:
                        startActivity(new Intent(getApplicationContext(), prescriptionActivity.class));
                        overridePendingTransition(0,0 );
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), profileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void filter(String newText) {
        List<medicines> filteredList = new ArrayList<>();
        for (medicines item : list){
            if (item.getmName().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(item);
            }
        }
        medicineAdapter.filterList(filteredList);
    }

    private void displayItems() {
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        // Filling The List
        list = new ArrayList<>();
        list.add(new medicines("Dolo"));
        list.add(new medicines("Crocin"));
        list.add(new medicines("Tramadol"));
        list.add(new medicines("Meloxicam"));

        //
        medicineAdapter = new medicineAdapter(this, list, this);
        recyclerView.setAdapter(medicineAdapter);
    }

    @Override
    public void onItemClicked(medicines medicines) {
        setContentView(R.layout.single_medicine_page);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.percription);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.homePage:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0 );
                        return true;

                    case R.id.percription:
                        startActivity(new Intent(getApplicationContext(), prescriptionActivity.class));
                        overridePendingTransition(0,0 );
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), profileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Snackbar snackbar = Snackbar.make(linearLayout, "Medicine Deleted!", Snackbar.LENGTH_LONG);
            snackbar.show();

            list.remove(viewHolder.getAdapterPosition());
            medicineAdapter.notifyDataSetChanged();
        }
    };
}
