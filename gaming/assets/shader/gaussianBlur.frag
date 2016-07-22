#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_sourceTexture;
uniform float u_viewportWidth;
uniform float u_viewportHeight;
uniform int u_blurRadius;
uniform float u_kernel[17];
uniform int u_vertical;

varying vec2 v_position;

void main() {
    vec2 pixelSize = vec2(1.0 / u_viewportWidth, 1.0 / u_viewportHeight);

    vec4 sampleAccum = vec4(0.0, 0.0, 0.0, 0.0);

    for (int i=0; i<=u_blurRadius; i++) {
        float kernel = u_kernel[i];
        if (u_vertical == 1) {
            sampleAccum += texture2D(u_sourceTexture, v_position + pixelSize * vec2(0, i)) * kernel;
            if (i>0) {
                sampleAccum += texture2D(u_sourceTexture, v_position - pixelSize * vec2(0, i)) * kernel;
            }
        } else {
            sampleAccum += texture2D(u_sourceTexture, v_position + pixelSize * vec2(i, 0)) * kernel;
            if (i>0) {
                sampleAccum += texture2D(u_sourceTexture, v_position - pixelSize * vec2(i, 0)) * kernel;
            }
        }
    }

    gl_FragColor = sampleAccum;
}