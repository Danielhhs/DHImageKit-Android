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
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageAmaroEffectFilter extends DHImageEffectFilter {

    private static String DH_AMARO_FRAGMENT_SHADER = "precision lowp float;\n" +
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
            "     vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     vec4 texel = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     vec3 bbTexel = texture2D(inputImageTexture2, textureCoordinate).rgb;\n" +
            "     \n" +
            "     vec3 vignetted;\n" +
            "     vignetted.r = texture2D(inputImageTexture3, vec2(bbTexel.r, texel.r)).r;\n" +
            "     vignetted.g = texture2D(inputImageTexture3, vec2(bbTexel.g, texel.g)).g;\n" +
            "     vignetted.b = texture2D(inputImageTexture3, vec2(bbTexel.b, texel.b)).b;\n" +
            "     \n" +
            "     vec4 mapped;\n" +
            "     mapped.r = texture2D(inputImageTexture4, vec2(vignetted.r, .16666)).r;\n" +
            "     mapped.g = texture2D(inputImageTexture4, vec2(vignetted.g, .5)).g;\n" +
            "     mapped.b = texture2D(inputImageTexture4, vec2(vignetted.b, .83333)).b;\n" +
            "     mapped.a = 1.0;\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(textureColor.rgb, mapped.rgb, strength), 1.0);\n" +
            " }";

    private DHImageFourInputFilter fourInputFilter;
    private DHImagePicture blowoutPicture;
    private DHImagePicture overlayPicture;
    private DHImagePicture mapPicture;

    public DHImageAmaroEffectFilter(Context context) {
        fourInputFilter = new DHImageFourInputFilter(DH_AMARO_FRAGMENT_SHADER);
        addFilter(fourInputFilter);

        blowoutPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.blackboard1024));
        overlayPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.overlay_map));
        mapPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.amaro_map));

        blowoutPicture.addTarget(fourInputFilter, 1);;
        blowoutPicture.processImage();;

        overlayPicture.addTarget(fourInputFilter, 2);
        overlayPicture.processImage();

        mapPicture.addTarget(fourInputFilter, 3);
        mapPicture.processImage();

        fourInputFilter.disableSecondFrameCheck();
        fourInputFilter.disableThirdFrameCheck();
        fourInputFilter.disableFourthFrameCheck();

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(fourInputFilter);
        setInitialFilters(initialFilters);

        setTerminalFilter(fourInputFilter);
    }
}
