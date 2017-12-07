package daniel.cn.dhimagekitandroid.DHFilters.componentfilters;

import daniel.cn.dhimagekitandroid.DHFilters.DHImageBaseFilter;
import daniel.cn.dhimagekitandroid.DHFilters.DHImageEditComponent;
import daniel.cn.dhimagekitandroid.DHFilters.DHImageEditComponentSubtype;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by huanghongsen on 2017/12/7.
 */

public final class DHImageComponentFilterFactory {
    public static GPUImageFilter filterForComponent(DHImageEditComponent component) {
        return DHImageComponentFilterFactory.filterForComponentAndSubtype(component, DHImageEditComponentSubtype.NoTiltShift);
    }

    public static DHImageBaseFilter filterForComponentAndSubtype(DHImageEditComponent component, DHImageEditComponentSubtype subtype) {
        switch (component) {
            case Brightness: {
                DHImageBrightnessFilter filter = new DHImageBrightnessFilter();
                return filter;
            }
            case Contrast: {
                DHImageContrastFilter filter = new DHImageContrastFilter();
                return filter;
            }
        }
        DHImageBaseFilter filter = new DHImageBaseFilter();
        return filter;
    }
}
