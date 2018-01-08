package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageGringhamEffectFilter extends DHImageEffectFilter {
    private DHImageToneCurveFilter toneCurveFilter;

    public DHImageGringhamEffectFilter(Context context) {
        toneCurveFilter = new DHImageToneCurveFilter(context.getResources().openRawResource(R.raw.grinham_curve));
        addFilter(toneCurveFilter);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(toneCurveFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(toneCurveFilter);
    }
}
