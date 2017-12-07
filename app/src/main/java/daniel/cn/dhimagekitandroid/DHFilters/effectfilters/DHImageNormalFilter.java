package daniel.cn.dhimagekitandroid.DHFilters.effectfilters;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by huanghongsen on 2017/12/6.
 */

public class DHImageNormalFilter extends DHImageFilter {
    private GPUImageFilter filter;
    public DHImageNormalFilter() {
        filter = new GPUImageFilter();
        addFilter(filter);
    }
}
