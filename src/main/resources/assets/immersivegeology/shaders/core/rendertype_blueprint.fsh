#version 150

#moj_import <fog.glsl> // Import fog functionality
#moj_import <light.glsl> // Import lighting functionality

uniform sampler2D Sampler0; // Texture sampler
uniform vec4 ColorModulator; // Color modulator
uniform vec4 FogColor; // Fog color
uniform float FogStart; // Start of the fog
uniform float FogEnd; // End of the fog
uniform float Time; // Time variable for animations or effects
uniform float GridThickness; // Control the thickness of grid lines
uniform vec3 ColorTint;
uniform vec4 TextureMat;

in vec3 fPosition; // The local position passed from the vertex shader
in vec4 fColor; // The color passed from the vertex shader
in vec2 texCoord0; // Block Textures
in vec3 fNormal; // The normal vector of the fragment

out vec4 fragColor; // The final color output

// Function to calculate the distance to the nearest grid line in local space
float rectDistance(vec2 uv, vec2 gridSize) {
    uv *= gridSize; // Scale the UV coordinates based on the grid size
    vec2 grid = abs(fract(uv) - 0.5); // Get the distance to the nearest grid line
    return min(grid.x, grid.y); // Return the minimum distance to the grid line
}


void main() {
    vec2 gridSize = vec2(16.0, 16.0);
    vec2 localPos = TextureMat.rg;

    // Animate gridSize based on time
    gridSize *= 1.0 + 0.1 * sin(Time * 2.0); // Oscillating grid size

    float dist = rectDistance(localPos, gridSize);

    float gridAlpha = smoothstep(0.0, GridThickness, dist);

    vec4 textureColor = texture(Sampler0, texCoord0) * fColor * ColorModulator;
    vec4 gridOverlay = vec4(ColorTint, gridAlpha); // Blueprint-style blue grid

    fragColor = mix(textureColor, gridOverlay, gridOverlay.a);
    fragColor = linear_fog(fragColor, length(fPosition), FogStart, FogEnd, FogColor);
}
