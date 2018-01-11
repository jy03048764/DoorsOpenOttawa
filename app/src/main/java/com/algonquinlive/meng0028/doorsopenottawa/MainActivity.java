package com.algonquinlive.meng0028.doorsopenottawa;

/**
 * Created by Yanming Meng (meng0028)  on 2018/1/10.
 */

//import all applicable packages
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import com.algonquinlive.meng0028.doorsopenottawa.model.BuildingPOJO;
import com.algonquinlive.meng0028.doorsopenottawa.services.MyService;
import com.algonquinlive.meng0028.doorsopenottawa.services.UploadImageFileService;
import com.algonquinlive.meng0028.doorsopenottawa.utils.HttpMethod;
import com.algonquinlive.meng0028.doorsopenottawa.utils.NetworkHelper;
import com.algonquinlive.meng0028.doorsopenottawa.utils.RequestPackage;

public class MainActivity extends AppCompatActivity {
// declare variables
    public static final String  NEW_BUILDING_DATA = "NEW_BUILDING_DATA";
    public static final String  NEW_BUILDING_IMAGE = "NEW_BUILDING_IMAGE";
    public static final String  EDIT_BUILDING_DATA = "EDIT_BUILDING_DATA";
    private static final String REMEMBER_SELECTED_CATEGORY_ID = "lastSelectedCategoryId";
    private static final String ABOUT_DIALOG_TAG = "About Dialog";
    public static final String JSON_URL = "https://doors-open-ottawa.mybluemix.net/buildings";



    public static final int     REQUEST_NEW_BUILDING = 1;
    public static final int     REQUEST_EDIT_BUILDING = 2;
    private static final int    NO_SELECTED_CATEGORY_ID = -1;


    private BuildingAdapter    mBuildingAdapter;
    private List<BuildingPOJO> mBuildingsList;
    private String[]           mCategories;
    private DrawerLayout       mDrawerLayout;
    private ListView           mDrawerList;
    private ProgressBar        mProgressBar;
    private RecyclerView       mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private int                mRememberSelectedCategoryId;
    private Bitmap             mBitmap;



    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (intent.hasExtra(MyService.MY_SERVICE_PAYLOAD)) {
                BuildingPOJO[] buildingsArray = (BuildingPOJO[]) intent
                        .getParcelableArrayExtra(MyService.MY_SERVICE_PAYLOAD);
                Toast.makeText(MainActivity.this,
                        "Received " + buildingsArray.length + " buildings from service",
                        Toast.LENGTH_SHORT).show();

                mBuildingsList = Arrays.asList(buildingsArray);
                displayBuildings();
            } else if (intent.hasExtra(MyService.MY_SERVICE_RESPONSE)) {
                BuildingPOJO myBuilding = intent.getParcelableExtra(MyService.MY_SERVICE_RESPONSE);
                uploadBitmap(myBuilding.getBuildingId());
            } else if (intent.hasExtra(MyService.MY_SERVICE_EXCEPTION)) {
                String message = intent.getStringExtra(MyService.MY_SERVICE_EXCEPTION);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pbNetwork);
        mProgressBar.setVisibility(View.INVISIBLE);

//      Code to manage sliding navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mCategories = getResources().getStringArray(R.array.categories);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_category_row, mCategories));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Category: " + mCategories[position], Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawer(mDrawerList);
                fetchBuildings(position);
            }
        });
//      end of navigation drawer

        mRecyclerView = (RecyclerView) findViewById(R.id.rvBuildings);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.new_building);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewBuildingActivity.class);
                startActivityForResult(intent, REQUEST_NEW_BUILDING);
            }
        });

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));

        SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
        mRememberSelectedCategoryId = settings.getInt(REMEMBER_SELECTED_CATEGORY_ID, NO_SELECTED_CATEGORY_ID);
        fetchBuildings(mRememberSelectedCategoryId);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_BUILDING) {
            if (resultCode == RESULT_OK) {
                BuildingPOJO newBuilding = data.getExtras().getParcelable(NEW_BUILDING_DATA);
                Bitmap image  = (Bitmap) data.getParcelableExtra(NEW_BUILDING_IMAGE);
                if (image != null) {
                    mBitmap = image;
                }

                Toast.makeText(this, "Added Building: " + newBuilding.getNameEN(), Toast.LENGTH_SHORT).show();
            }

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled: Add New Building", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt( REMEMBER_SELECTED_CATEGORY_ID, mRememberSelectedCategoryId );
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                // show about dialog
                DialogFragment newFragment = new AboutDialogFragment();
                newFragment.show(getFragmentManager(), ABOUT_DIALOG_TAG);
                return true;
            case R.id.action_all_items:
                // fetch and display all buildings
                fetchBuildings(NO_SELECTED_CATEGORY_ID);
                return true;
            case R.id.action_choose_category:
                // open the drawer
                mDrawerLayout.openDrawer(mDrawerList);
                return true;
            case R.id.action_sort_atoz:
                // sort from a to z
                mBuildingAdapter.sortByNameAscending();
                return true;
            case R.id.action_sort_ztoa:
                // sort from z to a
                mBuildingAdapter.sortByNameDescending();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayBuildings() {
        if (mBuildingsList != null) {
            mBuildingAdapter = new BuildingAdapter(this, mBuildingsList);
            mRecyclerView.setAdapter(mBuildingAdapter);
        }
    }

    private void fetchBuildings(int selectedCategoryId) {
        mRememberSelectedCategoryId = selectedCategoryId;
        if (NetworkHelper.hasNetworkAccess(this)) {
            mProgressBar.setVisibility(View.VISIBLE);
            RequestPackage requestPackage = new RequestPackage();
            requestPackage.setEndPoint(JSON_URL);
            if (selectedCategoryId != NO_SELECTED_CATEGORY_ID) {
                requestPackage.setParam("categoryId", selectedCategoryId + "");
            }

            Intent intent = new Intent(this, MyService.class);
            intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
            startService(intent);
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadBitmap(int buildingId) {
        if (mBitmap != null ) {
            RequestPackage requestPackage = new RequestPackage();
            requestPackage.setMethod(HttpMethod.POST);
            requestPackage.setEndPoint(JSON_URL + "/" + buildingId + "/image");

            Toast.makeText(this, "Uploaded image for Building Id: " + buildingId + "", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, UploadImageFileService.class);
            intent.putExtra(UploadImageFileService.REQUEST_PACKAGE, requestPackage);
            intent.putExtra(NEW_BUILDING_IMAGE, mBitmap);
            startService(intent);
        }
    }
}