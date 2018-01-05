package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImagePoint;

/**
 * Created by huanghongsen on 2018/1/4.
 */

public class DHImageToneCurveFilter extends DHImageFilter {
    public static String LOG_TAG = "ToneCurveFilter";

    public static String DH_TONE_CURVE_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D toneCurveTexture;\n" +
            " uniform mediump float strength;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     lowp float redCurveValue = texture2D(toneCurveTexture, vec2(textureColor.r, 0.0)).r;\n" +
            "     lowp float greenCurveValue = texture2D(toneCurveTexture, vec2(textureColor.g, 0.0)).g;\n" +
            "     lowp float blueCurveValue = texture2D(toneCurveTexture, vec2(textureColor.b, 0.0)).b;\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(textureColor.rgb, vec3(redCurveValue, greenCurveValue, blueCurveValue), strength), textureColor.a);\n" +
            " }";

    private DHImagePoint[] redControlPoints, greenControlPoints, blueControlPoints, rgbCompositeControlPoints;
    private int totalCurves, version;
    private float[] redCurve, greenCurve, blueCurve, rgbCompositeCurve;
    private byte[] toneCurveByteArray;
    private int toneCurveTextureUniform;
    private int toneCurveTexture;

    public DHImageToneCurveFilter() {
        this(0.f, 1.f, 1.f);
    }

    public DHImageToneCurveFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageToneCurveFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_TONE_CURVE_FRAGMENT_SHADER, minValue, maxValue, initialValue);
        toneCurveTextureUniform = filterProgram.getUniformIndex("toneCurveTexture");
        DHImagePoint[] defaultCurvePoints = {new DHImagePoint(0.f, 0.f), new DHImagePoint(0.5f, 0.5f), new DHImagePoint(1.f, 1.f)};
        setRgbCompositeControlPoints(defaultCurvePoints);
        setRedControlPoints(defaultCurvePoints);
        setGreenControlPoints(defaultCurvePoints);
        setBlueControlPoints(defaultCurvePoints);
    }

    public DHImageToneCurveFilter(InputStream input) {
        super(DH_VERTEX_SHADER_STRING, DH_TONE_CURVE_FRAGMENT_SHADER, 0.f, 1.f, 1.f);
        toneCurveTextureUniform = filterProgram.getUniformIndex("toneCurveTexture");
        setPointsWithACV(input);
        updateWithStrength(1.f);
    }

    public void  setPointsWithACV(InputStream input) {
        try {
            version = readShort(input);
            totalCurves = readShort(input);
            ArrayList<DHImagePoint[]> curves = new ArrayList<DHImagePoint[]>(totalCurves);
            float pointRate = 1.0f / 255;

            for (int i = 0; i < totalCurves; i++) {
                short pointCount = readShort(input);

                DHImagePoint[] points = new DHImagePoint[pointCount];

                // point count * 4
                // Curve points. Each curve point is a pair of short integers where
                // the first number is the output value (vertical coordinate on the
                // Curves dialog graph) and the second is the input value. All coordinates have range 0 to 255.
                for (int j = 0; j < pointCount; j++) {
                    short y = readShort(input);
                    short x = readShort(input);

                    points[j] = new DHImagePoint(x * pointRate, y * pointRate);
                }

                curves.add(points);
            }
            input.close();

            setRgbCompositeControlPoints(curves.get(0));
            setRedControlPoints(curves.get(1));
            setGreenControlPoints(curves.get(2));
            setBlueControlPoints(curves.get(3));

        } catch (Exception e) {
            Log.e(LOG_TAG, "Fail to read input stream");
        }
    }

    @Override
    public void renderToTexture(float[] vertices, float[] texCoords) {
        if (preventRendering) {
            firstInputFrameBuffer.unlock();
            return;
        }
        DHImageContext.setActiveProgram(filterProgram);
        outputFrameBuffer = DHImageContext.sharedFrameBufferCache().fetchFrameBuffer(sizeOfFBO(), getOutputTextureOptions(), false);
        outputFrameBuffer.activate();

        if (usingNextFrameForImageCapture) {
            outputFrameBuffer.lock();
        }

        GLES20.glClearColor(backgroundColorRed, backgroundColorGreen, backgroundColorBlue, backgroundColorAlpha);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, firstInputFrameBuffer.getTexture());
        GLES20.glUniform1i(filterInputTextureUniform, 2);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, toneCurveTexture);
        GLES20.glUniform1i(toneCurveTextureUniform, 3);

        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices).position(0);

        FloatBuffer texCoordsBuffer = ByteBuffer.allocateDirect(texCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoordsBuffer.put(texCoords).position(0);

        GLES20.glVertexAttribPointer(filterPositionAttribute, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(filterTexCoordAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoordsBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        firstInputFrameBuffer.unlock();

        outputFrameBuffer.deactivate();
    }

    @Override
    public void updateWithInput(float input) {
        updateWithStrength(input);
    }

    public float[] getPreparedSpineCurve(DHImagePoint[] points) {
        if (points == null || points.length == 0) {
            return null;
        }
        DHImagePoint sortedPoints[] = points.clone();
        Arrays.sort(sortedPoints);
        DHImagePoint convertedPoints[] = new DHImagePoint[sortedPoints.length];
        for (int i = 0;  i < sortedPoints.length; i++) {
            DHImagePoint point = sortedPoints[i];
            DHImagePoint convertedPoint = new DHImagePoint(point.x * 255.f, point.y * 255.f);
            convertedPoints[i] = convertedPoint;
        }
        List<DHImagePoint> splinePoints = getSplineCurve(convertedPoints);
        if (splinePoints == null) {
            return new float[0];
        }
        DHImagePoint firstSplinePoint = splinePoints.get(0);
        if (firstSplinePoint.x > 0) {
            for (int i = (int)firstSplinePoint.x; i >= 0; i--) {
                DHImagePoint newPoint = new DHImagePoint(i, 0);
                splinePoints.add(0, newPoint);
            }
        }
        DHImagePoint lastSplinePoint = splinePoints.get(splinePoints.size() - 1);
        if (lastSplinePoint.x < 255.f) {
            for (int i = (int)lastSplinePoint.x; i <= 255; i++) {
                splinePoints.add(new DHImagePoint(i, 255.f));
            }
        }

        float[] preparedSplinePoints = new float[splinePoints.size()];
        for (int i = 0; i < splinePoints.size(); i++) {
            DHImagePoint newPoint = splinePoints.get(i);
            DHImagePoint originalPoint = new DHImagePoint(newPoint.x, newPoint.x);
            float distance = (float)Math.sqrt(Math.pow((originalPoint.x - newPoint.x), 2.0) + (float)Math.pow((originalPoint.y - newPoint.y), 2.0));
            if (originalPoint.y > newPoint.y)
            {
                distance = -distance;
            }
            preparedSplinePoints[i] = distance;
        }
        return preparedSplinePoints;
    }

    public List<DHImagePoint> getSplineCurve(DHImagePoint[] points) {
        float[] sdA = getSecondDerivative(points);
        if (sdA == null) {
            return null;
        }
        int count = sdA.length;
        if (count < 1) return null;
        List<DHImagePoint> output = new ArrayList<>();
        for (int i = 0; i < count -1; i++) {
            DHImagePoint cur = points[i];
            DHImagePoint next = points[i + 1];

            for (int x = (int) cur.x; x < (int) next.x; x++) {
                float t = (x - cur.x) / (next.x - cur.x);

                float a = 1 - t;
                float b = t;
                float h = next.x - cur.x;

                float y = a * cur.y + b * next.y + (h * h / 6) * ((a * a * a - a) * sdA[i] + (b * b * b - b) * sdA[i + 1]);

                if (y > 255.0f) {
                    y = 255.0f;
                } else if (y < 0.0f) {
                    y = 0.0f;
                }
                output.add(new DHImagePoint(x, y));
            }
        }
        output.add(points[points.length - 1]);
        return output;
    }

    public float[] getSecondDerivative(DHImagePoint[] points) {
        int count = points.length;
        if (count < 1) {
            return null;
        }
        float matrix[][] = new float[count][3];
        float result[] = new float[count];

        matrix[0][1]=1.f;
        matrix[0][0]=0.f;
        matrix[0][2]=0.f;

        for(int i=1;i<count-1;i++)
        {
            DHImagePoint P1 = points[i-1];
            DHImagePoint P2 = points[i];
            DHImagePoint P3 = points[i+1];

            matrix[i][0]=(P2.x-P1.x)/6.f;
            matrix[i][1]=(P3.x-P1.x)/3.f;
            matrix[i][2]=(P3.x-P2.x)/6.f;
            result[i]=(P3.y-P2.y)/(P3.x-P2.x) - (P2.y-P1.y)/(P2.x-P1.x);
        }

        result[0] = 0;
        result[count-1] = 0;

        matrix[count-1][1]=1;
        // What about matrix[n-1][0] and matrix[n-1][2]? For now, assuming they are 0 (Brad L.)
        matrix[count-1][0]=0;
        matrix[count-1][2]=0;

        // solving pass1 (up->down)
        for(int i=1;i<count;i++)
        {
            double k = matrix[i][0]/matrix[i-1][1];
            matrix[i][1] -= k*matrix[i-1][2];
            matrix[i][0] = 0;
            result[i] -= k*result[i-1];
        }
        // solving pass2 (down->up)
        for(int i=count-2;i>=0;i--)
        {
            double k = matrix[i][2]/matrix[i+1][1];
            matrix[i][1] -= k*matrix[i+1][0];
            matrix[i][2] = 0;
            result[i] -= k*result[i+1];
        }

        float y2[] = new float[count];
        for(int i=0;i<count;i++) y2[i]=result[i]/matrix[i][1];

        return y2;

    }

    public void updateToneCurveTexture() {
        if (redCurve == null || greenCurve == null || blueCurve == null || rgbCompositeCurve == null) return;
        if (toneCurveTexture == 0)
        {
            int textures[] = new int[1];
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glGenTextures(1, textures, 0);
            toneCurveTexture = textures[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, toneCurveTexture);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            toneCurveByteArray = new byte[256 * 4];
        }
        else
        {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, toneCurveTexture);
        }

        if ( (redCurve.length >= 256) && (greenCurve.length >= 256) && (blueCurve.length >= 256) && (rgbCompositeCurve.length >= 256))
        {
            for (int currentCurveIndex = 0; currentCurveIndex < 256; currentCurveIndex++)
            {
                // RGBA for upload to texture
                int r = (int)Math.min(Math.max(currentCurveIndex + redCurve[currentCurveIndex], 0), 255);
                toneCurveByteArray[currentCurveIndex * 4 + 0] = (byte)Math.min(Math.max(r + rgbCompositeCurve[r], 0), 255);
                int g = (int)Math.min(Math.max(currentCurveIndex + greenCurve[currentCurveIndex], 0), 255);
                toneCurveByteArray[currentCurveIndex * 4 + 1] = (byte)Math.min(Math.max(g + rgbCompositeCurve[g], 0), 255);
                int b = (int)Math.min(Math.max(currentCurveIndex + blueCurve[currentCurveIndex], 0), 255);
                toneCurveByteArray[currentCurveIndex * 4 + 2] = (byte)Math.min(Math.max(b + rgbCompositeCurve[b], 0), 255);
                toneCurveByteArray[currentCurveIndex * 4 + 3] = (byte)255;
            }

            for (int i = 0; i < toneCurveByteArray.length; i++) {
                Log.d(LOG_TAG, "toneCurve[" + i + "] = " + toneCurveByteArray[i]);
            }
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 256 /*width*/, 1 /*height*/, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ByteBuffer.wrap(toneCurveByteArray));
        }
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.ToneCurve;
    }

    //Private Helpers
    private short readShort(InputStream input) throws IOException {
        return (short) (input.read() << 8 | input.read());
    }

    //Getters & Setters

    public DHImagePoint[] getRedControlPoints() {
        return redControlPoints;
    }

    public void setRedControlPoints(DHImagePoint[] redControlPoints) {
        this.redControlPoints = redControlPoints;
        redCurve = getPreparedSpineCurve(redControlPoints);
        updateToneCurveTexture();
    }

    public DHImagePoint[] getGreenControlPoints() {
        return greenControlPoints;
    }

    public void setGreenControlPoints(DHImagePoint[] greenControlPoints) {
        this.greenControlPoints = greenControlPoints;
        greenCurve = getPreparedSpineCurve(greenControlPoints);
        updateToneCurveTexture();
    }

    public DHImagePoint[] getBlueControlPoints() {
        return blueControlPoints;
    }

    public void setBlueControlPoints(DHImagePoint[] blueControlPoints) {
        this.blueControlPoints = blueControlPoints;
        blueCurve = getPreparedSpineCurve(blueControlPoints);
        updateToneCurveTexture();
    }

    public DHImagePoint[] getRgbCompositeControlPoints() {
        return rgbCompositeControlPoints;
    }

    public void setRgbCompositeControlPoints(DHImagePoint[] rgbCompositeControlPoints) {
        this.rgbCompositeControlPoints = rgbCompositeControlPoints;

        rgbCompositeCurve = getPreparedSpineCurve(rgbCompositeControlPoints);

        updateToneCurveTexture();
    }
}
