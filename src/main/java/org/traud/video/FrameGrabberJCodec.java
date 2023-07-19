package org.traud.video;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.MediaInfo;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FrameGrabberJCodec implements IFrameGrabber {
    MP4Demuxer dm;
    DemuxerTrack vt;
    int numberOfFrames;
    double totalDuration;
    double frameRate;
    FrameGrab grab;
    MediaInfo mediaInfo;
    private Mode mode;

    public FrameGrabberJCodec(File videoFile) throws IOException {
        try {
            FileChannelWrapper readableChannel = NIOUtils.readableChannel(videoFile);

            dm = MP4Demuxer.createRawMP4Demuxer(readableChannel);
            vt = dm.getVideoTrack();
            numberOfFrames = vt.getMeta().getTotalFrames();
            totalDuration = vt.getMeta().getTotalDuration();
            frameRate = numberOfFrames/totalDuration;

            grab = FrameGrab.createFrameGrab(readableChannel);
            mediaInfo = grab.getMediaInfo();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public double getFrameRate() {
        return frameRate;
    }


    @Override
    public BufferedImage grab(double secondsOffset) throws IOException {
        try {
            System.out.printf("\rseeking...");
            if (mode == Mode.SLOPPY) grab.seekToSecondSloppy(secondsOffset);
            else grab.seekToSecondPrecise(secondsOffset);

            //grab.seekToSecondPrecise(secOfs);
            System.out.printf("\rgrabbing picture...");
            Picture picture = grab.getNativeFrame();
            if (picture == null) {
                return null;
            }
            //for JDK (jcodec-javase)
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            return bufferedImage;
        } catch (JCodecException e) {
            throw new IOException("grabbing frame failed: " + e.getMessage(), e);
        }
    }


    @Override
    public BufferedImage grab() throws IOException {
        try {
            Picture picture = grab.getNativeFrame();
            if (picture == null) {
                return null;
            }
            return AWTUtil.toBufferedImage(picture);
        } catch (Exception e) {
            throw new IOException("grabbing frame failed: " + e.getMessage(), e);
        }
    }
    @Override
    public Size getDimensions()
    {
        return mediaInfo.getDim();
    }

    @Override
    public void close() throws IOException {
        dm.close();
    }

    @Override
    public int getTotalFrames() {
        return vt.getMeta().getTotalFrames();
    }

    @Override
    public double getTotalDurationSeconds() {
        return vt.getMeta().getTotalDuration();
    }
}
