package daniel.cn.dhimagekitandroid.DHFilters.componentfilters;

import android.opengl.GLES20;

import daniel.cn.dhimagekitandroid.DHFilters.DHImageBaseFilter;

/**
 * Created by huanghongsen on 2017/12/6.
 */

public class DHImageBrightnessFilter extends DHImageBaseFilter {
    public static final String DH_BRIGHTNESS_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float brightness;\n" +
            " uniform lowp float strength;" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(textureColor.rgb, textureColor.rgb + vec3(brightness), strength), textureColor.w);\n" +
            " }";

    protected int brightnessUniform;

    private float mBrightness;

    public DHImageBrightnessFilter() {
        super(NO_FILTER_VERTEX_SHADER, DH_BRIGHTNESS_FRAGMENT_SHADER);
        this.setMinValue(-0.25f);
        this.setMaxValue(0.25f);
        this.setInitialValue(0.f);
        mBrightness = 0.f;
    }

    public void onInit() {
        super.onInit();
        brightnessUniform = GLES20.glGetUniformLocation(mGLProgId, "brightness");
    }

    public void onInitialized() {
        super.onInitialized();;
    }

    public void setBrightness(float brightness) {
        mBrightness = brightness;
        setFloat(brightnessUniform, brightness);
    }

    @Override
    public void updateWithInput(float input) {
        float val = this.getMinValue() + (this.getMaxValue() - this.getMinValue()) * input;
        this.setBrightness(val);
    }
}

