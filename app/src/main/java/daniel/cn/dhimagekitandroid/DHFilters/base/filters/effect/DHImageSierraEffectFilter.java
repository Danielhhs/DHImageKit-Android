package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageColorFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageContrastBrightnessFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageStructureFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageVignetteFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector3;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageSierraEffectFilter extends DHImageEffectFilter {
    private DHImageToneCurveFilter toneCurveFilter;
    private DHImageStructureFilter levelsFilter;
    private DHImageContrastBrightnessFilter contrastBrightnessFilter;
    private DHImageColorFilter colorFilter;
    private DHImageVignetteFilter vignetteFilter;

    public DHImageSierraEffectFilter(Context context) {
        toneCurveFilter = new DHImageToneCurveFilter(context.getResources().openRawResource(R.raw.sierra_curve));
        addFilter(toneCurveFilter);

        levelsFilter = new DHImageStructureFilter();
        levelsFilter.setValues(20.f / 255.f, 1.f, 1.f);
        toneCurveFilter.addTarget(levelsFilter);
        addFilter(levelsFilter);

        contrastBrightnessFilter = new DHImageContrastBrightnessFilter();
        contrastBrightnessFilter.setBrightness(-0.02f);
        contrastBrightnessFilter.setContrast(1.25f);
        levelsFilter.addTarget(contrastBrightnessFilter);
        addFilter(contrastBrightnessFilter);

        colorFilter = new DHImageColorFilter();
        colorFilter.setColor(new DHVector3(0.97f, 0.93f, 0.96f));
        contrastBrightnessFilter.addTarget(colorFilter);
        addFilter(colorFilter);

        vignetteFilter = new DHImageVignetteFilter();
        vignetteFilter.setVignetteStart(0.35f);
        vignetteFilter.setVignetteColor(new DHVector3(0.15f, 0.15f, 0.15f));
        addFilter(vignetteFilter);
        colorFilter.addTarget(vignetteFilter);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(toneCurveFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(vignetteFilter);
    }
}
