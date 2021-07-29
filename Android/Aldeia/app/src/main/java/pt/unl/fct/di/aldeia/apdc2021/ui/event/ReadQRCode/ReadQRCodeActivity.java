package pt.unl.fct.di.aldeia.apdc2021.ui.event.ReadQRCode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;
import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SendQRCodeData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.VIBRATE;

public class ReadQRCodeActivity extends AppCompatActivity {
    private ScannerLiveView camera;
    private ReadQRCodeActivity mActivity;
    private ReadQRCodeViewModel viewModel;
    private UserLocalStore storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_qrcode);
        mActivity=this;
        final Switch qrReadSwitch = findViewById(R.id.scanCodeSwitch);
        qrReadSwitch.setChecked(false);
        storage=new UserLocalStore(this);
        viewModel=new ViewModelProvider(this, new ReadQRCodeViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(ReadQRCodeViewModel.class);
        if (checkPermission()) {
            // if permission is already granted display a toast message

        } else {
            requestPermission();
        }

        // initialize scannerLiveview and textview.

        viewModel.getParticipantQRCodeResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult defaultResult) {
                if(defaultResult==null){
                    return;
                }
                if(defaultResult.getError()!=null){
                    Toast.makeText(mActivity, defaultResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if(defaultResult.getSuccess()!=null){
                    Toast.makeText(mActivity, defaultResult.getSuccess(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getLeaveQRCodeResult().observe(this, new Observer<ReadQRLeaveResult>() {
            @Override
            public void onChanged(ReadQRLeaveResult readQRLeaveResult) {
                if(readQRLeaveResult==null){
                    return;
                }
                if(readQRLeaveResult.getError()!=null){
                    Toast.makeText(mActivity, readQRLeaveResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if(readQRLeaveResult.getSuccess()!=null){
                    String message= getString(R.string.qr_coins_received).concat(String.valueOf(readQRLeaveResult.getSuccess().getEarnedAmount()).concat(" coins!"));
                    Toast.makeText(mActivity, R.string.qr_coins_received, Toast.LENGTH_SHORT).show();
                    UserFullData userFD = storage.getUserFullData();
                    userFD.setCurrentCurrency(userFD.getCurrentCurrency()+readQRLeaveResult.getSuccess().getEarnedAmount());
                    storage.storeUserFullData(userFD);
                }
            }
        });

        camera = (ScannerLiveView) findViewById(R.id.readQRCodeCamView);

        camera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
                // method is called when scanner is started
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {
                // method is called when scanner is stoped.
            }

            @Override
            public void onScannerError(Throwable err) {
                // method is called when scanner gives some error.
            }

            @Override
            public void onCodeScanned(String data) {
                // method is called when camera scans the
                // qr code and the data from qr code is
                // stored in data in string format.
                UserAuthenticated user= storage.getLoggedInUser();
                if(!qrReadSwitch.isChecked()){
                    viewModel.sendParticipantQRCode(new SendQRCodeData(user.getEmail(),user.getTokenID(),data));
                }else{
                    viewModel.sendLeaveQRCode(new SendQRCodeData(user.getEmail(),user.getTokenID(),data));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ZXDecoder decoder = new ZXDecoder();
        // 0.5 is the area where we have
        // to place red marker for scanning.
        decoder.setScanAreaPercent(0.8);
        // below method will set secoder to camera.
        camera.setDecoder(decoder);
        camera.startScanner();
    }

    @Override
    protected void onPause() {
        // on app pause the
        // camera will stop scanning.
        camera.stopScanner();
        super.onPause();
    }

    private boolean checkPermission() {
        // here we are checking two permission that is vibrate
        // and camera which is granted by user and not.
        // if permission is granted then we are returning
        // true otherwise false.
        int camera_permission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int vibrate_permission = ContextCompat.checkSelfPermission(getApplicationContext(), VIBRATE);
        return camera_permission == PackageManager.PERMISSION_GRANTED && vibrate_permission == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        // this method is to request
        // the runtime permission.
        int PERMISSION_REQUEST_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, VIBRATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // this method is called when user
        // allows the permission to use camera.
        if (grantResults.length > 0) {
            boolean cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean vibrateaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (cameraaccepted && vibrateaccepted) {
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied \n You cannot use this functionality without providing permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}