package com.algonquinlive.meng0028.doorsopenottawa;

/**
 * Created by Yanming Meng (meng0028)  on 2018/1/10.
 */
// import all packages
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algonquinlive.meng0028.doorsopenottawa.model.BuildingPOJO;
import com.algonquinlive.meng0028.doorsopenottawa.services.MyService;
import com.algonquinlive.meng0028.doorsopenottawa.utils.HttpMethod;
import com.algonquinlive.meng0028.doorsopenottawa.utils.RequestPackage;

import static com.algonquinlive.meng0028.doorsopenottawa.MainActivity.NEW_BUILDING_DATA;
import static com.algonquinlive.meng0028.doorsopenottawa.MainActivity.NEW_BUILDING_IMAGE;
import static com.algonquinlive.meng0028.doorsopenottawa.MainActivity.JSON_URL;

public class NewBuildingActivity extends AppCompatActivity {
    // declare variables
    private final static int CAMERA_REQUEST_CODE = 100;
    private EditText nameEditText;
    private EditText addressEditText;
    private FloatingActionButton cameraBtn;
    private ImageView photoView;
    private Bitmap bitmap;
    private Button saveNewBtn;
    private Button cancelNewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_new);
        // assign objects to variables
        nameEditText = (EditText) findViewById(R.id.editNameNew);
        addressEditText = (EditText) findViewById(R.id.editAddress);
        cameraBtn = (FloatingActionButton) findViewById(R.id.cameraBtn);
        photoView = (ImageView) findViewById(R.id.photoView);
        bitmap = null;
        saveNewBtn = (Button) findViewById(R.id.saveNewBtn);
        cancelNewBtn = (Button) findViewById(R.id.cancelNewBtn);
        // assign event listeners
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });
        saveNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                // check empty field
                if (name.isEmpty()) {
                    nameEditText.setError("Please Enter the Name");
                    nameEditText.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    addressEditText.setError("Please Enter the Address");
                    addressEditText.requestFocus();
                    return;
                }
                // use POST method to update JSON file
                BuildingPOJO newBuilding = new BuildingPOJO();
                newBuilding.setNameEN(name);
                newBuilding.setAddressEN(address);
                RequestPackage requestPackage = new RequestPackage();
                requestPackage.setMethod(HttpMethod.POST);
                requestPackage.setEndPoint(JSON_URL);
                requestPackage.setParam("nameEN", newBuilding.getNameEN());
                requestPackage.setParam("addressEN", newBuilding.getAddressEN());
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
                startService(intent);
                intent = new Intent();
                intent.putExtra(NEW_BUILDING_DATA, newBuilding);
                if (bitmap != null) {
                    intent.putExtra(NEW_BUILDING_IMAGE, bitmap);
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cancelNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                if (bitmap != null) {
                    photoView.setImageBitmap(bitmap);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled: Take a Photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
