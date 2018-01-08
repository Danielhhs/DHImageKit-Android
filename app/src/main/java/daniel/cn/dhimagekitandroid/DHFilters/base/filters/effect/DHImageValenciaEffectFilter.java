package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageThreeInputFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageValenciaEffectFilter extends DHImageEffectFilter {
    private static String DH_VALENCIA_FRAGMENT_SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2; //map\n" +
            " uniform sampler2D inputImageTexture3; //gradMap\n" +
            " \n" +
            " uniform mediump float strength;\n" +
            " \n" +
            " mat3 saturateMatrix = mat3(\n" +
            "                            1.1402,\n" +
            "                            -0.0598,\n" +
            "                            -0.061,\n" +
            "                            -0.1174,\n" +
            "                            1.0826,\n" +
            "                            -0.1186,\n" +
            "                            -0.0228,\n" +
            "                            -0.0228,\n" +
            "                            1.1772);\n" +
            " \n" +
            " vec3 lumaCoeffs = vec3(.3, .59, .11);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     vec3 originalTexel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     \n" +
            "     vec3 texel = vec3(\n" +
            "                  texture2D(inputImageTexture2, vec2(originalTexel.r, .1666666)).r,\n" +
            "                  texture2D(inputImageTexture2, vec2(originalTexel.g, .5)).g,\n" +
            "                  texture2D(inputImageTexture2, vec2(originalTexel.b, .8333333)).b\n" +
            "                  );\n" +
            "     \n" +
            "     texel = saturateMatrix * texel;\n" +
            "     float luma = dot(lumaCoeffs, texel);\n" +
            "     texel = vec3(\n" +
            "                  texture2D(inputImageTexture3, vec2(luma, texel.r)).r,\n" +
            "                  texture2D(inputImageTexture3, vec2(luma, texel.g)).g,\n" +
            "                  texture2D(inputImageTexture3, vec2(luma, texel.b)).b);\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(originalTexel, texel, strength), 1.0);\n" +
            " }";

    private DHImageThreeInputFilter threeInputFilter;
    private DHImagePicture mapPicture;
    private DHImagePicture gradientMapPicture;

    public DHImageValenciaEffectFilter(Context context) {
        threeInputFilter = new DHImageThreeInputFilter(DH_VALENCIA_FRAGMENT_SHADER);
        addFilter(threeInputFilter);

        mapPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.valencia_map));
        gradientMapPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.valencia_gradient_map));

        mapPicture.addTarget(threeInputFilter, 1);
        mapPicture.processImage();
        gradientMapPicture.addTarget(threeInputFilter, 2);
        gradientMapPicture.processImage();

        threeInputFilter.disableSecondFrameCheck();
        threeInputFilter.disableThirdFrameCheck();

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(threeInputFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(threeInputFilter);
    }
}
