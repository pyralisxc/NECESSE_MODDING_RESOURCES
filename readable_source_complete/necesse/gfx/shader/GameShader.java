/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL20
 */
package necesse.gfx.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import necesse.gfx.Renderer;
import necesse.gfx.shader.ShaderLoader;
import necesse.gfx.shader.shaderVariable.ShaderVariable;
import org.lwjgl.opengl.GL20;

public class GameShader {
    protected int program;
    protected int vertex;
    protected int fragment;
    private final ArrayList<ShaderVariable<?>> variables = new ArrayList();

    public GameShader(String vertex, String fragment) {
        this.program = GL20.glCreateProgram();
        this.vertex = ShaderLoader.loadVertexShader(vertex);
        this.fragment = ShaderLoader.loadFragmentShader(fragment);
        GL20.glAttachShader((int)this.program, (int)this.vertex);
        GL20.glAttachShader((int)this.program, (int)this.fragment);
        GL20.glLinkProgram((int)this.program);
        GL20.glValidateProgram((int)this.program);
    }

    public int getUniform(String location) {
        return GL20.glGetUniformLocation((int)this.program, (CharSequence)location);
    }

    public int getProgram() {
        return this.program;
    }

    public final void _ScreenUse() {
        GL20.glUseProgram((int)this.program);
    }

    public void use() {
        Renderer.useShader(this);
        this.pass1i("texture", 0);
    }

    public void stop() {
        Renderer.stopShader(this);
    }

    public void delete() {
        GL20.glDeleteProgram((int)this.program);
        GL20.glDeleteShader((int)this.vertex);
        GL20.glDeleteShader((int)this.fragment);
    }

    public ArrayList<ShaderVariable<?>> getVariables() {
        return this.variables;
    }

    protected void addVariable(ShaderVariable<?> variable) {
        this.variables.add(variable);
    }

    public void pass1f(String variable, float v0) {
        GL20.glUniform1f((int)this.getUniform(variable), (float)v0);
    }

    public void pass1fv(String variable, float[] v0) {
        GL20.glUniform1fv((int)this.getUniform(variable), (float[])v0);
    }

    public float get1f(String variable) {
        return GL20.glGetUniformf((int)this.program, (int)this.getUniform(variable));
    }

    public void pass2f(String variable, float v0, float v1) {
        GL20.glUniform2f((int)this.getUniform(variable), (float)v0, (float)v1);
    }

    public float[] get2f(String variable) {
        float[] results = new float[2];
        GL20.glGetUniformfv((int)this.program, (int)this.getUniform(variable), (float[])results);
        return results;
    }

    public void pass3f(String variable, float v0, float v1, float v2) {
        GL20.glUniform3f((int)this.getUniform(variable), (float)v0, (float)v1, (float)v2);
    }

    public float[] get3f(String variable) {
        float[] results = new float[3];
        GL20.glGetUniformfv((int)this.program, (int)this.getUniform(variable), (float[])results);
        return results;
    }

    public void pass4f(String variable, float v0, float v1, float v2, float v3) {
        GL20.glUniform4f((int)this.getUniform(variable), (float)v0, (float)v1, (float)v2, (float)v3);
    }

    public float[] get4f(String variable) {
        float[] results = new float[4];
        GL20.glGetUniformfv((int)this.program, (int)this.getUniform(variable), (float[])results);
        return results;
    }

    public void pass1i(String variable, int v0) {
        GL20.glUniform1i((int)this.getUniform(variable), (int)v0);
    }

    public int get1i(String variable) {
        return GL20.glGetUniformi((int)this.program, (int)this.getUniform(variable));
    }

    public void pass2i(String variable, int v0, int v1) {
        GL20.glUniform2i((int)this.getUniform(variable), (int)v0, (int)v1);
    }

    public int[] get2i(String variable) {
        int[] results = new int[2];
        GL20.glGetUniformiv((int)this.program, (int)this.getUniform(variable), (int[])results);
        return results;
    }

    public void pass3i(String variable, int v0, int v1, int v2) {
        GL20.glUniform3i((int)this.getUniform(variable), (int)v0, (int)v1, (int)v2);
    }

    public int[] get3i(String variable) {
        int[] results = new int[3];
        GL20.glGetUniformiv((int)this.program, (int)this.getUniform(variable), (int[])results);
        return results;
    }

    public void pass4i(String variable, int v0, int v1, int v2, int v3) {
        GL20.glUniform4i((int)this.getUniform(variable), (int)v0, (int)v1, (int)v2, (int)v3);
    }

    public int[] get4i(String variable) {
        int[] results = new int[4];
        GL20.glGetUniformiv((int)this.program, (int)this.getUniform(variable), (int[])results);
        return results;
    }

    public void pass1(String variable, FloatBuffer values) {
        GL20.glUniform1fv((int)this.getUniform(variable), (FloatBuffer)values);
    }

    public void pass1(String variable, IntBuffer values) {
        GL20.glUniform1iv((int)this.getUniform(variable), (IntBuffer)values);
    }

    public void pass2(String variable, FloatBuffer values) {
        GL20.glUniform2fv((int)this.getUniform(variable), (FloatBuffer)values);
    }

    public void pass2(String variable, IntBuffer values) {
        GL20.glUniform1iv((int)this.getUniform(variable), (IntBuffer)values);
    }

    public void pass3(String variable, FloatBuffer values) {
        GL20.glUniform2fv((int)this.getUniform(variable), (FloatBuffer)values);
    }

    public void pass3(String variable, IntBuffer values) {
        GL20.glUniform1iv((int)this.getUniform(variable), (IntBuffer)values);
    }

    public void pass4(String variable, FloatBuffer values) {
        GL20.glUniform2fv((int)this.getUniform(variable), (FloatBuffer)values);
    }

    public void pass4(String variable, IntBuffer values) {
        GL20.glUniform1iv((int)this.getUniform(variable), (IntBuffer)values);
    }
}

