package daniel.cn.dhimagekitandroid.DHFilters.base.interfaces;

import android.graphics.Bitmap;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public interface IDHImagePictureCallback {
    public void onImageProcessFinished();
    public void onImageProcessFinished(Bitmap bitmap);
}
