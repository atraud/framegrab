package org.traud.video;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jcodec.common.model.Size;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// see https://stackoverflow.com/questions/15735716/how-can-i-get-a-frame-sample-jpeg-from-a-video-mov
public class FrameGrabberJavaCV implements IFrameGrabber {
    FFmpegFrameGrabber g;
    File videoFile;
    private Java2DFrameConverter java2DFrameConverter;
    private Mode mode;

    public FrameGrabberJavaCV(File videoFile) throws IOException {
        try {
            this.videoFile = videoFile;
            g = new FFmpegFrameGrabber(videoFile);
            g.start();

            java2DFrameConverter = new Java2DFrameConverter();
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public BufferedImage grab(double secondsOffset) throws IOException {

        try {
            if (mode == Mode.SLOPPY) {
                g.setTimestamp((long)(secondsOffset*1000000.0));
            }
            else {
                g.setFrameNumber(frameNumber(secondsOffset));
            }

            Frame frame = g.grab();
//            System.out.printf("calling convert(%s)...\n", frame);
            java2DFrameConverter = new Java2DFrameConverter();
            BufferedImage bufferedImage = java2DFrameConverter.convert(frame);
            return bufferedImage;
        } catch (Exception e) {
            throw new IOException("grabbing frame failed: " + e.getMessage(), e);
        }
    }

    @Override
    public BufferedImage grab() throws IOException {
        try {
            Frame frame = g.grab();
            java2DFrameConverter = new Java2DFrameConverter();
            BufferedImage bufferedImage = java2DFrameConverter.convert(frame);
            return bufferedImage;
        } catch (Exception e) {
            return null;
            // throw new IOException("grabbing frame failed: " + e.getMessage(), e);
        }
    }

    private int frameNumber(double secondsOffset) {
        return (int)(g.getFrameRate()*secondsOffset);
    }

    @Override
    public Size getDimensions()
    {
        return new Size(g.getImageWidth(), g.getImageHeight());
    }

    @Override
    public double getFrameRate() {
        return g.getFrameRate();
    }

    @Override
    public void close() throws IOException {
        try {
            g.stop();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int getTotalFrames() {
        return g.getLengthInFrames();
    }

    @Override
    public double getTotalDurationSeconds() {
        System.out.printf("getTotalDurationSeconds %s: %d frames%n", videoFile, g.getLengthInFrames());
        System.out.printf("getTotalDurationSeconds %s: %1.2fs%n", videoFile, g.getLengthInTime()/1000000.0);
        return g.getLengthInTime()/1000000.0;
    }
}
