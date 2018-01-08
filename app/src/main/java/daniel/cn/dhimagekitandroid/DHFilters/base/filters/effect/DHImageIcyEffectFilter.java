package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.blend.DHImageScreenBlendFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.blend.DHImageVignetteBlendFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageWarmthFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageIcyEffectFilter extends DHImageEffectFilter {
    private DHImageWarmthFilter warmthFilter;
    private DHImageVignetteBlendFilter vignetteBlendFilter;
    private DHImageScreenBlendFilter screenBlendFilter;
    private DHImagePicture icePicture;
    private DHImagePicture snowPicture;

    public DHImageIcyEffectFilter(Context context) {
        warmthFilter = new DHImageWarmthFilter();
        warmthFilter.setTemperature(3900.f);
        addFilter(warmthFilter);

        vignetteBlendFilter = new DHImageVignetteBlendFilter();
        icePicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.ice_texture));
        icePicture.addTarget(vignetteBlendFilter, 1);
        icePicture.processImage();;
        vignetteBlendFilter.disableSecondFrameCheck();
        addFilter(vignetteBlendFilter);
        warmthFilter.addTarget(vignetteBlendFilter);

        screenBlendFilter = new DHImageScreenBlendFilter();
        screenBlendFilter.setOpacity(0.5f);
        snowPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.snow_texture));
        snowPicture.addTarget(screenBlendFilter, 1);
        snowPicture.processImage();
        screenBlendFilter.disableSecondFrameCheck();
        vignetteBlendFilter.addTarget(screenBlendFilter);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(warmthFilter);
        setInitialFilters(initialFilters);

        setTerminalFilter(screenBlendFilter);
    }
}
