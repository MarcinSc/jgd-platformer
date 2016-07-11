attribute vec3 a_position;

uniform float u_backgroundImageStartX;
uniform float u_backgroundImageStartY;
uniform float u_backgroundImageWidth;
uniform float u_backgroundImageHeight;

varying vec2 v_position;

void main() {
    vec2 position = (a_position.xy + 1.0) / 2.0;
    v_position = position * vec2(u_backgroundImageWidth, u_backgroundImageHeight)+vec2(u_backgroundImageStartX, u_backgroundImageStartY);
    gl_Position =  vec4(a_position, 1.0);
}