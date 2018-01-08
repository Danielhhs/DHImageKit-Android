package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;

/**
 * Created by huanghongsen on 2018/1/2.
 *
 * In correspondance to GPUImageWhiteBalanceFilter
 */

public class DHImageWarmthFilter extends DHImageFilter {
    public static String DH_WARMTH_FRAGMENT_SHADER = "uniform sampler2D inputImageTexture;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform lowp float temperature;\n" +
            "uniform lowp float strength;\n" +
            "uniform lowp float tint;\n" +
            "\n" +
            "const lowp vec3 warmFilter = vec3(0.93, 0.54, 0.0);\n" +
            "\n" +
            "const mediump mat3 RGBtoYIQ = mat3(0.299, 0.587, 0.114, 0.596, -0.274, -0.322, 0.212, -0.523, 0.311);\n" +
            "const mediump mat3 YIQtoRGB = mat3(1.0, 0.956, 0.621, 1.0, -0.272, -0.647, 1.0, -1.105, 1.702);\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\tlowp vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
            "\t\n" +
            "\tmediump vec3 yiq = RGBtoYIQ * source.rgb; //adjusting tint\n" +
            "\tyiq.b = clamp(yiq.b + tint*0.5226*0.1, -0.5226, 0.5226);\n" +
            "\tlowp vec3 rgb = YIQtoRGB * yiq;\n" +
            "\n" +
            "\tlowp vec3 processed = vec3(\n" +
            "\t\t(rgb.r < 0.5 ? (2.0 * rgb.r * warmFilter.r) : (1.0 - 2.0 * (1.0 - rgb.r) * (1.0 - warmFilter.r))), //adjusting temperature\n" +
            "\t\t(rgb.g < 0.5 ? (2.0 * rgb.g * warmFilter.g) : (1.0 - 2.0 * (1.0 - rgb.g) * (1.0 - warmFilter.g))), \n" +
            "\t\t(rgb.b < 0.5 ? (2.0 * rgb.b * warmFilter.b) : (1.0 - 2.0 * (1.0 - rgb.b) * (1.0 - warmFilter.b))));\n" +
            "\n" +
            "\tprocessed = mix(rgb, processed, temperature);\n" +
            "\tgl_FragColor = vec4(mix(rgb, processed, strength), source.a);\n" +
            "}";

    protected int temperatureUniform, tintUniform;
    private float temperature, tint;

    public DHImageWarmthFilter() {
        this(3500.f, 6500.f, 5000.f);
    }

    public DHImageWarmthFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageWarmthFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_WARMTH_FRAGMENT_SHADER, minValue, maxValue, initialValue);
        temperatureUniform = filterProgram.getUniformIndex("temperature");
        tintUniform = filterProgram.getUniformIndex("tint");

        setTemperature(initialValue);
        setTint(0.f);
        updateWithStrength(1.f);
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.Warmth;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
        float uniformValue = 0.f;
        if (temperature < 5000.f) {
            uniformValue = 0.0004f * (temperature-5000.0f);
        } else {
            uniformValue = 0.00006f * (temperature-5000.0f);
        }
        setFloatUniform(uniformValue, temperatureUniform, filterProgram);
    }

    public float getTint() {
        return tint;
    }

    public void setTint(float tint) {
        this.tint = tint;
        float uniformValue = tint / 100.f;
        setFloatUniform(uniformValue, tintUniform, filterProgram);
    }

    @Override
    public void updateWithInput(float input) {
        setTemperature(input);
    }

    @Override
    public float getCurrentValue() {
        return temperature;
    }
}

