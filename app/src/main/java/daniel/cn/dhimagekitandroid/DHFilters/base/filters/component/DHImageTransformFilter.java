package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import android.opengl.Matrix;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHMatrix4X4;

/**
 * Created by huanghongsen on 2018/1/3.
 */

public class DHImageTransformFilter extends DHImageFilter {

    public static float squareVertices[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f,  1.0f,
            1.0f,  1.0f,
    };

    public static float squareVerticesAnchorTL[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f,  1.0f,
            1.0f,  1.0f,
    };

    public static String DH_TRANSFORM_VERTEX_SHADER = "attribute vec4 position;\n" +
            " attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            " uniform mat4 transformMatrix;\n" +
            " uniform mat4 orthographicMatrix;\n" +
            " \n" +
            " varying vec2 textureCoordinate;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_Position = transformMatrix * vec4(position.xyz, 1.0) * orthographicMatrix;\n" +
            "     textureCoordinate = inputTextureCoordinate.xy;\n" +
            " }";

    private int transformMatrixUniform, orthographicMatrixUniform;
    private DHMatrix4X4 orthographicsMatrix;
    private DHMatrix4X4 transformMatrix;
    private float rotation;

    private boolean ignoreAspectRatio;
    private boolean anchorTopLeft;

    public DHImageTransformFilter() {
        this(-25.f, 25.f, 0.f);
    }

    public DHImageTransformFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageTransformFilter(float minValue, float maxValue, float initialValue) {
        super(DH_TRANSFORM_VERTEX_SHADER, DH_PASS_THROUGH_FRAGMENT_SHADER, minValue, maxValue, initialValue);
        filterProgram.use();
        transformMatrixUniform = filterProgram.getUniformIndex("transformMatrix");
        orthographicMatrixUniform = filterProgram.getUniformIndex("orthographicMatrix");

        setIgnoreAspectRatio(true);
        setRotation(initialValue);
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.Transform;
    }

    @Override
    public void newFrameReady(float time, int index) {
        DHImageSize currentFBOSize = sizeOfFBO();
        float normalizedHeight = currentFBOSize.height / currentFBOSize.width;
        float adjustedVertices[] = {
                -1.0f, -normalizedHeight,
                1.0f, -normalizedHeight,
                -1.0f,  normalizedHeight,
                1.0f,  normalizedHeight,
        };

        float adjustedVerticesAnchorTL[] = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f,  normalizedHeight,
                1.0f,  normalizedHeight,
        };

        if (ignoreAspectRatio)
        {
            if (anchorTopLeft) {
                renderToTexture(squareVerticesAnchorTL, textureCoordinatesForRotation(inputRotationMode));
            } else {
                renderToTexture(squareVertices, textureCoordinatesForRotation(inputRotationMode));
            }
        }
        else {
            if (anchorTopLeft) {
                renderToTexture(adjustedVerticesAnchorTL, textureCoordinatesForRotation(inputRotationMode));
            } else {
                renderToTexture(adjustedVertices, textureCoordinatesForRotation(inputRotationMode));
            }
        }
        informTargetsAboutNewFrameReadyAtTime(time);
    }

    public DHMatrix4X4 getOrthographicsMatrix() {
        return orthographicsMatrix;
    }

    public void setOrthographicsMatrix(DHMatrix4X4 orthographicsMatrix) {
        this.orthographicsMatrix = orthographicsMatrix;
        setMatrix4X4Uniform(orthographicsMatrix, orthographicMatrixUniform, filterProgram);
    }

    public DHMatrix4X4 getTransformMatrix() {
        return transformMatrix;
    }

    public void setTransformMatrix(DHMatrix4X4 transformMatrix) {
        this.transformMatrix = transformMatrix;
        setMatrix4X4Uniform(transformMatrix, transformMatrixUniform, filterProgram);
    }

    @Override
    public float getCurrentValue() {
        return rotation;
    }

    @Override
    public void updateWithInput(float input) {
        setRotation(input);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        float[] matrix = DHMatrix4X4.identityMatrix().toArray();
        Matrix.rotateM(matrix, 0,rotation, 0.f, 0.f, 1.f);
        setTransformMatrix(DHMatrix4X4.matrixFromArray(matrix));
    }

    public boolean isIgnoreAspectRatio() {
        return ignoreAspectRatio;
    }

    public void setIgnoreAspectRatio(boolean ignoreAspectRatio) {
        this.ignoreAspectRatio = ignoreAspectRatio;
        if (ignoreAspectRatio) {
            loadOrthoMatrix(-1.f, 1.f, -1.f, 1.f, -1.f, 1.f);
            setMatrix4X4Uniform(orthographicsMatrix, orthographicMatrixUniform, filterProgram);
        } else {
            setupFilterForSize(sizeOfFBO());
        }
    }

    @Override
    public void setupFilterForSize(DHImageSize size) {
        if (!ignoreAspectRatio)
        {
            loadOrthoMatrix(-1.f, 1.f, (-1.f * size.height / size.width), (1.f * size.height / size.width), -1.f, 1.f);
            setMatrix4X4Uniform(orthographicsMatrix, orthographicMatrixUniform, filterProgram);
        }
    }

    public boolean isAnchorTopLeft() {
        return anchorTopLeft;
    }

    public void setAnchorTopLeft(boolean anchorTopLeft) {
        this.anchorTopLeft = anchorTopLeft;
        setIgnoreAspectRatio(ignoreAspectRatio);
    }

    private void loadOrthoMatrix(float left, float right, float bottom, float top, float near, float far) {
        float r_l = right - left;
        float t_b = top - bottom;
        float f_n = far - near;
        float tx = - (right + left) / (right - left);
        float ty = - (top + bottom) / (top - bottom);
        float tz = - (far + near) / (far - near);

        float scale = 2.0f;
        if (anchorTopLeft)
        {
            scale = 4.0f;
            tx=-1.0f;
            ty=-1.0f;
        }

        float matrix[] = new float[16];
        matrix[0] = scale / r_l;
        matrix[1] = 0.0f;
        matrix[2] = 0.0f;
        matrix[3] = tx;

        matrix[4] = 0.0f;
        matrix[5] = scale / t_b;
        matrix[6] = 0.0f;
        matrix[7] = ty;

        matrix[8] = 0.0f;
        matrix[9] = 0.0f;
        matrix[10] = scale / f_n;
        matrix[11] = tz;

        matrix[12] = 0.0f;
        matrix[13] = 0.0f;
        matrix[14] = 0.0f;
        matrix[15] = 1.0f;
        orthographicsMatrix = DHMatrix4X4.matrixFromArray(matrix);
    }
}
