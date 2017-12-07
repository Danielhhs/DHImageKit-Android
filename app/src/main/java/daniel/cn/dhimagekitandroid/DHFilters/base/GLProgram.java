package daniel.cn.dhimagekitandroid.DHFilters.base;

import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanghongsen on 2017/12/7.
 */

public class GLProgram {
    private int program;
    private int vertexShader;
    private int fragmentShader;
    private List<String> attributes;
    private List<String> uniforms;

    private boolean initialized;
    private String vertexShaderLog;
    private String fragmentShaderLog;
    private String programLog;

    public GLProgram(String vertexShaderString, String fragmentShaderString) {
        initialized = false;
        attributes = new ArrayList<>();
        uniforms = new ArrayList<>();

        program = GLES20.glCreateProgram();
        vertexShader = compileShader(vertexShaderString, GLES20.GL_VERTEX_SHADER);
        if (vertexShader == -1) {
            Log.e("DHImage", "Error while compiling vertex shader");
        }
        fragmentShader = compileShader(fragmentShaderString, GLES20.GL_FRAGMENT_SHADER);
        if (fragmentShader == -1) {
            Log.e("DHImage", "Error while compiling fragment shader");
        }

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
    }

    public void addAttribute(String attribute) {
        if (!attributes.contains(attribute)) {
            attributes.add(attribute);
            GLES20.glBindAttribLocation(program, attributes.indexOf(attribute), attribute);
        }
    }

    public int getAttributeIndex(String attribute) {
        return attributes.indexOf(attribute);
    }

    public int getUniformIndex(String uniform) {
        return uniforms.indexOf(uniform);
    }

    public boolean link() {
        int[] status = new int[1];
        GLES20.glLinkProgram(program);
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] == GLES20.GL_FALSE) {
            return false;
        }
        if (vertexShader != 0) {
            GLES20.glDeleteShader(vertexShader);
            vertexShader = 0;
        }
        if (fragmentShader != 0) {
            GLES20.glDeleteShader(fragmentShader);
            fragmentShader = 0;
        }
        initialized = true;
        return true;
    }

    public void use() {
        GLES20.glUseProgram(program);
    }

    public void validate() {
        int []logLength = new int[1];
        GLES20.glValidateProgram(program);
        GLES20.glGetProgramiv(program, GLES20.GL_INFO_LOG_LENGTH, logLength, 0);
        if (logLength[0] > 0) {
            programLog = GLES20.glGetProgramInfoLog(program);
        }
    }

    //PRIVATE HELPER
    private int compileShader(String shaderString, int shaderType) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader, shaderString);
        GLES20.glCompileShader(shader);
        int []status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            if (shaderType == GLES20.GL_VERTEX_SHADER) {
                vertexShaderLog = GLES20.glGetShaderInfoLog(shader);
            } else {
                fragmentShaderLog = GLES20.glGetShaderInfoLog(shader);
            }
            shader = 0;
        }
        return shader;
    }

    public void onDestroy() {

    }
}
