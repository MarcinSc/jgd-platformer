attribute vec3 a_position;

uniform float u_leftEdge;
uniform float u_topEdge;
uniform float u_rightEdge;
uniform float u_bottomEdge;

varying vec2 v_position;

void main() {
    vec2 position = (a_position.xy + 1.0) / 2.0;
    v_position = position * vec2(u_rightEdge-u_leftEdge, u_topEdge-u_bottomEdge) + vec2(u_leftEdge, u_bottomEdge);
    gl_Position =  vec4(a_position, 1.0);
}