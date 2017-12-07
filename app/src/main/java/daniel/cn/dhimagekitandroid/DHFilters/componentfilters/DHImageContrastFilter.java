package daniel.cn.dhimagekitandroid.DHFilters.componentfilters;

import android.opengl.GLES20;

import daniel.cn.dhimagekitandroid.DHFilters.DHImageBaseFilter;

/**
 * Created by huanghongsen on 2017/12/7.
 */

public class DHImageContrastFilter extends DHImageBaseFilter {
    public static final String DH_CONTRAST_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float contrast;\n" +
            " uniform lowp float strength; \n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(textureColor.rgb, ((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), strength), textureColor.w);\n" +
            " }";

    protected int contrastUniform;

    private float mContrast;

    public DHImageContrastFilter() {
        super(NO_FILTER_VERTEX_SHADER, DH_CONTRAST_FRAGMENT_SHADER);
        this.setMinValue(0.7f);
        this.setMaxValue(1.3f);
        this.setInitialValue(1.f);
        mContrast = 0.f;
    }

    public void onInit() {
        super.onInit();
        contrastUniform = GLES20.glGetUniformLocation(mGLProgId, "contrast");
    }

    public void onInitialized() {
        super.onInitialized();;
    }

    public void setContrast(float contrast) {
        mContrast = contrast;
        setFloat(contrastUniform, contrast);
    }

    @Override
    public void updateWithInput(float input) {
        float val = this.getMinValue() + (this.getMaxValue() - this.getMinValue()) * input;
        this.setContrast(val);
    }
}
