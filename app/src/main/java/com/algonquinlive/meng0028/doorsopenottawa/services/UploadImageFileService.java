package com.algonquinlive.meng0028.doorsopenottawa.services;

/**
 * Created by Yanming Meng (meng0028)  on 2018/1/10.
 */

        import android.app.IntentService;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Log;


        import java.io.ByteArrayOutputStream;
        import com.google.gson.Gson;
        import com.algonquinlive.meng0028.doorsopenottawa.utils.HttpHelper;
        import com.algonquinlive.meng0028.doorsopenottawa.utils.RequestPackage;

        import static com.algonquinlive.meng0028.doorsopenottawa.MainActivity.NEW_BUILDING_IMAGE;

/**
 * UploadImageFileService.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */
public class UploadImageFileService extends IntentService {

    public static final String UPLOAD_IMAGE_FILE_SEVICE_MESSAGE = "UploadImageFileServiceMessage";
    public static final String UPLOAD_IMAGE_FILE_SERVICE_RESPONSE = "UploadImageFileServiceResponse";
    public static final String UPLOAD_IMAGE_FILE_SERVICE_EXCEPTION = "UploadImageFileServiceException";
    public static final String REQUEST_PACKAGE = "requestPackage";

    public UploadImageFileService() {
        super("UploadImageFileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RequestPackage requestPackage = (RequestPackage) intent.getParcelableExtra(REQUEST_PACKAGE);
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra(NEW_BUILDING_IMAGE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, baos);

        String response = null;
        try {
            response = HttpHelper.uploadFile(requestPackage, "meng0028", "password", "photo.jpg", baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            Intent messageIntent = new Intent(UPLOAD_IMAGE_FILE_SEVICE_MESSAGE);
            messageIntent.putExtra(UPLOAD_IMAGE_FILE_SERVICE_EXCEPTION, e.getMessage());
            LocalBroadcastManager manager =
                    LocalBroadcastManager.getInstance(getApplicationContext());
            manager.sendBroadcast(messageIntent);
            return;
        }

        Intent messageIntent = new Intent(UPLOAD_IMAGE_FILE_SEVICE_MESSAGE);
        Gson gson = new Gson();
        try {
            String message = "Uploaded Image";
            messageIntent.putExtra(UPLOAD_IMAGE_FILE_SERVICE_RESPONSE, message);
            LocalBroadcastManager manager =
                    LocalBroadcastManager.getInstance(getApplicationContext());
            manager.sendBroadcast(messageIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
