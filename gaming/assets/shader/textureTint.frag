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
    // The tintTexture is "upside down" in relation to the coordinates we use
    gl_FragColor = mix(
        texture2D(u_sourceTexture, v_position),
        texture2D(u_tintTexture, u_tintTextureOrigin + vec2(v_position.x, 1.0 - v_position.y) * u_tintTextureSize),
        u_factor);
}