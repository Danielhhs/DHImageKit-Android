package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageEffectType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterGroup;

/**
 * Created by huanghongsen on 2017/12/27.
 */

public class DHImageEffectFilter extends DHImageFilterGroup {
    protected float strength;
    public DHImageEffectType getEffectType() {
        return DHImageEffectType.None;
    }

    @Override
    public void updateWithStrength(float strength) {
        super.updateWithStrength(strength);
        this.strength = strength;
    }

    public float getCurrentValue() {
        return strength;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DHImageEffectFilter) {
            DHImageEffectFilter effectFilter = (DHImageEffectFilter)obj;
            return effectFilter.getType().equals(this.getType());
        }
        return false;
    }
}
