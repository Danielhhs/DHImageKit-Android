package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageTwoInputFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageKelvinEffectFilter extends DHImageEffectFilter {
    private static String DH_KELVIN_FRAGMENT_SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " uniform mediump float strength;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     vec3 originalTexel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     \n" +
            "     vec3 texel;\n" +
            "     vec2 lookup;\n" +
            "     lookup.y = .5;\n" +
            "     \n" +
            "     lookup.x = originalTexel.r;\n" +
            "     texel.r = texture2D(inputImageTexture2, lookup).r;\n" +
            "     \n" +
            "     lookup.x = originalTexel.g;\n" +
            "     texel.g = texture2D(inputImageTexture2, lookup).g;\n" +
            "     \n" +
            "     lookup.x = originalTexel.b;\n" +
            "     texel.b = texture2D(inputImageTexture2, lookup).b;\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(originalTexel, texel, strength), 1.0);\n" +
            " }";

    private DHImageTwoInputFilter twoInputFilter;
    private DHImagePicture mapPicture;

    public DHImageKelvinEffectFilter(Context context) {
        twoInputFilter = new DHImageTwoInputFilter(DH_KELVIN_FRAGMENT_SHADER);
        addFilter(twoInputFilter);

        mapPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.kelvin_map));
        mapPicture.addTarget(twoInputFilter, 1);
        mapPicture.processImage();

        twoInputFilter.disableSecondFrameCheck();

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(twoInputFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(twoInputFilter);
    }
}
