package daniel.cn.dhimagekitandroid.DHFilters.base.filters;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLSurface;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageSurfaceTexture;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageInput;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageUpdatable;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImageOutput;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/26.
 */

public class DHImageFilterGroup extends DHImageFilterBase {

    private DHImageFilterBase terminalFilter;
    private List<DHImageFilterBase> initialFilters;

    private List<DHImageFilterBase> filters;
    private DHImageFilterBase inputFilterToIgnoreForUpdates;
    private boolean isEndProcessing;

    public DHImageFilterGroup() {
        filters = new ArrayList<>();
    }

    public void addFilter(DHImageFilterBase filter) {
        filters.add(filter);
    }

    public int filterCount() {
        return filters.size();
    }

    public DHImageFilterBase filterAtIndex(int index) {
        return filters.get(index);
    }

    //IDHImageUpdatable
    @Override
    public void updateWithStrength(float strength) {
        for (DHImageFilterBase filter : filters) {
            filter.updateWithStrength(strength);
        }
    }

    @Override
    public void updateWithInput(float input) {
        for (DHImageFilterBase filter : filters) {
            filter.updateWithInput(input);
        }
    }

    //DHImageOutput
    @Override
    public void useNextFrameForImageCapture() {
        if (terminalFilter == null) return;
        terminalFilter.useNextFrameForImageCapture();
    }

    @Override
    public Bitmap newImageFromCurrentlyProcessedOutput() {
        if (terminalFilter == null) return null;
        return terminalFilter.newImageFromCurrentlyProcessedOutput();
    }

    @Override
    public void setTargetToIgnoreForUpdates(IDHImageInput targetToIgnoreForUpdates) {
        if (terminalFilter == null) return;
        terminalFilter.setTargetToIgnoreForUpdates(targetToIgnoreForUpdates);
    }

    @Override
    public void addTarget(IDHImageInput target, int location) {
        if (terminalFilter == null) return;
        terminalFilter.addTarget(target, location);
    }

    @Override
    public void removeTarget(IDHImageInput target) {
        if (terminalFilter == null) return;
        terminalFilter.removeTarget(target);
    }

    @Override
    public void removeAllTargets() {
        if (terminalFilter == null) return;
        terminalFilter.removeAllTargets();
    }

    @Override
    public List<IDHImageInput> getTargets() {
        if (terminalFilter == null) return null;
        return terminalFilter.getTargets();
    }

    //IDHImageInput
    @Override
    public void newFrameReady(float time, int index) {
        if (initialFilters == null) return;
        for (DHImageFilterBase filter : initialFilters) {
            if (!filter.equals(inputFilterToIgnoreForUpdates)) {
                filter.newFrameReady(time, index);
            }
        }
    }

    @Override
    public void setInputSurfaceTexture(EGLSurface inputSurface, DHImageSurfaceTexture surfaceTexture, int index) {
        if (initialFilters == null) return;
        for (DHImageFilterBase filter : initialFilters) {
            filter.setInputSurfaceTexture(inputSurface, surfaceTexture, index);
        }
    }

    @Override
    public int nextAvailableTextureIndex() {
        return 0;
    }

    @Override
    public void setInputSize(DHImageSize size, int index) {
        if (initialFilters == null) return;
        for (DHImageFilterBase filter : initialFilters) {
            filter.setInputSize(size, index);
        }
    }

    @Override
    public void setInputRotation(DHImageRotationMode rotationMode, int index) {
        if (initialFilters == null) return;
        for (DHImageFilterBase filter : initialFilters) {
            filter.setInputRotation(rotationMode, index);
        }
    }

    @Override
    public DHImageSize maximumOutputSize() {
        return DHImageSize.zeroSize();
    }

    @Override
    public void endProcessing() {
        isEndProcessing = true;
        if (initialFilters == null) return;
        for (DHImageFilterBase filter : initialFilters) {
            filter.endProcessing();
        }
    }

    @Override
    public boolean shouldIgnoreUpdatesToThisTarget() {
        return false;
    }

    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public boolean wantsMonochromeInput() {
        if (initialFilters == null) return true;
        boolean allWantsMonochomeInput = true;
        for (DHImageFilterBase filter : initialFilters) {
            allWantsMonochomeInput = allWantsMonochomeInput & filter.wantsMonochromeInput();
        }
        return allWantsMonochomeInput;
    }

    @Override
    public void setCurrentlyReceivingMonochromeInput(boolean newValue) {
        if (initialFilters == null) return;
        for (DHImageFilterBase filter : initialFilters) {
            filter.setCurrentlyReceivingMonochromeInput(newValue);
        }
    }

    //Getters & Setters
    public DHImageFilterBase getTerminalFilter() {
        return terminalFilter;
    }

    public void setTerminalFilter(DHImageFilterBase terminalFilter) {
        this.terminalFilter = terminalFilter;
    }

    public List<DHImageFilterBase> getInitialFilters() {
        return initialFilters;
    }

    public void setInitialFilters(List<DHImageFilterBase> initialFilters) {
        this.initialFilters = initialFilters;
    }

    public DHImageFilterBase getInputFilterToIgnoreForUpdates() {
        return inputFilterToIgnoreForUpdates;
    }

    public void setInputFilterToIgnoreForUpdates(DHImageFilterBase inputFilterToIgnoreForUpdates) {
        this.inputFilterToIgnoreForUpdates = inputFilterToIgnoreForUpdates;
    }
}
