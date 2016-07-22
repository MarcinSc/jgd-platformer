#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_sourceTexture;
uniform sampler2D u_tintTexture;
uniform vec2 u_tintTextureOrigin;
uniform vec2 u_tintTextureSize;
uniform float u_factor;

uniform vec2 u_tintShift;
uniform vec2 u_repeatFactor;

varying vec2 v_position;

void main() {
    vec2 sizeMultiplier = u_tintShift + u_repeatFactor * vec2(v_position.x, 1.0 - v_position.y);
    sizeMultiplier = vec2(mod(sizeMultiplier.x, 1.0), mod(sizeMultiplier.y, 1.0));

    // The tintTexture is "upside down" in relation to the coordinates we use
    gl_FragColor = mix(
        texture2D(u_sourceTexture, v_position),
        texture2D(u_tintTexture, u_tintTextureOrigin + sizeMultiplier * u_tintTextureSize),
        u_factor);
}