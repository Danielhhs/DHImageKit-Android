package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFourInputFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/5.
 */

public class DHImageRiseEffectFilter extends DHImageEffectFilter {

    private static String DH_RISE_EFFECT_FRAGMENT_SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2; //blowout;\n" +
            " uniform sampler2D inputImageTexture3; //overlay;\n" +
            " uniform sampler2D inputImageTexture4; //map\n" +
            " \n" +
            " uniform mediump float strength;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     \n" +
            "     mediump vec4 texel = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     mediump vec3 bbTexel = texture2D(inputImageTexture2, textureCoordinate).rgb;\n" +
            "     \n" +
            "     texel.r = texture2D(inputImageTexture3, vec2(bbTexel.r, texel.r)).r;\n" +
            "     texel.g = texture2D(inputImageTexture3, vec2(bbTexel.g, texel.g)).g;\n" +
            "     texel.b = texture2D(inputImageTexture3, vec2(bbTexel.b, texel.b)).b;\n" +
            "     \n" +
            "     vec4 mapped;\n" +
            "     mapped.r = texture2D(inputImageTexture4, vec2(texel.r, .16666)).r;\n" +
            "     mapped.g = texture2D(inputImageTexture4, vec2(texel.g, .5)).g;\n" +
            "     mapped.b = texture2D(inputImageTexture4, vec2(texel.b, .83333)).b;\n" +
            "     mapped.a = 1.0;\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(texel.rgb, mapped.rgb, strength), mapped.a);\n" +
            " }";

    private DHImageFourInputFilter fourInputFilter;
    private DHImagePicture blowoutPicture, overlayPicture, mapPicture;

    public DHImageRiseEffectFilter(Context context) {
        fourInputFilter = new DHImageFourInputFilter(DH_RISE_EFFECT_FRAGMENT_SHADER);

        blowoutPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.blackboard1024));
        overlayPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.overlay_map));
        mapPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.rise_map));

        blowoutPicture.addTarget(fourInputFilter, 1);
        blowoutPicture.processImage();

        overlayPicture.addTarget(fourInputFilter, 2);
        overlayPicture.processImage();

        mapPicture.addTarget(fourInputFilter, 3);
        mapPicture.processImage();
        addFilter(fourInputFilter);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(fourInputFilter);

        setInitialFilters(initialFilters);
        setTerminalFilter(fourInputFilter);
    }
}
