package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;
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

public class DHImageWaldenEffectFilter extends DHImageEffectFilter {
    private static String DH_WALDEN_FRAGMENT_SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2; //map\n" +
            " uniform sampler2D inputImageTexture3; //vigMap\n" +
            " \n" +
            " uniform mediump float strength;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     \n" +
            "     vec3 originalTexel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "    \n" +
            "     vec3 texel = vec3(\n" +
            "                  texture2D(inputImageTexture2, vec2(originalTexel.r, .16666)).r,\n" +
            "                  texture2D(inputImageTexture2, vec2(originalTexel.g, .5)).g,\n" +
            "                  texture2D(inputImageTexture2, vec2(originalTexel.b, .83333)).b);\n" +
            "     \n" +
            "     vec2 tc = (2.0 * textureCoordinate) - 1.0;\n" +
            "     float d = dot(tc, tc);\n" +
            "     vec2 lookup = vec2(d, texel.r);\n" +
            "     texel.r = texture2D(inputImageTexture3, lookup).r;\n" +
            "     lookup.y = texel.g;\n" +
            "     texel.g = texture2D(inputImageTexture3, lookup).g;\n" +
            "     lookup.y = texel.b;\n" +
            "     texel.b\t= texture2D(inputImageTexture3, lookup).b;\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(originalTexel, texel, strength), 1.0);\n" +
            " }";

    private DHImageThreeInputFilter threeInputFilter;
    private DHImagePicture mapPicture;
    private DHImagePicture vignettePicture;

    public DHImageWaldenEffectFilter(Context context) {
        threeInputFilter = new DHImageThreeInputFilter(DH_WALDEN_FRAGMENT_SHADER);
        addFilter(threeInputFilter);

        mapPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.walden_map));
        vignettePicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.vignette_map));

        mapPicture.addTarget(threeInputFilter, 1);
        mapPicture.processImage();

        vignettePicture.addTarget(threeInputFilter, 2);
        mapPicture.processImage();

        threeInputFilter.disableSecondFrameCheck();
        threeInputFilter.disableThirdFrameCheck();

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(threeInputFilter);
        setInitialFilters(initialFilters);

        setTerminalFilter(threeInputFilter);
    }
}
