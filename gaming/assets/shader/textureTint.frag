#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_sourceTexture;
uniform sampler2D u_tintTexture;
uniform vec2 u_tintTextureOrigin;
uniform vec2 u_tintTextureSize;
uniform float u_factor;

varying vec2 v_position;

void main() {
    gl_FragColor = mix(texture2D(u_sourceTexture, v_position), texture2D(u_tintTexture, u_tintTextureOrigin + v_position * u_tintTextureSize), u_factor);
}