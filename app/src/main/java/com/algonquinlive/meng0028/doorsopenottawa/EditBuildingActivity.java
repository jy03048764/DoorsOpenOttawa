package com.algonquinlive.meng0028.doorsopenottawa;

/**
 * Created by Yanming Meng (meng0028)  on 2018/1/10.
 */
// import all packages

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.algonquinlive.meng0028.doorsopenottawa.model.BuildingPOJO;
import com.algonquinlive.meng0028.doorsopenottawa.services.MyService;
import com.algonquinlive.meng0028.doorsopenottawa.utils.HttpMethod;
import com.algonquinlive.meng0028.doorsopenottawa.utils.RequestPackage;
import com.algonquinlive.meng0028.doorsopenottawa.BuildingAdapter;

public class EditBuildingActivity extends AppCompatActivity {
    // declare variables
    private final String JSON_URL = "https://doors-open-ottawa.mybluemix.net/buildings/:";
    private final String TAG = "EDIT";
    BuildingPOJO mBuilding;
    EditText editNameOld;
    EditText editDescription;
    CheckBox isBuildingNew;
    EditText editSaturdayStartTime;
    EditText editSaturdayEndTime;
    EditText editSundayStartTime;
    EditText editSundayEndTime;
    Button saveOldBtn;
    Button cancelOldBtn;
    Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_building);
        BuildingPOJO mBuilding = getIntent().getExtras().getParcelable(BuildingAdapter.BUILDING_KEY);
        if (mBuilding == null) {
            throw new AssertionError("Null data item received!");
        }
        // assign objects to variables
        editNameOld = (EditText) findViewById(R.id.editNameOld);
        editDescription = (EditText) findViewById(R.id.editDescription);
        isBuildingNew = (CheckBox) findViewById(R.id.isBuildingNew);
        editSaturdayStartTime = (EditText) findViewById(R.id.editSaturdayStartTime);
        editSaturdayEndTime = (EditText) findViewById(R.id.editSaturdayEndTime);
        editSundayStartTime = (EditText) findViewById(R.id.editSundayStartTime);
        editSundayEndTime = (EditText) findViewById(R.id.editSundayEndTime);
        saveOldBtn = (Button) findViewById(R.id.saveOldBtn);
        cancelOldBtn = (Button) findViewById(R.id.cancelOldBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        // loading existing values
        editNameOld.setText(mBuilding.getNameEN());
        editDescription.setText(mBuilding.getDescriptionEN());
        isBuildingNew.setChecked(mBuilding.isIsNewBuilding());
        editSaturdayStartTime.setText(mBuilding.getSaturdayStart().toString());
        editSaturdayEndTime.setText(mBuilding.getSaturdayClose().toString());
        editSundayStartTime.setText(mBuilding.getSundayStart().toString());
        editSundayEndTime.setText(mBuilding.getSundayClose().toString());
        // add event listeners to buttons
        saveOldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBuilding();
            }
        });
        cancelOldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBuilding();
            }
        });
    }


    private void updateBuilding() {
        BuildingPOJO pluto = new BuildingPOJO();
        mBuilding.setAddressEN(editNameOld.getText().toString());
        mBuilding.setDescriptionEN(editDescription.getText().toString());
        mBuilding.setIsNewBuilding(isBuildingNew.isChecked());
        mBuilding.setSaturdayStart(editSaturdayStartTime.getText().toString());
        mBuilding.setSaturdayClose(editSaturdayEndTime.getText().toString());
        mBuilding.setSundayStart(editSundayStartTime.getText().toString());
        mBuilding.setSundayClose(editSundayEndTime.getText().toString());

        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(HttpMethod.PUT);
        requestPackage.setEndPoint(MainActivity.JSON_URL + "/" + mBuilding.getBuildingId());
        requestPackage.setParam("addressEN", mBuilding.getAddressEN());
        requestPackage.setParam("descriptionEN", mBuilding.getDescriptionEN());
        requestPackage.setParam("isNewBuilding", mBuilding.isIsNewBuilding() + "");
        requestPackage.setParam("saturdayStart", mBuilding.getSaturdayStart().toString());
        requestPackage.setParam("saturdayClose", mBuilding.getSaturdayClose().toString());
        requestPackage.setParam("sundayStart", mBuilding.getSundayStart().toString());
        requestPackage.setParam("sundayClose", mBuilding.getSundayClose().toString());
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
        startService(intent);
        Log.i(TAG, "updatePlanetPluto(): " + mBuilding.getNameEN());
        finish();
    }
    private void deleteBuilding() {
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(HttpMethod.DELETE);
        requestPackage.setEndPoint(JSON_URL + "/" + Integer.toString(mBuilding.getBuildingId()));
        requestPackage.setParam("buildingId", mBuilding.getBuildingId() + "");
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
        startService(intent);
        mBuilding.remove(position);
        BuildingAdapter.this.notifyDataSetChanged();
        dialog.dismiss();
    }
}
