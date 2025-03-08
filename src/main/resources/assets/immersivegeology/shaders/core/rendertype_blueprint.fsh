#version 150

#moj_import <fog.glsl> // Import fog functionality
#moj_import <light.glsl> // Import lighting functionality

uniform sampler2D Sampler0; // Texture sampler
uniform vec4 ColorModulator; // Color modulator
uniform float Time; // Time variable for animations or effects
uniform vec3 ColorTint;
uniform vec4 TextureMat;

in vec3 fPosition; // The local position passed from the vertex shader
in vec4 fColor; // The color passed from the vertex shader
in vec2 texCoord0; // Block Textures
in vec3 fNormal; // The normal vector of the fragment
out vec4 fragColor; // The final color output

void main() {
    // Calculate a dynamic alpha value for the fading effect
    float baseWave = sin(Time * 0.1); // Slower primary wave
    float detailWave = sin(Time * 0.05) * 0.3; // Even slower secondary wave with less influence
    float smoothFade = (baseWave + detailWave) * 0.5 + 0.5; // Normalize to 0-1 range
    float fadeEffect = 0.5 + (smoothFade * 0.5); // Map to 0.8-1.0 range

    // Sample the texture
    vec4 sampledTexture = texture(Sampler0, texCoord0) * fColor * ColorModulator;

    // Create the background color with constant alpha 0.5
    vec4 backgroundColor = vec4(ColorTint, 0.2);

    // Apply a 30% tint of the background color to the sampled texture
    // This happens even when the texture is at 100% visibility
    vec4 tintedTexture = mix(sampledTexture, vec4(ColorTint, sampledTexture.a), 0.3);

    // Mix between the already-tinted texture and background color based on the fade effect
    vec4 finalColor = mix(tintedTexture, backgroundColor, 0.1);
    // If the original texture is transparent (alpha < 0.01),
    // just use the background color with its defined alpha
    vec4 modifiedTexture = (sampledTexture.a < 0.01) ? backgroundColor : tintedTexture;
    modifiedTexture.a *= fadeEffect;
    fragColor = modifiedTexture;
}