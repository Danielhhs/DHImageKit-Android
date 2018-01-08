package daniel.cn.dhimagekitandroid.DHFilters.base.filters;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageEffectType;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageBrightnessFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageColorFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageContrastFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageFalseColorFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageGaussianBlurFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageHazeFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageHighlightFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageLinearTiltShiftFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageRadialTiltShiftFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageSaturationFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageShadowFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageSharpenFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageStructureFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageTransformFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageVignetteFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageWarmthFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageAmaroEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageBrannanEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageClarendonEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageCremaEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageFreshEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageGringhamEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageJunoEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageLarkEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageMoonEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageNashvilleEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageNormalEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageRiseEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageSierraEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;

/**
 * Created by huanghongsen on 2017/12/26.
 */

public class DHImageFilterFactory {

    private static List<DHImageFilterType> availableFilters;
    private static List<DHImageEffectType> availableEffects;

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
        filters.add(DHImageFilterType.Shadow);
        filters.add(DHImageFilterType.Highlight);
        filters.add(DHImageFilterType.GaussianBlur);
        filters.add(DHImageFilterType.LinearTiltShift);
        filters.add(DHImageFilterType.RadialTiltShift);
        filters.add(DHImageFilterType.Transform);
        filters.add(DHImageFilterType.Colors);
        filters.add(DHImageFilterType.ToneCurve);
        filters.add(DHImageFilterType.FalseColor);

        return filters;
    }

    public static List<DHImageEffectType> availableEffects() {
        if (availableEffects != null) {
            return availableEffects;
        }
        List<DHImageEffectType> effects = new ArrayList<>();
        effects.add(DHImageEffectType.Normal);
        effects.add(DHImageEffectType.Moon);
        effects.add(DHImageEffectType.Fresh);
        effects.add(DHImageEffectType.Brannan);
        effects.add(DHImageEffectType.Rise);
        effects.add(DHImageEffectType.Gringham);
        effects.add(DHImageEffectType.Sierra);
        effects.add(DHImageEffectType.Crema);
        effects.add(DHImageEffectType.Lark);
        effects.add(DHImageEffectType.Nashville);
        effects.add(DHImageEffectType.Clarendon);
        effects.add(DHImageEffectType.Juno);
        effects.add(DHImageEffectType.Amaro);

        return effects;
    }

    public static DHImageFilterBase filterForType(DHImageFilterType filterType) {
        DHImageFilterParameters parameters = DHImageFilterFactory.filterParametersForType(filterType);
        return DHImageFilterFactory.filterForType(filterType, parameters);
    }

    public static DHImageFilterBase filterForType(DHImageFilterType filterType, DHImageFilterParameters parameters) {
        switch (filterType) {
            case Brightness: return new DHImageBrightnessFilter(parameters);
            case Contrast: return new DHImageContrastFilter(parameters);
            case Saturation: return new DHImageSaturationFilter(parameters);
            case Warmth: return new DHImageWarmthFilter(parameters);
            case Structure: return new DHImageStructureFilter(parameters);
            case Vignette: return new DHImageVignetteFilter(parameters);
            case Fade: return new DHImageHazeFilter(parameters);
            case Sharpen: return new DHImageSharpenFilter(parameters);
            case Shadow: return new DHImageShadowFilter(parameters);
            case Highlight: return new DHImageHighlightFilter(parameters);
            case GaussianBlur: return new DHImageGaussianBlurFilter(parameters);
            case LinearTiltShift: return new DHImageLinearTiltShiftFilter(parameters);
            case RadialTiltShift: return new DHImageRadialTiltShiftFilter(parameters);
            case Transform: return new DHImageTransformFilter(parameters);
            case Colors: return new DHImageColorFilter(parameters);
            case ToneCurve: return new DHImageToneCurveFilter(parameters);
            case FalseColor: return new DHImageFalseColorFilter(parameters);
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
            case Shadow: return new DHImageFilterParameters(0.9f, 1.1f, 1.f);
            case Highlight: return new DHImageFilterParameters(0.9f, 1.1f, 1.f);
            case GaussianBlur: return new DHImageFilterParameters(0.f, 30.f, 0.f);
            case LinearTiltShift: return new DHImageFilterParameters(0.1f, 0.9f, 0.5f);
            case RadialTiltShift: return new DHImageFilterParameters(0.f, 0.5f, 0.2f);
            case Transform: return new DHImageFilterParameters(-25.f, 25.f, 0.f);
            case Colors: return new DHImageFilterParameters(0.f, 1.f, 0.5f);
            case ToneCurve: return new DHImageFilterParameters(0.f, 1.f, 1.f);
            case FalseColor: return new DHImageFilterParameters(0.f, 1.f, 0.f);
        }
        return null;
    }

    public static DHImageEffectFilter filterForEffect(DHImageEffectType effectType, Context context) {
        switch (effectType) {
            case None: return new DHImageEffectFilter();
            case Normal: return new DHImageNormalEffectFilter();
            case Moon: return new DHImageMoonEffectFilter(context);
            case Fresh: return new DHImageFreshEffectFilter(context);
            case Brannan: return new DHImageBrannanEffectFilter(context);
            case Rise: return new DHImageRiseEffectFilter(context);
            case Gringham: return new DHImageGringhamEffectFilter(context);
            case Sierra: return new DHImageSierraEffectFilter(context);
            case Crema: return new DHImageCremaEffectFilter(context);
            case Lark: return new DHImageLarkEffectFilter(context);
            case Nashville: return new DHImageNashvilleEffectFilter(context);
            case Clarendon: return new DHImageClarendonEffectFilter(context);
            case Juno: return new DHImageJunoEffectFilter(context);
            case Amaro: return new DHImageAmaroEffectFilter(context);
        }
        return null;
    }

}
