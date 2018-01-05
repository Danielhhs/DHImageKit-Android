package daniel.cn.dhimagekitandroid.DHFilters.base.filters.effect;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageSixInputFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;
import daniel.cn.dhimagekitandroid.R;

/**
 * Created by huanghongsen on 2018/1/5.
 */

public class DHImageBrannanEffectFilter extends DHImageEffectFilter {
    private static String DH_BRANNAN_SIX_INPUT_TEXTURE_FRAGMENT_SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;  //process\n" +
            " uniform sampler2D inputImageTexture3;  //blowout\n" +
            " uniform sampler2D inputImageTexture4;  //contrast\n" +
            " uniform sampler2D inputImageTexture5;  //luma\n" +
            " uniform sampler2D inputImageTexture6;  //screen\n" +
            " \n" +
            " uniform mediump float strength;\n" +
            " \n" +
            " mat3 saturateMatrix = mat3(\n" +
            "                            1.105150,\n" +
            "                            -0.044850,\n" +
            "                            -0.046000,\n" +
            "                            -0.088050,\n" +
            "                            1.061950,\n" +
            "                            -0.089200,\n" +
            "                            -0.017100,\n" +
            "                            -0.017100,\n" +
            "                            1.132900);\n" +
            " \n" +
            " vec3 luma = vec3(.3, .59, .11);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     \n" +
            "     vec3 originalTexel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     vec3 texel;\n" +
            "     vec2 lookup;\n" +
            "     lookup.y = 0.5;\n" +
            "     lookup.x = originalTexel.r;\n" +
            "     texel.r = texture2D(inputImageTexture2, lookup).r;\n" +
            "     lookup.x = originalTexel.g;\n" +
            "     texel.g = texture2D(inputImageTexture2, lookup).g;\n" +
            "     lookup.x = originalTexel.b;\n" +
            "     texel.b = texture2D(inputImageTexture2, lookup).b;\n" +
            "     \n" +
            "     texel = saturateMatrix * texel;\n" +
            "     \n" +
            "     \n" +
            "     vec2 tc = (2.0 * textureCoordinate) - 1.0;\n" +
            "     float d = dot(tc, tc);\n" +
            "     vec3 sampled;\n" +
            "     lookup.y = 0.5;\n" +
            "     lookup.x = texel.r;\n" +
            "     sampled.r = texture2D(inputImageTexture3, lookup).r;\n" +
            "     lookup.x = texel.g;\n" +
            "     sampled.g = texture2D(inputImageTexture3, lookup).g;\n" +
            "     lookup.x = texel.b;\n" +
            "     sampled.b = texture2D(inputImageTexture3, lookup).b;\n" +
            "     float value = smoothstep(0.0, 1.0, d);\n" +
            "     texel = mix(sampled, texel, value);\n" +
            "     \n" +
            "     lookup.x = texel.r;\n" +
            "     texel.r = texture2D(inputImageTexture4, lookup).r;\n" +
            "     lookup.x = texel.g;\n" +
            "     texel.g = texture2D(inputImageTexture4, lookup).g;\n" +
            "     lookup.x = texel.b;\n" +
            "     texel.b = texture2D(inputImageTexture4, lookup).b;\n" +
            "     \n" +
            "     \n" +
            "     lookup.x = dot(texel, luma);\n" +
            "     texel = mix(texture2D(inputImageTexture5, lookup).rgb, texel, .5);\n" +
            "     \n" +
            "     lookup.x = texel.r;\n" +
            "     texel.r = texture2D(inputImageTexture6, lookup).r;\n" +
            "     lookup.x = texel.g;\n" +
            "     texel.g = texture2D(inputImageTexture6, lookup).g;\n" +
            "     lookup.x = texel.b;\n" +
            "     texel.b = texture2D(inputImageTexture6, lookup).b;\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(originalTexel, texel, strength), 1.0);\n" +
            " }";

    private DHImagePicture processPicture, blowOutPicture, contrastPicture, lumaPicture, screenPicture;
    private DHImageSixInputFilter sixInputFilter;

    public DHImageBrannanEffectFilter(Context context) {
        sixInputFilter = new DHImageSixInputFilter(DH_BRANNAN_SIX_INPUT_TEXTURE_FRAGMENT_SHADER);
        addFilter(sixInputFilter);

        processPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.brannan_process));
        processPicture.addTarget(sixInputFilter, 1);
        processPicture.processImage();

        blowOutPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.brannan_blowout));
        blowOutPicture.addTarget(sixInputFilter, 2);
        blowOutPicture.processImage();

        contrastPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.brannan_contrast));
        contrastPicture.addTarget(sixInputFilter, 3);
        contrastPicture.processImage();

        lumaPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.brannan_luma));
        lumaPicture.addTarget(sixInputFilter, 4);
        lumaPicture.processImage();

        screenPicture = new DHImagePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.brannan_screen));
        screenPicture.addTarget(sixInputFilter, 5);
        screenPicture.processImage();

        sixInputFilter.disableSecondFrameCheck();
        sixInputFilter.disableThirdFrameCheck();
        sixInputFilter.disableFourthFrameCheck();
        sixInputFilter.disableFifthFrameCheck();
        sixInputFilter.disableSixthFrameCheck();

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(sixInputFilter);
        setInitialFilters(initialFilters);
        setTerminalFilter(sixInputFilter);
    }
}
