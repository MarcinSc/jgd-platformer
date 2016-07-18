#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_sourceTexture;
uniform float u_factor;

varying vec2 v_position;

void main() {
    vec4 result = texture2D(u_sourceTexture, v_position);
    result.r = pow(result.r, 1.0/u_factor);
    result.g = pow(result.g, 1.0/u_factor);
    result.b = pow(result.b, 1.0/u_factor);
    gl_FragColor = result;
}