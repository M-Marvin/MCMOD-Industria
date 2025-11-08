#include "flywheel:util/quaternion.glsl"

void flw_instanceVertex(in FlwInstance i) {
    flw_vertexPos = vec4(flw_vertexPos.xyz + i.position, 1.0);
    flw_vertexColor *= i.color;
    flw_vertexOverlay = i.overlay;
    flw_vertexLight = max(vec2(i.light) / 256.0, flw_vertexLight);
    uint quadId = flw_vertexId / 4;
    flw_vertexTexCoord = flw_vertexTexCoord + i.shifts[quadId];
}
