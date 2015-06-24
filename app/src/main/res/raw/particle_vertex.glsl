#define M_PI 3.1415926535897932384626433832795
precision mediump float;
uniform float u_hole_radius;
uniform float u_scale_factor;

attribute vec4 a_color;
attribute float a_move;
attribute float a_rand;

varying vec4 v_color;

float lastRnd;
float rand(float seed)
{
    float a = 12.9898;
    float b = 78.233;
    float c = 43758.5453;
    float dt= dot(vec2(lastRnd, seed), vec2(a,b));
    float sn= mod(dt, 3.14);
    lastRnd = fract(sin(sn) * c);
    return lastRnd;
}

bool init;
float radius;
float ring;
float rand;

void main()
{
    v_color = a_color;
    if(!init) {
        rand = a_rand;
        init = true;
    }
    if(radius < u_scale_factor / 6.66) {
        radius = rand(a_rand) * u_scale_factor;
        ring = rand(a_rand) * u_hole_radius * 3.0;
    }

    radius *= 0.994; //disappear

    //move
    ring = max(ring - 1.0, u_hole_radius);
    rand += a_move;

    gl_PointSize = radius;
    gl_Position.x = cos(rand * M_PI) * ring;
    gl_Position.y = sin(rand * M_PI) * ring;
    gl_Position.z = 0.0;
    gl_Position.w = 1.0;
}

/*

Space.prototype.moveParticle = function (p) {
        p.ring = Math.max(p.ring - 1, this.r);
        p.random += p.move;
        p.x = Math.cos(p.random + Math.PI) * p.ring;
        p.y = Math.sin(p.random + Math.PI) * p.ring;
    };

Space.prototype.resetParticle = function (p) {
        p.ring = Math.random() * this.r * 3;
        p.radius = Math.random() * 5;
    };
    Space.prototype.disappear = function (p) {
        if (p.radius < 0.8) {
            this.resetParticle(p);
        }
        p.radius *= 0.994;
    };
    Space.prototype.draw = function (p) {
        this.ctx.beginPath();
        this.ctx.fillStyle = p.color;
        this.ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
        this.ctx.fill();
    };


*/