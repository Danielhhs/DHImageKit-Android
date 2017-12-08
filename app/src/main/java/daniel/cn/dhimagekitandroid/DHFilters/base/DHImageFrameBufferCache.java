package daniel.cn.dhimagekitandroid.DHFilters.base;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huanghongsen on 2017/12/7.
 */

public class DHImageFrameBufferCache {
    private HashMap<String, DHImageFrameBuffer> frameBufferCache;
    private HashMap<String, Integer> frameBufferTypeCount;
    private ArrayList<DHImageFrameBuffer> activeFrameBufferList;

    public DHImageFrameBuffer fetchFrameBuffer(int width, int height, DHImageTextureOptions textureOptions, boolean onlyTexture) {
        DHImageFrameBuffer frameBufferFromCache = null;
        //TO-DO: Run on another thread
        String lookupHash = hashForTexture(width, height, textureOptions, onlyTexture);
        Integer numberOfMachings = frameBufferTypeCount.get(lookupHash);
        if (numberOfMachings == null || numberOfMachings == 0) {
            frameBufferFromCache = new DHImageFrameBuffer(width, height, textureOptions, onlyTexture);
        } else {
            Integer currentTextureId = numberOfMachings - 1;
            while ((frameBufferFromCache == null) && currentTextureId >= 0) {
                String textureHashKey = lookupHash + "-" + currentTextureId;
                frameBufferFromCache = frameBufferCache.get(textureHashKey);
                if (frameBufferFromCache != null) {
                    frameBufferCache.remove(textureHashKey);
                }
                currentTextureId--;
            }
            currentTextureId++;
            frameBufferTypeCount.put(lookupHash, currentTextureId);
            if (frameBufferFromCache == null) {
                frameBufferFromCache = new DHImageFrameBuffer(width, height, textureOptions, onlyTexture);
            }
        }
        frameBufferFromCache.lock();
        return frameBufferFromCache;
    }

    public DHImageFrameBuffer fetchFrameBuffer(int width, int height, boolean onlyTexture) {
        return fetchFrameBuffer(width, height, new DHImageTextureOptions(), onlyTexture);
    }

    public void returnFrameBufferToCache(DHImageFrameBuffer frameBuffer) {
        frameBuffer.clearAllLocks();
        //TO-DO: Run on another thread;
        String lookupHash = hashForTexture(frameBuffer.getWidth(), frameBuffer.getHeight(), frameBuffer.getTextureOptions(),frameBuffer.isMissingFrameBuffer());
        Integer numberOfTextures = frameBufferTypeCount.get(lookupHash);
        String textureHash = lookupHash + "-" + numberOfTextures;
        frameBufferCache.put(textureHash, frameBuffer);
        frameBufferTypeCount.put(lookupHash, numberOfTextures + 1);
    }

    public void addFrameBufferToActiveImageCaptureList(DHImageFrameBuffer frameBuffer) {
        //TO-DO: Run on another thread
        activeFrameBufferList.add(frameBuffer);
    }

    public void removeFrameBufferFromActiveImageCaptureList(DHImageFrameBuffer frameBuffer) {
        //TO-DO: Run on another thread
        activeFrameBufferList.remove(frameBuffer);
    }

    public void purgeAllUnassignedFrameBuffer() {
        //TO-DO: Run on another thread
        frameBufferCache.clear();
        frameBufferTypeCount.clear();
    }

    //Private Helper
    private String hashForTexture(int width, int height, DHImageTextureOptions textureOptions, boolean onlyTexture) {
        StringBuilder sb = new StringBuilder();
        sb.append(width).append("x").append(height).append("-").append(textureOptions.toString());
        if (onlyTexture) {
            sb.append("-NOFB");
        }
        return sb.toString();
    }

    //Singleton
    private DHImageFrameBufferCache() {
        frameBufferCache = new HashMap<>();
        frameBufferTypeCount = new HashMap<>();
        activeFrameBufferList = new ArrayList<>();
    };

    private static class DHImageFrameBufferCacheHolder {
        private static final DHImageFrameBufferCache sharedInstance = new DHImageFrameBufferCache();
    }

    public static DHImageFrameBufferCache sharedCache() {
        return DHImageFrameBufferCacheHolder.sharedInstance;
    }


}
