package daniel.cn.dhimagekitandroid.DHFilters.base.filters;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageBrightnessFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageContrastFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageSaturationFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;

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
        filters.add(DHImageFilterType.Saturation);

        return filters;
    }

    public static DHImageFilter filterForType(DHImageFilterType filterType) {
        DHImageFilterParameters parameters = DHImageFilterFactory.filterParametersForType(filterType);
        return DHImageFilterFactory.filterForType(filterType, parameters);
    }

    public static DHImageFilter filterForType(DHImageFilterType filterType, DHImageFilterParameters parameters) {
        switch (filterType) {
            case Brightness: return new DHImageBrightnessFilter(parameters);
            case Contrast: return new DHImageContrastFilter(parameters);
            case Saturation: return new DHImageSaturationFilter(parameters);
        }
        return null;
    }

    public static DHImageFilterParameters filterParametersForType(DHImageFilterType filterType) {
        switch (filterType) {
            case Brightness: return new DHImageFilterParameters(-1.f, 1.f, 0.f);
            case Contrast: return new DHImageFilterParameters(0.7f, 1.3f, 1.f);
            case Saturation: return new DHImageFilterParameters(0.5f, 1.5f, 1.f);
        }
        return null;
    }

}
