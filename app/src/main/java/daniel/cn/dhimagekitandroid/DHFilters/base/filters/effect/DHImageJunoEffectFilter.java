package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageVignetteFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector3;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageJunoEffectFilter extends DHImageEffectFilter {
    private DHImageToneCurveFilter toneCurveFilter;
    private DHImageVignetteFilter vignetteFilter;

    public DHImageJunoEffectFilter(Context context) {

        vignetteFilter = new DHImageVignetteFilter();
        vignetteFilter.setVignetteColor(new DHVector3(0.3f, 0.3f, 0.3f));
        vignetteFilter.setVignetteStart(0.5f);
        addFilter(vignetteFilter);

        toneCurveFilter = new DHImageToneCurveFilter(context.getResources().openRawResource(R.raw.juno_curve));
        addFilter(toneCurveFilter);

        vignetteFilter.addTarget(toneCurveFilter);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(vignetteFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(toneCurveFilter);
    }
}
