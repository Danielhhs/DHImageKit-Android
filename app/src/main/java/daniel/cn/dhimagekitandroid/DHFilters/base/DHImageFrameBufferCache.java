package daniel.cn.dhimagekitandroid.DHFilters.base;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/26.
 */

public class DHImageFrameBufferCache {
    public static String LOG_TAG = "DHImageFrameBufferCache";
    private Map<String, DHImageFrameBuffer> frameBufferCache;
    private Map<String, Integer> frameBufferTypeCounts;

    public DHImageFrameBufferCache() {
        frameBufferCache = new HashMap<>();
        frameBufferTypeCounts = new HashMap<>();
    }

    public DHImageFrameBuffer fetchFrameBuffer(DHImageSize size) {
        DHImageTextureOptions defaultOptions = new DHImageTextureOptions();
        return fetchFrameBuffer(size, defaultOptions);
    }

    public DHImageFrameBuffer fetchFrameBuffer(DHImageSize size, DHImageTextureOptions options) {
        return fetchFrameBuffer(size, options, false);
    }

    public DHImageFrameBuffer fetchFrameBuffer(DHImageSize size, DHImageTextureOptions options, boolean onlyTexture) {
        //TO-DO: Run on video processing queue
        DHImageFrameBuffer frameBuffer = null;

        String hashKey = hashKeyForFrameBuffer(size, options, onlyTexture);
        Integer numberOfMatchingsInCache = frameBufferTypeCounts.get(hashKey);

        if (numberOfMatchingsInCache == null || numberOfMatchingsInCache < 1) {
            Log.d(LOG_TAG, ">>>Failed to Fetching frameBuffer for key: " + hashKey);
            frameBuffer = new DHImageFrameBuffer(size, options, onlyTexture);
        } else {
            Log.d(LOG_TAG, ">>>Fetching frameBuffer for key: " + hashKey + ", number of matches: " + numberOfMatchingsInCache);
            Integer currentTextureId = numberOfMatchingsInCache - 1;
            while (frameBuffer == null && currentTextureId > 0) {
                String textureHash = hashKey + "-" + currentTextureId;
                frameBuffer = frameBufferCache.get(hashKey);
                if (frameBuffer != null) {
                    frameBufferCache.remove(hashKey);
                }
                currentTextureId--;
            }
            currentTextureId++;
            frameBufferTypeCounts.put(hashKey, currentTextureId);
            if (frameBuffer == null) {
                frameBuffer = new DHImageFrameBuffer(size, options, onlyTexture);
            }
        }

        frameBuffer.lock();
        return frameBuffer;
    }

    public void returnFrameBuffer(DHImageFrameBuffer frameBuffer) {
        frameBuffer.clearAllLocks();
        DHImageSize size = frameBuffer.getSize();
        DHImageTextureOptions textureOptions = frameBuffer.getTextureOptions();
        boolean onlyTexture = frameBuffer.isOnlyTexture();

        String hashKey = hashKeyForFrameBuffer(size, textureOptions, onlyTexture);
        Integer numberOfFrameBuffersInCache = frameBufferTypeCounts.get(hashKey);
        if (numberOfFrameBuffersInCache == null) {
            numberOfFrameBuffersInCache = 0;
        }
        String textureHashKey = hashKey + "-" + numberOfFrameBuffersInCache;
        frameBufferCache.put(textureHashKey, frameBuffer);
        frameBufferTypeCounts.put(hashKey, numberOfFrameBuffersInCache + 1);
        Log.d(LOG_TAG, ">>>Returning frameBuffer for key: " + textureHashKey + ", textureCount: " + numberOfFrameBuffersInCache + " for hashKey: " + hashKey);
    }

    private String hashKeyForFrameBuffer(DHImageSize size, DHImageTextureOptions options, boolean onlyTexture) {
        StringBuilder sb = new StringBuilder();
        sb.append(size.width).append("x").append(size.height).append('-').append(options.minFilter).append(':')
                .append(options.maxFilter).append(':').append(options.wrapS).append(':').append(options.wrapT).append(':')
                .append(options.internalFormat).append(':').append(options.format).append(':').append(options.type);
        if (onlyTexture) {
            sb.append("-NOFB");
        }
        return sb.toString();
    }
}
