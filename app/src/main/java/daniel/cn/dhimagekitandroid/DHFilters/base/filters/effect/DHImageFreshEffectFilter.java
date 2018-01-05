package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageContrastBrightnessFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/5.
 */

public class DHImageFreshEffectFilter extends DHImageEffectFilter {
    private DHImageToneCurveFilter toneCurveFilter;
    private DHImageContrastBrightnessFilter contrastBrightnessFilter;

    public DHImageFreshEffectFilter(Context context) {
        toneCurveFilter = new DHImageToneCurveFilter(context.getResources().openRawResource(R.raw.fresh));
        addFilter(toneCurveFilter);

        contrastBrightnessFilter = new DHImageContrastBrightnessFilter();
        contrastBrightnessFilter.setBrightness(-0.05f);
        addFilter(contrastBrightnessFilter);
        toneCurveFilter.addTarget(contrastBrightnessFilter);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(toneCurveFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(contrastBrightnessFilter);
    }
}
