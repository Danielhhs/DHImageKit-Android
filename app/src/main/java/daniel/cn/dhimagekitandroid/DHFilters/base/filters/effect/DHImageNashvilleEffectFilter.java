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

public class DHImageNashvilleEffectFilter extends DHImageEffectFilter {
    private static String DH_NASHVILLE_FRAGMENT_SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " uniform mediump float strength;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     mediump vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     mediump vec3 mappedTexel = vec3(\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.r, .16666)).r,\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.g, .5)).g,\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.b, .83333)).b);\n" +
            "     gl_FragColor = vec4(mix(texel, mappedTexel, strength), 1.0);\n" +
            " }";

    private DHImageTwoInputFilter twoInputFilter;
    private DHImagePicture picture;

    public DHImageNashvilleEffectFilter(Context context) {
        twoInputFilter = new DHImageTwoInputFilter(DH_NASHVILLE_FRAGMENT_SHADER);

        picture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.nashville_map));
        picture.addTarget(twoInputFilter, 1);
        picture.processImage();

        twoInputFilter.disableSecondFrameCheck();
        addFilter(twoInputFilter);
        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(twoInputFilter);
        setInitialFilters(initialFilters);

        setTerminalFilter(twoInputFilter);
    }
}
