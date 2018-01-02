package daniel.cn.dhimagekitandroid.DHFilters.base.filters;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageBrightnessFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageContrastFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageHazeFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageSaturationFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageSharpenFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageStructureFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageVignetteFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageWarmthFilter;
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
        filters.add(DHImageFilterType.Warmth);
        filters.add(DHImageFilterType.Structure);
        filters.add(DHImageFilterType.Vignette);
        filters.add(DHImageFilterType.Fade);
        filters.add(DHImageFilterType.Sharpen);

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
            case Warmth: return new DHImageWarmthFilter(parameters);
            case Structure: return new DHImageStructureFilter(parameters);
            case Vignette: return new DHImageVignetteFilter(parameters);
            case Fade: return new DHImageHazeFilter(parameters);
            case Sharpen: return new DHImageSharpenFilter(parameters);
        }
        return null;
    }

    public static DHImageFilterParameters filterParametersForType(DHImageFilterType filterType) {
        switch (filterType) {
            case Brightness: return new DHImageFilterParameters(-1.f, 1.f, 0.f);
            case Contrast: return new DHImageFilterParameters(0.7f, 1.3f, 1.f);
            case Saturation: return new DHImageFilterParameters(0.5f, 1.5f, 1.f);
            case Warmth: return new DHImageFilterParameters(3500.f, 6500.f, 5000.f);
            case Structure: return new DHImageFilterParameters(0.f, 0.25f, 0.f);
            case Vignette: return new DHImageFilterParameters(0.75f, 1.5f, 0.75f);
            case Fade: return new DHImageFilterParameters(-0.2f, 0.f, 0.f);
            case Sharpen: return new DHImageFilterParameters(0.f, 4.f, 0.f);
        }
        return null;
    }

}
