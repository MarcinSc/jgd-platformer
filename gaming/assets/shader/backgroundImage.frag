#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_backgroundTexture;
uniform vec3 u_backgroundColor;

// Actual TextureRegion coordinates
uniform float u_imageStartX;
uniform float u_imageStartY;
uniform float u_imageWidth;
uniform float u_imageHeight;

varying vec2 v_position;

void main() {
    if (v_position.x<0.0 ||v_position.x>1.0 || v_position.y<0.0 || v_position.y>1.0) {
        gl_FragColor = vec4(u_backgroundColor.r, u_backgroundColor.g, u_backgroundColor.b, 1.0);
    } else {
        gl_FragColor = texture2D(u_backgroundTexture, v_position * vec2(u_imageWidth, u_imageHeight) + vec2(u_imageStartX, u_imageStartY));
    }
}