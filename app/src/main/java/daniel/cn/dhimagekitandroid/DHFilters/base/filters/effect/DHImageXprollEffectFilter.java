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

public class DHImageXprollEffectFilter extends DHImageEffectFilter {
    private static String DH_XPROLL_FRAGMENT_SHADER = "precision lowp float;\n" +
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
            "     \n" +
            "     vec3 texel;\n" +
            "     vec2 tc = (2.0 * textureCoordinate) - 1.0;\n" +
            "     float d = dot(tc, tc);\n" +
            "     vec2 lookup = vec2(d, originalTexel.r);\n" +
            "     texel.r = texture2D(inputImageTexture3, lookup).r;\n" +
            "     lookup.y = originalTexel.g;\n" +
            "     texel.g = texture2D(inputImageTexture3, lookup).g;\n" +
            "     lookup.y = originalTexel.b;\n" +
            "     texel.b\t= texture2D(inputImageTexture3, lookup).b;\n" +
            "     \n" +
            "     vec2 red = vec2(texel.r, 0.16666);\n" +
            "     vec2 green = vec2(texel.g, 0.5);\n" +
            "     vec2 blue = vec2(texel.b, .83333);\n" +
            "     texel.r = texture2D(inputImageTexture2, red).r;\n" +
            "     texel.g = texture2D(inputImageTexture2, green).g;\n" +
            "     texel.b = texture2D(inputImageTexture2, blue).b;\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(originalTexel, texel, strength), 1.0);\n" +
            "     \n" +
            " }";

    private DHImageThreeInputFilter threeInputFilter;
    private DHImagePicture mapPicture;
    private DHImagePicture vignettePicture;

    public DHImageXprollEffectFilter(Context context) {
        threeInputFilter = new DHImageThreeInputFilter(DH_XPROLL_FRAGMENT_SHADER);
        addFilter(threeInputFilter);

        mapPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.xproll_map));
        vignettePicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.vignette_map));

        mapPicture.addTarget(threeInputFilter, 1);
        mapPicture.processImage();

        vignettePicture.addTarget(threeInputFilter, 2);
        vignettePicture.processImage();

        threeInputFilter.disableSecondFrameCheck();
        threeInputFilter.disableThirdFrameCheck();

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(threeInputFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(threeInputFilter);
    }
}
