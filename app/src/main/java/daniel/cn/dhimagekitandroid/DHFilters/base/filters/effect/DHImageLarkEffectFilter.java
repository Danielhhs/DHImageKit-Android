package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageContrastBrightnessFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageLarkEffectFilter extends DHImageEffectFilter {
    private DHImageToneCurveFilter toneCurveFilter;
    private DHImageContrastBrightnessFilter contrastBrightnessFilter;

    public DHImageLarkEffectFilter(Context context) {
        toneCurveFilter = new DHImageToneCurveFilter(context.getResources().openRawResource(R.raw.lark_curve));
        addFilter(toneCurveFilter);

        contrastBrightnessFilter = new DHImageContrastBrightnessFilter();
        contrastBrightnessFilter.setBrightness(0.0618f);
        addFilter(contrastBrightnessFilter);
        toneCurveFilter.addTarget(contrastBrightnessFilter);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(toneCurveFilter);
        setInitialFilters(initialFilters);

        setTerminalFilter(contrastBrightnessFilter);
    }
}
