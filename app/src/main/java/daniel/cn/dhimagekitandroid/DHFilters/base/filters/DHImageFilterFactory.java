package daniel.cn.dhimagekitandroid.DHFilters.base.filters;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;

/**
 * Created by huanghongsen on 2017/12/26.
 */

public class DHImageFilterFactory {

    private static List<DHImageFilterType> availableFilters;

    public static List<DHImageFilterType> availableFilters() {
        if (availableFilters != null) {
            return availableFilters;
        }
        ArrayList<DHImageFilterType> filters = new ArrayList<>();

        filters.add(DHImageFilterType.Brightness);
        filters.add(DHImageFilterType.Contrast);

        return filters;
    }

    public static DHImageFilter filterForType(DHImageFilterType filterType) {
        switch (filterType) {
            case Brightness: return new DHImageBrightnessFilter();
            case Contrast: return new DHImageContrastFilter();
        }
        return null;
    }
}
