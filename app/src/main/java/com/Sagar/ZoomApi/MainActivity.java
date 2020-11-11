package com.Sagar.ZoomApi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;



import dmax.dialog.SpotsDialog;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MainActivityViewModel viewModel;
    private SearchView searchView;
    private TextView resultsFoundTV;
    private LinearLayoutManager manager;
    private AlertDialog spotsBox;

    private restaurantsAdapter restroAdapter;
    private boolean isRecyclerViewInit = false;
    private boolean isScrolling = false;
    private String query;
    private static final String TAG = "debug";
    private int currentItems, totalItems, scrollOutItems, totalResultsFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        recyclerView.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        spotsBox.show();
        viewModel.queryRestaurants("" ,  false);
        observeData();
        stopDialogAfterFiveSeconds();
        addScrollListener();
    }

    private void observeData() {

        viewModel.getResultsFound().observe(this, totalResults -> {
            if (spotsBox.isShowing()) {
                spotsBox.cancel();
            }
            if (totalResults == null) {
                resultsFoundTV.setVisibility(View.GONE);
                return;
            }
            totalResultsFound = totalResults;
            String resultsFound = "Results Found: " + totalResults;
            resultsFoundTV.setVisibility(View.VISIBLE);
            resultsFoundTV.setText(resultsFound);
        });
        viewModel.getResroLiveData().observe(this, restaurants -> {
            if (spotsBox.isShowing()) {
                spotsBox.cancel();
            }
            if (!isRecyclerViewInit) {
                initRecyclerView();
            } else {
                restroAdapter.setRestaurants(restaurants);
            }
        });

    }

    private void initViews() {
        toolbar = findViewById(R.id.tools);
        recyclerView = findViewById(R.id.rv);
        resultsFoundTV = findViewById(R.id.results_found_tv);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        manager = new LinearLayoutManager(this);
        spotsBox = new SpotsDialog(this, "Loading");
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(manager);
        recyclerView.setVisibility(View.VISIBLE);
        restroAdapter = new restaurantsAdapter(this, viewModel.getResroLiveData().getValue());
        recyclerView.setAdapter(restroAdapter);
        isRecyclerViewInit = true;
    }

    private void addScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling
                        && currentItems + scrollOutItems == totalItems
                        && totalResultsFound > totalItems) {
                    Log.d(TAG, "onScrolled: Loading more items");
                    viewModel.queryRestaurants(query,  true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newQuery) {
                spotsBox.show();
                query = newQuery;
                viewModel.queryRestaurants(query,  false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }
    private void stopDialogAfterFiveSeconds(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                spotsBox.cancel();
            }
        }).start();
    }
}
