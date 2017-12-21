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

import daniel.cn.dhimagekitandroid.DHFilters.DHImageEditComponent;
import daniel.cn.dhimagekitandroid.DHFilters.DHImageEditor;
import daniel.cn.dhimagekitandroid.DHFilters.componentfilters.DHImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener, View.OnClickListener{

    private GPUImageView gpuImageView;
    private ArrayList<String> filterNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = (GridView)findViewById(R.id.gridView);
        String []from = {"name"};
        int []to = {R.id.componentNameText};
        SimpleAdapter adapter = new SimpleAdapter(this, DHImageEditComponent.componentNames(), R.layout.component_item, from, to);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        gpuImageView = (GPUImageView)findViewById(R.id.imageView);
        DHImageEditor.sharedEditor().initializeEditor(loadImage(), gpuImageView, null);

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
        DHImageEditComponent component = DHImageEditComponent.allComponents().get(i);
        DHImageEditor.sharedEditor().startProcessing(component);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setProgress(50);
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, DHImageActivity.class));
    }
}
