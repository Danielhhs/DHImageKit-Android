package daniel.cn.dhimagekitandroid.DHFilters.base.output;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLSurface;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageFrameBuffer;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageSurfaceTexture;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageTextureOptions;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageInput;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHImageOutput {
//    protected EGLSurface mSurface;
    protected DHImageFrameBuffer outputFrameBuffer;
    protected List<IDHImageInput> targets;
    protected List<Integer> targetTextureIndices;
    protected boolean usingNextFrameForImageCapture;
    protected DHImageSize inputTextureSize;
    protected boolean overrideInputSize;
    protected DHImageSize forcedMaximumSize;

    private DHImageSize cachedMaximumOutputSize;
    private boolean allTargetsWantMonochromeData;

    private boolean shouldSmoothlyScaleOutput;
    private boolean shouldIgnoreUpdatesToThisTarget;
    private boolean enabled;
    private IDHImageInput targetToIgnoreForUpdates;
    private DHImageTextureOptions outputTextureOptions;

    public DHImageOutput() {
        targets = new ArrayList<>();
        targetTextureIndices = new ArrayList<>();
        enabled = true;
        usingNextFrameForImageCapture = false;
        allTargetsWantMonochromeData = true;
        outputTextureOptions = new DHImageTextureOptions();
    }

    public void setInputSurfaceTextureForTarget(IDHImageInput target, int inputTextureIndex) {
        target.setInputFrame(frameBufferForOutput(), inputTextureIndex);
    }

    public DHImageFrameBuffer frameBufferForOutput() {
        return outputFrameBuffer;
    }

    public void removeOutputFrameBuffer() {
        outputFrameBuffer = null;
    }

    public void notifyTargetsAboutNewOutputTexture() {
        for (IDHImageInput target : targets) {
            Integer index = targets.indexOf(target);
            int textureIndex = targetTextureIndices.indexOf(index);
            setInputSurfaceTextureForTarget(target, textureIndex);
        }
    }

    public List<IDHImageInput> getTargets() {
        return targets;
    }

    public void addTarget(IDHImageInput target) {
        if (target == null) return;
        int nextAvailableTextureIndex = target.nextAvailableTextureIndex();
        addTarget(target, nextAvailableTextureIndex);
        if (target.shouldIgnoreUpdatesToThisTarget()) {
            targetToIgnoreForUpdates = target;
        }
    }

    public void addTarget(IDHImageInput target, int location) {
        if (target == null) return;
        if (targets.contains(target)) {
            return;
        }
        cachedMaximumOutputSize = DHImageSize.zeroSize();
        //TO-DO: Run on Video Processing Queue
        setInputSurfaceTextureForTarget(target, location);
        targets.add(target);
        targetTextureIndices.add(new Integer(location));

        allTargetsWantMonochromeData = allTargetsWantMonochromeData && target.wantsMonochromeInput();
    }

    public void removeTarget(IDHImageInput target) {
        if (target == null) return;
        if (!targets.contains(target)) {
            return;
        }
        if (target.equals(targetToIgnoreForUpdates)) {
            targetToIgnoreForUpdates = null;
        }
        cachedMaximumOutputSize = DHImageSize.zeroSize();

        int indexOfObject = targets.indexOf(target);
        Integer textureIndexOfTarget = targetTextureIndices.get(indexOfObject);
        //TO-DO: Run on video processing queue
        target.setInputSize(DHImageSize.zeroSize(), textureIndexOfTarget);
        target.setInputRotation(DHImageRotationMode.NoRotation, textureIndexOfTarget);

        targetTextureIndices.remove(indexOfObject);
        targets.remove(target);
        target.endProcessing();
    }

    public void removeAllTargets() {
        cachedMaximumOutputSize = DHImageSize.zeroSize();
        //TO-DO: Run on video processing queue
        for (IDHImageInput target : targets) {
            int indexOfObject = targets.indexOf(target);
            Integer textureIndexOfTarget = targetTextureIndices.get(indexOfObject);

            target.setInputSize(DHImageSize.zeroSize(), textureIndexOfTarget);
            target.setInputRotation(DHImageRotationMode.NoRotation, textureIndexOfTarget);
        }
        targets.clear();;
        targetTextureIndices.clear();
        allTargetsWantMonochromeData = true;
    }

    public void forceProcessingAtSize(DHImageSize size) {

    }

    public void forceProcessingAtSizeRespectingAspectRatio(DHImageSize size) {

    }

    public void useNextFrameForImageCapture() {

    }

    public Bitmap newImageFromCurrentlyProcessedOutput() {
        return null;
    }

    public Bitmap newImageByFilteringImage(Bitmap originalImage) {
        DHImagePicture picture = new DHImagePicture(originalImage);
        useNextFrameForImageCapture();
        picture.addTarget((IDHImageInput) this);
        picture.processImage();
        Bitmap processedImage = newImageFromCurrentlyProcessedOutput();
        picture.removeTarget((IDHImageInput)this);
        return processedImage;
    }

    public Bitmap imageFromCurrentFrameBuffer() {
        Bitmap image = newImageFromCurrentlyProcessedOutput();
        //TO-DO: process by orientation
        return image;
    }

    public Bitmap imageByFilteringImage(Bitmap originalImage) {
        Bitmap image = newImageByFilteringImage(originalImage);
        //TO-DO: process by orientation
        return image;
    }


    public void destroy() {
        removeAllTargets();
    }

    public boolean isShouldSmoothlyScaleOutput() {
        return shouldSmoothlyScaleOutput;
    }

    public void setShouldSmoothlyScaleOutput(boolean shouldSmoothlyScaleOutput) {
        this.shouldSmoothlyScaleOutput = shouldSmoothlyScaleOutput;
    }

    public boolean isShouldIgnoreUpdatesToThisTarget() {
        return shouldIgnoreUpdatesToThisTarget;
    }

    public void setShouldIgnoreUpdatesToThisTarget(boolean shouldIgnoreUpdatesToThisTarget) {
        this.shouldIgnoreUpdatesToThisTarget = shouldIgnoreUpdatesToThisTarget;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public IDHImageInput getTargetToIgnoreForUpdates() {
        return targetToIgnoreForUpdates;
    }

    public void setTargetToIgnoreForUpdates(IDHImageInput targetToIgnoreForUpdates) {
        this.targetToIgnoreForUpdates = targetToIgnoreForUpdates;
    }

    public DHImageTextureOptions getOutputTextureOptions() {
        return outputTextureOptions;
    }

    public void setOutputTextureOptions(DHImageTextureOptions outputTextureOptions) {
        this.outputTextureOptions = outputTextureOptions;
    }

//    public EGLSurface getSurface() {
//        return mSurface;
//    }
}
