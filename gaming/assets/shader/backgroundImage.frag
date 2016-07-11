#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_backgroundTexture;

varying vec2 v_position;

void main() {
    //gl_FragColor = vec4(v_position.x, v_position.x, v_position.x, 1.0);
    gl_FragColor = texture2D(u_backgroundTexture, v_position);
}