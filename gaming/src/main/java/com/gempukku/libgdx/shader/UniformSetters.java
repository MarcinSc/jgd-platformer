package com.gempukku.libgdx.shader;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class UniformSetters {
    private UniformSetters() {
    }

    public final static UniformRegistry.UniformSetter projTrans = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, shader.getCamera().projection);
        }
    };
    public final static UniformRegistry.UniformSetter viewTrans = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, shader.getCamera().view);
        }
    };
    public final static UniformRegistry.UniformSetter projViewTrans = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, shader.getCamera().combined);
        }
    };
    public final static UniformRegistry.UniformSetter cameraPosition = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, shader.getCamera().position.x, shader.getCamera().position.y, shader.getCamera().position.z,
                    1.1881f / (shader.getCamera().far * shader.getCamera().far));
        }
    };
    public final static UniformRegistry.UniformSetter cameraDirection = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, shader.getCamera().direction);
        }
    };
    public final static UniformRegistry.UniformSetter cameraUp = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, shader.getCamera().up);
        }
    };
    public final static UniformRegistry.UniformSetter cameraNearFar = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, shader.getCamera().near, shader.getCamera().far);
        }
    };
    public final static UniformRegistry.UniformSetter worldTrans = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, renderable.worldTransform);
        }
    };
    public final static UniformRegistry.UniformSetter viewWorldTrans = new UniformRegistry.UniformSetter() {
        final Matrix4 temp = new Matrix4();

        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, temp.set(shader.getCamera().view).mul(renderable.worldTransform));
        }
    };
    public final static UniformRegistry.UniformSetter projViewWorldTrans = new UniformRegistry.UniformSetter() {
        final Matrix4 temp = new Matrix4();

        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, temp.set(shader.getCamera().combined).mul(renderable.worldTransform));
        }
    };
    public final static UniformRegistry.UniformSetter normalMatrix = new UniformRegistry.UniformSetter() {
        private final Matrix3 tmpM = new Matrix3();

        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, tmpM.set(renderable.worldTransform).inv().transpose());
        }
    };

    public static class Bones implements UniformRegistry.UniformSetter {
        private final static Matrix4 idtMatrix = new Matrix4();
        public final float bones[];

        public Bones(final int numBones) {
            this.bones = new float[numBones * 16];
        }

        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            for (int i = 0; i < bones.length; i++) {
                final int idx = i / 16;
                bones[i] = (renderable.bones == null || idx >= renderable.bones.length || renderable.bones[idx] == null) ? idtMatrix.val[i % 16]
                        : renderable.bones[idx].val[i % 16];
            }
            shader.setUniformMatrixArray(location, bones);
        }
    }

    public final static UniformRegistry.UniformSetter shininess = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, ((FloatAttribute) (combinedAttributes.get(FloatAttribute.Shininess))).value);
        }
    };
    public final static UniformRegistry.UniformSetter diffuseColor = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, ((ColorAttribute) (combinedAttributes.get(ColorAttribute.Diffuse))).color);
        }
    };
    public final static UniformRegistry.UniformSetter diffuseTexture = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final int unit = shader.getContext().textureBinder.bind(((TextureAttribute) (combinedAttributes
                    .get(TextureAttribute.Diffuse))).textureDescription);
            shader.setUniform(location, unit);
        }
    };
    public final static UniformRegistry.UniformSetter diffuseUVTransform = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final TextureAttribute ta = (TextureAttribute) (combinedAttributes.get(TextureAttribute.Diffuse));
            shader.setUniform(location, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
        }
    };
    public final static UniformRegistry.UniformSetter specularColor = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, ((ColorAttribute) (combinedAttributes.get(ColorAttribute.Specular))).color);
        }
    };
    public final static UniformRegistry.UniformSetter specularTexture = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final int unit = shader.getContext().textureBinder.bind(((TextureAttribute) (combinedAttributes
                    .get(TextureAttribute.Specular))).textureDescription);
            shader.setUniform(location, unit);
        }
    };
    public final static UniformRegistry.UniformSetter specularUVTransform = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final TextureAttribute ta = (TextureAttribute) (combinedAttributes.get(TextureAttribute.Specular));
            shader.setUniform(location, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
        }
    };
    public final static UniformRegistry.UniformSetter emissiveColor = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, ((ColorAttribute) (combinedAttributes.get(ColorAttribute.Emissive))).color);
        }
    };
    public final static UniformRegistry.UniformSetter emissiveTexture = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final int unit = shader.getContext().textureBinder.bind(((TextureAttribute) (combinedAttributes
                    .get(TextureAttribute.Emissive))).textureDescription);
            shader.setUniform(location, unit);
        }
    };
    public final static UniformRegistry.UniformSetter emissiveUVTransform = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final TextureAttribute ta = (TextureAttribute) (combinedAttributes.get(TextureAttribute.Emissive));
            shader.setUniform(location, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
        }
    };
    public final static UniformRegistry.UniformSetter reflectionColor = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, ((ColorAttribute) (combinedAttributes.get(ColorAttribute.Reflection))).color);
        }
    };
    public final static UniformRegistry.UniformSetter reflectionTexture = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final int unit = shader.getContext().textureBinder.bind(((TextureAttribute) (combinedAttributes
                    .get(TextureAttribute.Reflection))).textureDescription);
            shader.setUniform(location, unit);
        }
    };
    public final static UniformRegistry.UniformSetter reflectionUVTransform = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final TextureAttribute ta = (TextureAttribute) (combinedAttributes.get(TextureAttribute.Reflection));
            shader.setUniform(location, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
        }
    };
    public final static UniformRegistry.UniformSetter normalTexture = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final int unit = shader.getContext().textureBinder.bind(((TextureAttribute) (combinedAttributes
                    .get(TextureAttribute.Normal))).textureDescription);
            shader.setUniform(location, unit);
        }
    };
    public final static UniformRegistry.UniformSetter normalUVTransform = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final TextureAttribute ta = (TextureAttribute) (combinedAttributes.get(TextureAttribute.Normal));
            shader.setUniform(location, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
        }
    };
    public final static UniformRegistry.UniformSetter ambientTexture = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final int unit = shader.getContext().textureBinder.bind(((TextureAttribute) (combinedAttributes
                    .get(TextureAttribute.Ambient))).textureDescription);
            shader.setUniform(location, unit);
        }
    };
    public final static UniformRegistry.UniformSetter ambientUVTransform = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final TextureAttribute ta = (TextureAttribute) (combinedAttributes.get(TextureAttribute.Ambient));
            shader.setUniform(location, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
        }
    };

    public static class ACubemap implements UniformRegistry.UniformSetter {
        private final static float ones[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        private final AmbientCubemap cacheAmbientCubemap = new AmbientCubemap();
        private final static Vector3 tmpV1 = new Vector3();
        public final int dirLightsOffset;
        public final int pointLightsOffset;

        public ACubemap(final int dirLightsOffset, final int pointLightsOffset) {
            this.dirLightsOffset = dirLightsOffset;
            this.pointLightsOffset = pointLightsOffset;
        }

        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            if (renderable.environment == null)
                shader.setUniformArray(location, ones);
            else {
                renderable.worldTransform.getTranslation(tmpV1);
                if (combinedAttributes.has(ColorAttribute.AmbientLight))
                    cacheAmbientCubemap.set(((ColorAttribute) combinedAttributes.get(ColorAttribute.AmbientLight)).color);

                if (combinedAttributes.has(DirectionalLightsAttribute.Type)) {
                    Array<DirectionalLight> lights = ((DirectionalLightsAttribute) combinedAttributes
                            .get(DirectionalLightsAttribute.Type)).lights;
                    for (int i = dirLightsOffset; i < lights.size; i++)
                        cacheAmbientCubemap.add(lights.get(i).color, lights.get(i).direction);
                }

                if (combinedAttributes.has(PointLightsAttribute.Type)) {
                    Array<PointLight> lights = ((PointLightsAttribute) combinedAttributes.get(PointLightsAttribute.Type)).lights;
                    for (int i = pointLightsOffset; i < lights.size; i++)
                        cacheAmbientCubemap.add(lights.get(i).color, lights.get(i).position, tmpV1, lights.get(i).intensity);
                }

                cacheAmbientCubemap.clamp();
                shader.setUniformArray(location, cacheAmbientCubemap.data);
            }
        }
    }

    public final static UniformRegistry.UniformSetter environmentCubemap = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            if (combinedAttributes.has(CubemapAttribute.EnvironmentMap)) {
                shader.setUniform(location, shader.getContext().textureBinder.bind(((CubemapAttribute) combinedAttributes
                        .get(CubemapAttribute.EnvironmentMap)).textureDescription));
            }
        }
    };

    public final static UniformRegistry.UniformSetter fog = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, ((ColorAttribute) combinedAttributes.get(ColorAttribute.Fog)).color);
        }
    };

    public final static UniformRegistry.UniformSetter blending = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            BlendingAttribute blendingAttribute = (BlendingAttribute) combinedAttributes.get(BlendingAttribute.Type);
            shader.getContext().setBlending(true, blendingAttribute.sourceFunction, blendingAttribute.destFunction);
            shader.setUniform(location, blendingAttribute.opacity);
        }
    };

    public final static UniformRegistry.UniformSetter alphaTest = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            FloatAttribute alphaTestAttribute = (FloatAttribute) combinedAttributes.get(FloatAttribute.AlphaTest);
            shader.setUniform(location, alphaTestAttribute.value);
        }
    };
}
