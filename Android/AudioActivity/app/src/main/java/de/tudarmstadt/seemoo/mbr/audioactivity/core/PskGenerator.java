package de.tudarmstadt.seemoo.mbr.audioactivity.core;

import java.util.Iterator;

@SuppressWarnings("SameParameterValue")
public class PskGenerator extends SineGenerator {
    private Iterator<Byte> iterator;
    private FilterInterface filterInterface;
    private int currentSign = 1;
    private float[] cosTable;

    public void setIterator(Iterator<Byte> iterator) {
        this.iterator = iterator;
    }

    public void setSymbolRate(float symbolRate) {
        this.bufferSize = (int) (sampleRate / symbolRate);
        update();
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        update();
    }

    @Override
    protected void setup() {
        super.setup();
        setSymbolRate(31.25f);
        update();
        filterInterface = new FirFilter(FirCoefficients.ULTRASONIC);
    }

    @Override
    protected float[] process(float[] in) {
        return filterInterface.filter(in);
    }

    void update() {
        cosTable = new float[this.bufferSize];

        for (int i = 0; i < this.bufferSize; i++) {
            cosTable[i] = (float) Math.cos(((double)i / bufferSize) * Math.PI);
        }
    }

    @Override
    protected float[] generateSamples() {
        if (!iterator.hasNext()) {
            return null;
        }

        float[] samples = super.generateSamples();

        if (iterator.next() == 0) {
            for (int i = 0; i < samples.length; i++) {
                samples[i] *= cosTable[i] * currentSign;
            }
            currentSign *= -1;
        } else {
            for (int i = 0; i < samples.length; i++) {
                samples[i] *= currentSign;
            }
        }

        return samples;
    }
}
