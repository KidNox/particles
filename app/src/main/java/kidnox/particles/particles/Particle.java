package kidnox.particles.particles;

public class Particle {

    public float x, y, z;
    public float dx, dy, dz;
    public float r, g, b;

    public float timeToLive;

    public float respawnTime;

    public Particle() {
    }

    public Particle(float newx, float newy, float newz) {
        this.x = newx;
        this.y = newy;
        this.z = newz;
    }

    // constructor to assign location and velocity
    public Particle(float newx, float newy, float newz, float newdx, float newdy, float newdz) {
        this.x = newx;
        this.y = newy;
        this.z = newz;
        this.dx = newdx;
        this.dy = newdy;
        this.dz = newdz;
    }
}
