package Riverside;

import toxi.physics2d.VerletParticle2D;

import java.util.ArrayList;

public class CurveAttractors {
    ArrayList<VerletParticle2D> attractors;

    public CurveAttractors() {
        attractors = new ArrayList<VerletParticle2D>();
    }

    void add(VerletParticle2D e){
        attractors.add(e);
    }

    int size(){
        return attractors.size();
    }

}
