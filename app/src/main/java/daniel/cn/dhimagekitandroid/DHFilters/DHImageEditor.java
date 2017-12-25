package daniel.cn.dhimagekitandroid.DHFilters;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.effectfilters.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.effectfilters.DHImageNormalFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by huanghongsen on 2017/12/6.
 */
public class DHImageEditor {

    private GPUImageFilterGroup filterGroup;
    private GPUImageView renderTarget;
    private DHImageNormalFilter normalFilter;
    private DHImageFilter dhFilter;
    private DHImageEditComponent currentComponent;
    private DHImageEditComponentSubtype currentSubtype;
    private GPUImageFilter currentFilter;
    private Bitmap sourceImage;
    private List<GPUImageFilter> filters;

    //Initialization
    public void initializeEditor(Bitmap image, GPUImageView target, IDHImageEditorCallBack callback) {
        filters = new ArrayList<>();
        sourceImage = image;
        filterGroup = new GPUImageFilterGroup();
        renderTarget = target;
        normalFilter = new DHImageNormalFilter();
        renderTarget.setImage(image);
        renderTarget.setFilter(filterGroup);
        if (callback != null) {
            callback.initCallBack();
        }
    }

    public void initializeEditor(Uri uri, GPUImageView target, IDHImageEditorCallBack callback) {

    }

    //ImageProcessing
    public void startProcessing(DHImageFilter filter) {
        dhFilter = filter;
        addFilter(filter);
        renderTarget.requestRender();
    }

    public void startProcessing(DHImageEditComponent component) {
        startProcessing(component, DHImageEditComponentSubtype.NoTiltShift);
    }

    public void startProcessing(DHImageEditComponent component, DHImageEditComponentSubtype subtype) {
        currentComponent = component;
        currentSubtype = subtype;

        renderTarget.requestRender();
    }

    //UpdateFilter
    public void updateWithInput(float input) {
        if (currentFilter instanceof IDHImageUpdatable) {
            IDHImageUpdatable updatable = (IDHImageUpdatable)currentFilter;
            updatable.updateWithInput(input);
        }
        renderTarget.requestRender();
    }

    //PRIVATE HELPERS
    private void addFilter(GPUImageFilter filter) {
        currentFilter = filter;
        filters.add(filter);
        filterGroup = new GPUImageFilterGroup(filters);
        renderTarget.setImage(sourceImage);
        renderTarget.setFilter(filterGroup);
    }

    //SINGLETON
    private static class DHImageEditorSingletonHolder {
        private static final DHImageEditor SINGLETON = new DHImageEditor();
    }

    private DHImageEditor() {}

    public static final DHImageEditor sharedEditor() {
        return DHImageEditorSingletonHolder.SINGLETON;
    }
}
