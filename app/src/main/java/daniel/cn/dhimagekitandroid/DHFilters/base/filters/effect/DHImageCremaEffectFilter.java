package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageContrastBrightnessFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageSaturationFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageCremaEffectFilter extends DHImageEffectFilter {
    private DHImageToneCurveFilter toneCurveFilter;
    private DHImageSaturationFilter saturationFilter;
    private DHImageContrastBrightnessFilter contrastBrightnessFilter;

    public DHImageCremaEffectFilter(Context context) {
        toneCurveFilter = new DHImageToneCurveFilter(context.getResources().openRawResource(R.raw.crema_curve));
        addFilter(toneCurveFilter);

        saturationFilter = new DHImageSaturationFilter();
        saturationFilter.setSaturation(0.618f);
        addFilter(saturationFilter);
        toneCurveFilter.addTarget(saturationFilter);

        contrastBrightnessFilter = new DHImageContrastBrightnessFilter();
        contrastBrightnessFilter.setContrast(1.5f);
        contrastBrightnessFilter.setBrightness(0.06f);
        addFilter(contrastBrightnessFilter);
        saturationFilter.addTarget(contrastBrightnessFilter);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(toneCurveFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(contrastBrightnessFilter);
    }
}
