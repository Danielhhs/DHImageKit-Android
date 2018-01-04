package daniel.cn.dhimagekitandroid.DHFilters.base.filters.base;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageUpdatable;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImageOutput;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageInput;

/**
 * Created by huanghongsen on 2017/12/26.
 */

public abstract class DHImageFilterBase extends DHImageOutput implements IDHImageInput, IDHImageUpdatable {
    public DHImageFilterType getType() {
        return DHImageFilterType.None;
    }
}
