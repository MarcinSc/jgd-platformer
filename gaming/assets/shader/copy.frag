#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_sourceTexture;

varying vec2 v_position;

void main() {
    gl_FragColor = texture2D(u_sourceTexture, v_position);
}