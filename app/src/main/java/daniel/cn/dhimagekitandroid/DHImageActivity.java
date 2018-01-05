package daniel.cn.dhimagekitandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.DHImageEditor;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.executors.DHImageVideoProcessExecutor;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.DHImageFilterFactory;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterGroup;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageSurfaceListener;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageView;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageViewRenderer;

public class DHImageActivity extends AppCompatActivity implements IDHImageSurfaceListener, SeekBar.OnSeekBarChangeListener, ListView.OnItemClickListener {

    DHImagePicture picture;
    DHImageViewRenderer renderer;
    List<DHImageFilterType> filterTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhimage);

        DHImageView imageView = (DHImageView)findViewById(R.id.dhImageView);
        imageView.setSurfaceListener(this);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar2);
        seekBar.setOnSeekBarChangeListener(this);

        ListView listView = (ListView)findViewById(R.id.listView);
        filterTypes = DHImageFilterFactory.availableFilters();
        FilterAdapter adapter = new FilterAdapter(this, filterTypes);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

    }

    private Bitmap loadImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scene);
        return bitmap;
    }

    @Override
    public void onSurfaceTextureAvailable() {
        DHImageView imageView = (DHImageView)findViewById(R.id.dhImageView);
        DHImageEditor.sharedEditor().initializeEditor(getApplicationContext(), loadImage(), imageView, null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        DHImageView imageView = (DHImageView)findViewById(R.id.dhImageView);
//        imageView.setBackgroundColor(Color.parseColor("#F5F5DC"));
        int width = imageView.getWidth();
        int height = imageView.getHeight();

        Log.e("sdafsf", width + ", " + height);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        final float percent = seekBar.getProgress() / 100.f;
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageEditor.sharedEditor().updateWithInput(percent);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final DHImageView imageView = (DHImageView)findViewById(R.id.dhImageView);
        final DHImageFilterType filterType = filterTypes.get(position);
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
               DHImageEditor.sharedEditor().startProcessing(filterType);
            }
        });
    }
}
