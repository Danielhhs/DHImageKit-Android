package daniel.cn.dhimagekitandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.nio.IntBuffer;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageSurfaceListener;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImageView;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImageViewRenderer;

public class DHImageActivity extends AppCompatActivity implements IDHImageSurfaceListener {

    DHImagePicture picture;
    DHImageViewRenderer renderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhimage);

        DHImageView imageView = (DHImageView)findViewById(R.id.dhImageView);
        imageView.setSurfaceListener(this);
    }

    private Bitmap loadImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scene);
        return bitmap;
    }

    @Override
    public void onSurfaceTextureAvailable() {
        DHImageView imageView = (DHImageView)findViewById(R.id.dhImageView);
        picture = new DHImagePicture(loadImage());
        picture.addTarget(imageView);

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
}
