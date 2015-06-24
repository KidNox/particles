precision mediump float;
#define PI 3.1415
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

float noice(float x, float y) {
    float a = 12.9898;
    float b = 78.233;
    float c = 43758.5453;
    float dt= dot(vec2(x, y), vec2(a,b));
    float sn= mod(dt, 3.14);
    float lastRnd = fract(sin(sn) * c);
    return lastRnd;
}

void main() {
    time = sin(u_time * a_life) / a_life - a_age;
    if(time < 0.0) {
        time = -time;
    }
    time = sin(time * u_time * a_life);
    if(time < 0.0) {
        time = -time;
    }
    v_size = (a_life - (time * 10.0 * a_age)) * u_size;

/*    if (v_size < u_size * 0.2) {
        float td = a_life/a_age;
        td /= (10.0 + sin(noice(a_life, a_age)));
        float df = u_time/td;
        int div = int(df);
        df = float(div);
        td *= df;
        time = u_time - td;
        v_size = (a_life - (time * 10.0 * a_age)) * u_size;
    }*/
    gl_PointSize = v_size;
    v_color = a_color;
    gl_Position = a_position;
    gl_Position += (time * a_move * 0.5);
    gl_Position.w = 1.0;
}
