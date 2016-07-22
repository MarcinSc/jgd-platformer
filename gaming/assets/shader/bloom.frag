#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_sourceTexture;
uniform float u_minimalBrightness;
uniform float u_viewportWidth;
uniform float u_viewportHeight;
uniform float u_blurRadius;
uniform float u_bloomStrength;

varying vec2 v_position;

vec3 getBloomForColor(vec3 color) {
    float brightness = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
    if (brightness < u_minimalBrightness) {
        return vec3(0.0);
    }
    return color;
}

vec3 getBloom() {
    vec2 pixelSize = vec2(1.0 / u_viewportWidth, 1.0 / u_viewportHeight);

    vec3 sampleAccum = vec3(0.0);

    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(-0.326212,-0.40581)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(-0.840144,-0.07358)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(-0.695914,0.457137)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(-0.203345,0.620716)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(0.96234,-0.194983)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(0.473434,-0.480026)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(0.519456,0.767022)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(0.185461,-0.893124)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(0.507431,0.064425)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(0.89642,0.412458)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(-0.32194,-0.932615)).rgb);
    sampleAccum += getBloomForColor(texture2D(u_sourceTexture, v_position + pixelSize * u_blurRadius * vec2(-0.791559,-0.59771)).rgb);

    return sampleAccum / 12.0;
}

void main() {
    vec4 color = texture2D(u_sourceTexture, v_position);
    color.rgb += u_bloomStrength * getBloom();
    gl_FragColor = color;
}