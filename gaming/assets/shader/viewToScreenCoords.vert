attribute vec3 a_position;

varying vec2 v_position;

void main() {
    v_position = (a_position.xy + 1.0) / 2.0;
    gl_Position =  vec4(a_position, 1.0);
}