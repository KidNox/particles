precision mediump float;
uniform sampler2D u_texture;
varying vec4 v_color;
void main() {
    vec4 tex = texture2D(u_texture, gl_PointCoord);
    gl_FragColor = v_color * tex;
    //gl_FragColor.w = v_alpha;
}