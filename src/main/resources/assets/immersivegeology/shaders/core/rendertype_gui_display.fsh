#version 150

#moj_import <fog.glsl> // Import fog functionality
#moj_import <light.glsl> // Import lighting functionality

uniform sampler2D Sampler0; // Texture sampler
uniform vec4 ColorModulator; // Color modulator
uniform float Alpha;
uniform vec4 TextureMat;

in vec3 fPosition; // The local position passed from the vertex shader
in vec4 fColor; // The color passed from the vertex shader
in vec2 texCoord0; // Block Textures
in vec3 fNormal; // The normal vector of the fragment
out vec4 fragColor; // The final color output

void main() {
    // Sample the texture and apply color modulation
    vec4 sampledTexture = texture(Sampler0, texCoord0) * fColor * ColorModulator;

    // Set the alpha to the parsed-in Alpha value
    sampledTexture.a = Alpha;

    fragColor = sampledTexture;
}
