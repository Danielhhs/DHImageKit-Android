package daniel.cn.dhimagekitandroid.DHFilters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageView;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageEditComponent;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageEffectType;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.DHImageFilterFactory;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterGroup;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageToneCurveFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect.DHImageNormalEffectFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageUpdatable;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageValues;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;

/**
 * Created by huanghongsen on 2017/12/6.
 */
public class DHImageEditor {

    private DHImagePicture picture;
    private DHImageFilterGroup filterGroup;
    private DHImageView renderTarget;
    private DHImageEffectFilter effectFilter;
    private DHImageFilterType currentComponent;
    private DHImageFilterBase currentFilter;
    private List<DHImageFilterBase> filters;

    //Initialization
    public void initializeEditor(final Context context, final Bitmap image, DHImageView target, final IDHImageEditorCallBack callback) {
        filters = new ArrayList<>();
        renderTarget = target;

        picture = new DHImagePicture(image);
        filterGroup = new DHImageFilterGroup();
        startProcessing(DHImageEffectType.Normal, context);

        //TO-DO: Call on calling looper
        if (callback != null) {
            callback.initCallBack();
        }
    }

    public void initializeEditor(Uri uri, DHImageView target, IDHImageEditorCallBack callback) {

    }

    //ImageProcessing
    public void startProcessing(final DHImageEffectType effectType, Context context) {
        DHImageEffectFilter filter = DHImageFilterFactory.filterForEffect(effectType, context);
                if (effectFilter == null) {
                    addFilter(filter);
                } else {
                    replaceEffectFilterWithFilter(effectFilter, filter);
                }
                effectFilter = filter;
                processImage();
    }

    public void startProcessing(final DHImageFilterType component) {
                currentComponent = component;

                DHImageFilterBase existingFilter = findExistingFilter(component);
                if (existingFilter == null) {
                    DHImageFilterBase componentFilter = DHImageFilterFactory.filterForType(component);
                    addFilter(componentFilter);
                    currentFilter = componentFilter;
                } else {
                    currentFilter = existingFilter;
                }
                processImage();
    }

    //UpdateFilter
    public void updateWithInput(float input) {
        if (currentFilter instanceof IDHImageValues) {
            IDHImageValues updatable = (IDHImageValues)currentFilter;
            updatable.updateWithPercent(input);
        } else if (currentFilter instanceof  IDHImageUpdatable) {
            IDHImageUpdatable updatable = (IDHImageUpdatable) currentFilter;
            updatable.updateWithStrength(input);
        }
        processImage();
    }

    public void updateWithStrength(float strength) {
        if (effectFilter != null) {
            effectFilter.updateWithStrength(strength);
            processImage();
        }
    }

    //PRIVATE HELPERS
    private void addFilter(DHImageFilterBase filter) {
        filterGroup.removeAllTargets();
        filterGroup.addFilter(filter);
        if (filterGroup.filterCount() == 1) {
            List<DHImageFilterBase> initialFilters = new ArrayList<>();
            initialFilters.add(filter);
            filterGroup.setInitialFilters(initialFilters);
            filterGroup.setTerminalFilter(filter);
        } else {
            DHImageFilterBase currentTerminal = filterGroup.getTerminalFilter();
            List<DHImageFilterBase> currentInitials = filterGroup.getInitialFilters();
            currentTerminal.addTarget(filter);
            filterGroup.setTerminalFilter(filter);
            List<DHImageFilterBase> initials = new ArrayList<>();
            initials.add(currentInitials.get(0));
            filterGroup.setInitialFilters(initials);
        }
        filters.add(filter);
    }

    private void replaceEffectFilterWithFilter(DHImageFilterBase filterToRemove, DHImageFilterBase newFilter) {
        DHImageFilterGroup newFilterGroup = new DHImageFilterGroup();
        DHImageFilterGroup previousFilterGroup = filterGroup;
        filterGroup = newFilterGroup;
        for (int i = 0; i < previousFilterGroup.filterCount(); i++) {
            DHImageFilterBase filterInGroup = previousFilterGroup.filterAtIndex(i);
            if (!filterInGroup.equals(filterToRemove)) {
                addFilter(filterInGroup);
            } else {
                addFilter(newFilter);
            }
        }
        previousFilterGroup.removeAllTargets();
        previousFilterGroup = null;
    }

    private void processImage() {
        picture.removeAllTargets();
        filterGroup.removeAllTargets();
        if (filterGroup.filterCount() == 0) {
            picture.addTarget(renderTarget);
        } else {
            picture.addTarget(filterGroup);
            filterGroup.addTarget(renderTarget);
        }
        picture.processImage();
    }

    private DHImageFilterBase findExistingFilter(DHImageFilterType component) {
        if (filters == null) { return null;}
        for (DHImageFilterBase aFilter : filters) {
            if (aFilter instanceof DHImageFilterBase) {
                DHImageFilterBase filter = (DHImageFilterBase)aFilter;
                if (((DHImageFilterBase) aFilter).getType().equals(component)) {
                    return aFilter;
                }
            }
        }
        return null;
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
