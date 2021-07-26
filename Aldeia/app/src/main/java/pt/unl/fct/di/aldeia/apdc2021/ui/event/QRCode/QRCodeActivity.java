package pt.unl.fct.di.aldeia.apdc2021.ui.event.QRCode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;

public class QRCodeActivity extends AppCompatActivity {

    private QRCodeViewModel viewModel;
    private UserLocalStore storage;
    private String eventId;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private QRCodeActivity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        viewModel=new ViewModelProvider(this, new QRCodeViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(QRCodeViewModel.class);
        storage= new UserLocalStore(this);
        eventId=getIntent().getStringExtra("event_id");
        final Button qrParticipationButton = findViewById(R.id.qr_participation_button);
        final Button qrCompletionButton = findViewById(R.id.qr_completion_button);
        final ImageView qrImage = findViewById(R.id.qrCodeImageView);
        UserAuthenticated user=storage.getLoggedInUser();
        bitmap=null;
        mActivity=this;
        qrParticipationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getParticipantQRCode(new GetEventData(user.getEmail(), user.getTokenID(),eventId));
            }
        });
        qrCompletionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getLeaveQRCode(new GetEventData(user.getEmail(), user.getTokenID(),eventId));
            }
        });

        viewModel.getParticipantQRCOdeResult().observe(this, new Observer<QRCodeResult>() {
            @Override
            public void onChanged(QRCodeResult qrCodeResult) {
                if(qrCodeResult==null){
                    return;
                }
                if(qrCodeResult.getError()!=null){
                    Toast.makeText(mActivity, qrCodeResult.getError() ,Toast.LENGTH_SHORT).show();
                }
                if(qrCodeResult.getSuccess()!=null){
                    String QREncoded = qrCodeResult.getSuccess().getQRCode();
                    setQRCode(QREncoded,qrImage);
                }
            }
        });

        viewModel.getLeaveQRCOdeResult().observe(this, new Observer<QRCodeResult>() {
            @Override
            public void onChanged(QRCodeResult qrCodeResult) {
                if(qrCodeResult==null){
                    return;
                }
                if(qrCodeResult.getError()!=null){
                    Toast.makeText(mActivity, qrCodeResult.getError() ,Toast.LENGTH_SHORT).show();
                }
                if(qrCodeResult.getSuccess()!=null){
                    String QREncoded = qrCodeResult.getSuccess().getQRCode();
                    setQRCode(QREncoded,qrImage);
                }
            }
        });




    }

    private void setQRCode(String data,ImageView qrCodeIV){

            // below line is for getting
            // the windowmanager service.
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // initializing a variable for default display.
            Display display = manager.getDefaultDisplay();

            // creating a variable for point which
            // is to be displayed in QR Code.
            Point point = new Point();
            display.getSize(point);

            // getting width and
            // height of a point
            int width = point.x;
            int height = point.y;

            // generating dimension from width and height.
            int dimen = width < height ? width : height;
            dimen = dimen * 3 / 4;

            // setting this dimensions inside our qr code
            // encoder to generate our qr code.
            qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, dimen);
            // getting our qrcode in the form of bitmap.
            try {
                // Getting QR-Code as Bitmap
                Bitmap map =null;
                 map = qrgEncoder.getBitmap();
                // Setting Bitmap to ImageView
                qrCodeIV.setImageBitmap(map);
            }catch (Exception e) {
                Log.v("Bitmap Converter", e.toString());
             }

        }



}