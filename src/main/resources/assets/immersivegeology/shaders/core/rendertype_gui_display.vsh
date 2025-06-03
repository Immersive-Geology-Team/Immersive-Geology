#version 150

#moj_import <fog.glsl> // Import fog functionality
#moj_import <light.glsl> // Import lighting functionality

in vec3 Position;  // The position of the vertex
in vec4 Color;     // The color of the vertex
in vec2 UV0;       // The texture coordinates
in vec3 Normal;    // The normal of the vertex

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset; // Position offset for chunks

out vec3 fPosition;  // Output world position
out vec4 fColor;     // Output color
out vec2 texCoord0;  // Output texture coordinates
out vec3 fNormal;    // Output normal

void main() {
vec3 pos = Position + ChunkOffset;
// Transform position by ModelView and Projection Matrices
gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

// Pass out data to fragment shader
fPosition = pos; // Local position (after applying chunk offset and animation)
fColor = Color;
texCoord0 = UV0;
fNormal = Normal;
}