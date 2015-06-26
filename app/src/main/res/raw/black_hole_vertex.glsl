precision mediump float;
#define PI 3.1415

//const
uniform float u_hole_r;
uniform float u_scale;

uniform float u_velocityDivider;
uniform float u_angleMultiplier;
uniform float u_rMultiplier;
uniform float u_rInitialMultiplier;
uniform float u_lifeMultiplier;
uniform float u_minParticleSize;

uniform float u_xDivider;
uniform float u_yDivider;

uniform float u_xAndgleDif;
uniform float u_yAndgleDif;

uniform float u_ringOffsetMultiplier;

//mutable
uniform float u_elapsedTime;// +=1 for frame

uniform float u_xTransition;
uniform float u_yTransition;

//init state
attribute float a_timeOffset;
attribute float a_life; //based on radius
attribute float a_angle;
attribute float a_ring;
attribute float a_radius;
attribute float a_velocity;
attribute vec4 a_color;
varying vec4 v_color;

float angle;
float ring;
float radius;

float elapsedTime;
float time;
void main()
{
    v_color = a_color;
    elapsedTime = u_elapsedTime + a_timeOffset;
    angle = a_angle * u_angleMultiplier;
    ring = a_ring * u_ringOffsetMultiplier;
    radius = a_radius * u_rInitialMultiplier;

    float previousTime = fract((elapsedTime - 1.0) / a_life);
    time = fract(elapsedTime / a_life);

    float frame = time * a_life;
    float lifePeriod = a_life * u_lifeMultiplier;
    float appearVelocity =  (radius - u_minParticleSize) / lifePeriod;

    if(frame < lifePeriod) {
        radius = u_minParticleSize + (appearVelocity * frame);
        v_color.a /= (lifePeriod - frame);
    } else if(frame > a_life - lifePeriod) {
        radius = u_minParticleSize * appearVelocity * (a_life - frame);
        v_color.a *= (a_life - frame) / lifePeriod;
    } else {
        radius *= pow(u_rMultiplier, frame);
    }

    ring -= time / a_life;

    if(ring < u_hole_r) {
        ring = u_hole_r;
    }

    angle += (frame + elapsedTime) * a_velocity / u_velocityDivider;

    gl_PointSize = radius * u_scale;
    gl_Position.x = cos(angle / u_xDivider) * ring * u_xTransition;
    gl_Position.y = sin(angle / u_yDivider) * ring * u_yTransition;
    gl_Position.w = 1.0;
}