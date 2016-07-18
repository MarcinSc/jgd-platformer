#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_sourceTexture;
uniform vec4 u_color;
uniform float u_factor;

varying vec2 v_position;

void main() {
    gl_FragColor = mix(texture2D(u_sourceTexture, v_position), u_color, u_factor);
}