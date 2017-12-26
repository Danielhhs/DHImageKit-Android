package daniel.cn.dhimagekitandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.DHImageBrightnessFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.DHImageContrastFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageSurfaceListener;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageView;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageViewRenderer;

public class DHImageActivity extends AppCompatActivity implements IDHImageSurfaceListener, SeekBar.OnSeekBarChangeListener {

    DHImagePicture picture;
    DHImageViewRenderer renderer;

    DHImageFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhimage);

        DHImageView imageView = (DHImageView)findViewById(R.id.dhImageView);
        imageView.setSurfaceListener(this);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar2);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private Bitmap loadImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scene);
        return bitmap;
    }

    @Override
    public void onSurfaceTextureAvailable() {
        DHImageView imageView = (DHImageView)findViewById(R.id.dhImageView);
        picture = new DHImagePicture(loadImage());

        DHImageContrastFilter filter = new DHImageContrastFilter();
        picture.addTarget(filter);
        filter.addTarget(imageView);
        this.filter = filter;

        picture.processImage();
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
        float percent = seekBar.getProgress() / 100.f;
        filter.updateWithPercent(percent);
        picture.processImage();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
