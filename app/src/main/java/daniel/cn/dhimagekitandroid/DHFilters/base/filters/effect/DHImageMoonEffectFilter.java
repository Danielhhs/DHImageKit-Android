package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageContrastBrightnessFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageFalseColorFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector3;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/5.
 */

public class DHImageMoonEffectFilter extends DHImageEffectFilter {
    private DHImageFalseColorFilter falseColorFilter;
    private DHImageToneCurveFilter toneCurveFilter;
    private DHImageContrastBrightnessFilter contrastBrightnessFilter;

    public DHImageMoonEffectFilter(Context context) {
        falseColorFilter = new DHImageFalseColorFilter();
        falseColorFilter.setFirstColor(new DHVector3(0.098f, 0.098f, 0.098f));
        falseColorFilter.setSecondColor(new DHVector3(1.f, 1.f, 1.f));

        addFilter(falseColorFilter);

        toneCurveFilter = new DHImageToneCurveFilter(context.getResources().openRawResource(R.raw.moon_curve));
        addFilter(toneCurveFilter);

        falseColorFilter.addTarget(toneCurveFilter);

        contrastBrightnessFilter = new DHImageContrastBrightnessFilter();
        contrastBrightnessFilter.setContrast(1.2f);
        addFilter(contrastBrightnessFilter);
        toneCurveFilter.addTarget(contrastBrightnessFilter);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(falseColorFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(contrastBrightnessFilter);
    }
}
