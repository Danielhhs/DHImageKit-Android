package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageEffectType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;

/**
 * Created by huanghongsen on 2017/12/27.
 */

public class DHImageNormalEffectFilter extends DHImageEffectFilter {

    public DHImageNormalEffectFilter() {
        DHImageFilter filter = new DHImageFilter();
        addFilter(filter);
        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(filter);
        setInitialFilters(initialFilters);
        setTerminalFilter(filter);
    }

    @Override
    public DHImageEffectType getEffectType() {
        return DHImageEffectType.Normal;
    }
}
