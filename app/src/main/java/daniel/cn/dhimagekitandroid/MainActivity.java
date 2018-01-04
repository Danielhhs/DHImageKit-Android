package daniel.cn.dhimagekitandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;

import java.util.ArrayList;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageView;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageEditComponent;
import daniel.cn.dhimagekitandroid.DHFilters.DHImageEditor;
import daniel.cn.dhimagekitandroid.DHFilters.base.executors.DHImageVideoProcessExecutor;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.blend.DHImageAlphaBlendFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageSurfaceListener;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class MainActivity extends AppCompatActivity implements IDHImageSurfaceListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener, View.OnClickListener{

    private DHImageView gpuImageView;
    private DHImagePicture picture;
    private DHImagePicture overlayPicture;
    private DHImageAlphaBlendFilter filter;

    private ArrayList<String> filterNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpuImageView = (DHImageView)findViewById(R.id.imageView);
        gpuImageView.setSurfaceListener(this);

        ((SeekBar)findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    private Bitmap loadImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scene);
        return bitmap;
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        float brightness = seekBar.getProgress() / 100.f;
        Log.d("hhs", brightness + "");
        DHImageEditor.sharedEditor().updateWithInput(brightness);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        DHImageEditComponent component = DHImageEditComponent.allComponents().get(i);
//        DHImageEditor.sharedEditor().startProcessing(component);
//        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
//        seekBar.setProgress(50);
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, DHImageActivity.class));
    }

    @Override
    public void onSurfaceTextureAvailable() {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageView imageView = (DHImageView)findViewById(R.id.imageView);
                picture = new DHImagePicture(loadImage());
                DHImageToneCurveFilter toneCurveFilter = new DHImageToneCurveFilter(getApplicationContext().getResources().openRawResource(R.raw.fresh));
                picture.addTarget(toneCurveFilter);
                toneCurveFilter.addTarget(imageView);
                picture.processImage();
            }
        });
    }

    private Bitmap loadOverlayPicture() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.overlay);
        return bitmap;
    }
}
