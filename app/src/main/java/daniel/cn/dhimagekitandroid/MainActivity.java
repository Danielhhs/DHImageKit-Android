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
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageView;
import daniel.cn.dhimagekitandroid.DHFilters.DHImageEditor;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageEffectType;
import daniel.cn.dhimagekitandroid.DHFilters.base.executors.DHImageVideoProcessExecutor;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.DHImageFilterFactory;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.blend.DHImageAlphaBlendFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageMoonEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageSurfaceListener;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;

public class MainActivity extends AppCompatActivity implements IDHImageSurfaceListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener, View.OnClickListener{

    private DHImageView gpuImageView;
    private DHImagePicture picture;
    private List<DHImageEffectType> effectTypes;

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

        ListView listView = (ListView)findViewById(R.id.effectListView);
        effectTypes = DHImageFilterFactory.availableEffects();
        EffectAdapter adapter = new EffectAdapter(this, effectTypes);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
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
        final float percent = seekBar.getProgress() / 100.f;
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageEditor.sharedEditor().updateWithStrength(percent);
            }
        });
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final DHImageEffectType effectType = DHImageFilterFactory.availableEffects().get(i);
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageEditor.sharedEditor().startProcessing(effectType, getApplicationContext());
            }
        });
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
                DHImageEditor.sharedEditor().initializeEditor(getApplicationContext(), loadImage(), imageView, null);
            }
        });
    }

    private Bitmap loadOverlayPicture() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.overlay);
        return bitmap;
    }
}
