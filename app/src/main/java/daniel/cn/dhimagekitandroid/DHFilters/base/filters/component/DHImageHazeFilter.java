package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;

/**
 * Created by huanghongsen on 2018/1/2.
 */

public class DHImageHazeFilter extends DHImageFilter {
    public static String DH_HAZE_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " \n" +
            " uniform lowp float hazeDistance;\n" +
            " uniform highp float slope;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "\t//todo reconsider precision modifiers\t \n" +
            "\t highp vec4 color = vec4(1.0);//todo reimplement as a parameter\n" +
            "\t \n" +
            "\t highp float  d = textureCoordinate.y * slope  +  hazeDistance;\n" +
            "\t \n" +
            "\t highp vec4 c = texture2D(inputImageTexture, textureCoordinate) ; // consider using unpremultiply\n" +
            "\t \n" +
            "\t c = (c - d * color) / (1.0 -d);\n" +
            "\t \n" +
            "\t gl_FragColor = c; //consider using premultiply(c);\n" +
            " }";

    protected int distanceUnfiorm, slopeUniform;
    private float distance, slope;

    public DHImageHazeFilter() {
        this(-0.3f, 0.3f, 0.f);
    }

    public DHImageHazeFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageHazeFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_HAZE_FRAGMENT_SHADER, minValue, maxValue, initialValue);

        distanceUnfiorm = filterProgram.getUniformIndex("hazeDistance");
        slopeUniform = filterProgram.getUniformIndex("slope");

        setDistance(initialValue);
        setSlope(0.f);
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
        setFloatUniform(distance, distanceUnfiorm, filterProgram);
    }

    public float getSlope() {
        return slope;
    }

    public void setSlope(float slope) {
        this.slope = slope;
        setFloatUniform(slope, slopeUniform, filterProgram);
    }

    @Override
    public void updateWithInput(float input) {
        setDistance(input);
    }

    @Override
    public float getCurrentValue() {
        return distance;
    }
}
