#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_sourceTexture;
uniform float u_viewportWidth;
uniform float u_viewportHeight;
uniform float u_blurRadius;

varying vec2 v_position;

void main() {
    vec2 textelSize = vec2(1.0 / u_viewportWidth, 1.0 / u_viewportHeight);

    vec4 sampleAccum = vec4(0.0, 0.0, 0.0, 0.0);

    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(-0.326212,-0.40581));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(-0.840144,-0.07358));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(-0.695914,0.457137));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(-0.203345,0.620716));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(0.96234,-0.194983));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(0.473434,-0.480026));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(0.519456,0.767022));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(0.185461,-0.893124));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(0.507431,0.064425));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(0.89642,0.412458));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(-0.32194,-0.932615));
    sampleAccum += texture2D(u_sourceTexture, v_position + textelSize * u_blurRadius * vec2(-0.791559,-0.59771));

    gl_FragColor = sampleAccum / 12.0;
}