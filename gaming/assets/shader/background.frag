#ifdef GL_ES
precision mediump float;
#endif

uniform vec3 u_backgroundColor;

void main() {
    gl_FragColor = vec4(u_backgroundColor, 1.0);
}