precision mediump float;
uniform float u_size;
uniform float u_time;
attribute vec4 a_position;
attribute vec4 a_move;
attribute vec4 a_color;
attribute float a_life;
attribute float a_age;
varying vec4 v_color;
varying float v_size;
float time;
void main() {
    v_size = (a_life - (u_time * 10.0 * a_age)) * u_size;
    time = u_time;
    if (v_size < u_size * 0.2) {
        float td = a_life/a_age;
        td /= 10.0;
        float df = u_time/td;
        int div = int(df);
        df = float(div);
        td *= df;
        time = u_time - td;
        v_size = (a_life - (time * 10.0 * a_age)) * u_size;
    }
    gl_PointSize = v_size;
    v_color = a_color;
    gl_Position = a_position;
    gl_Position += (time * a_move * 0.5);
    gl_Position.w = 1.0;
}