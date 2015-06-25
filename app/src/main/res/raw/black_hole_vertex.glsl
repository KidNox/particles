precision mediump float;

//consts(init)
#define PI 3.1415
#define cRand 7
#define cRing 3
#define cRadius 5
#define cMinRadius 0.15
#define cHole 0.5

//increments(diff)
#define dRing 0.002
#define dRadius 0.994

//const
uniform float u_hole_r;
uniform float u_scale;

//mutable
uniform float u_elapsedTime;

attribute vec4 a_color;

//init state
attribute float a_life; //200 ... 500 frames
attribute float a_rand;
attribute float a_ring;
attribute float a_radius;
attribute float a_move;

varying vec4 v_color;


float rand;
float ring;
float radius;

float time;
void main()
{
    v_color = a_color;
    rand = a_rand;
    ring = a_ring;
    radius = a_radius;

    float fractTime = fract(u_elapsedTime / a_life);

    time = fractTime;
    if(time < 0.0) {
        time = -time;
    }

    rand += time * a_move;

    float sinTime = sin(u_elapsedTime / (a_life * 15.0));

    radius -= abs(sinTime);

    if(radius < cMinRadius) {
        radius = a_radius;
        ring = a_ring;
    }

    ring -= time * radius;

    float a;
    if(ring < u_hole_r) {
        ring = u_hole_r;
        a = 3.0;
    } else {
        a = PI * (sin(ring - u_hole_r) + (PI / 2.0));
    }


    gl_PointSize = radius * u_scale;
    gl_Position.x = cos(rand / a) * ring;
    gl_Position.y = sin(rand) * ring;
    gl_Position.w = 1.0;
}