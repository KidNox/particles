precision mediump float;
#define PI 3.1415

//const
uniform float u_hole_r;
uniform float u_scale;

uniform float u_velocityDivider;
uniform float u_angleMultiplier;
uniform float u_rMultiplier;
uniform float u_rInitialMultiplier;
uniform float u_xMultiplier;
uniform float u_yMultiplier;

uniform float u_ringOffsetMultiplier;

//mutable
uniform float u_elapsedTime;// +=1 for frame

attribute vec4 a_color;

//init state
attribute float a_timeOffset;
attribute float a_life; //based on radius
attribute float a_angle;
attribute float a_ring;
attribute float a_radius;
attribute float a_velocity;

varying vec4 v_color;

float angle;
float ring;
float radius;

float elapsedTime;
float time;
void main()
{
    v_color = a_color;
    angle = a_angle * u_angleMultiplier;
    ring = a_ring * u_ringOffsetMultiplier;
    radius = a_radius * u_rInitialMultiplier;
    elapsedTime = u_elapsedTime + a_timeOffset;

    float previousTime = fract((elapsedTime - 1.0) / a_life);
    time = fract(elapsedTime / a_life);

    float frames = time * a_life;

    //check reset
    if(previousTime > time) {
        radius = a_radius * u_rInitialMultiplier;
        ring = a_ring * u_ringOffsetMultiplier;
    } else {
        radius *= pow(u_rMultiplier, frames);
        ring -= time / a_life;
    }
    if(ring < u_hole_r) {
        ring = u_hole_r;
    }

    angle += (frames + u_elapsedTime) * a_velocity / u_velocityDivider;

    gl_PointSize = radius * u_scale;
    gl_Position.x = cos(angle * u_xMultiplier) * ring;
    gl_Position.y = sin(angle * u_yMultiplier) * ring;
    gl_Position.w = 1.0;
}