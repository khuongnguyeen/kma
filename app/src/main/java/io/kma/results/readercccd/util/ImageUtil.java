package io.kma.results.readercccd.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import java.io.EOFException;

import java.nio.ByteBuffer;
import java.util.Vector;
import jj2000.j2k.fileformat.FileFormatBoxes;
import jj2000.j2k.io.RandomAccessIO;
import jj2000.j2k.util.FacilityManager;

import com.gemalto.jp2.JP2Decoder;

public class ImageUtil
{
    public static byte[] imageToByteArray(Image image) {
        byte[] data = null;
        if (image.getFormat() == ImageFormat.JPEG) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            data = new byte[buffer.capacity()];
            buffer.get(data);
            return data;
        } else if (image.getFormat() == ImageFormat.YUV_420_888) {
            data = NV21toJPEG(
                    YUV_420_888toNV21(image),
                    image.getWidth(), image.getHeight());
        }
        return data;
    }
    public static byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv21 = new byte[ySize + uSize + vSize];

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        return nv21;
    }

    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        return out.toByteArray();
    }

    ///RCC
    public static Bitmap decode(InputStream in)
    {
        int width=307;
        int height=378;
        Bitmap ret = null;
        System.out.println("En ImageUtil.decode init");
        try
        {
            JP2Decoder decoder = new JP2Decoder(in);
            JP2Decoder.Header header = decoder.readHeader();

            int skipResolutions = 1;
            int imgWidth = header.width;
            int imgHeight = header.height;
            System.out.println("imgWidth:"+imgWidth+" imgHeight:"+imgHeight);
            while (skipResolutions < header.numResolutions) {
                imgWidth >>= 1;
                imgHeight >>= 1;
                if (imgWidth < width || imgHeight < height) break;
                else skipResolutions++;
            }

            //we break the loop when skipResolutions goes over the correct value
            skipResolutions--;


            if (skipResolutions > 0) decoder.setSkipResolutions(skipResolutions);


            System.out.println("En ImageUtil.decode antes de decode");
            ret = decoder.decode();

        }
        catch (Exception e)
        {
            System.out.println("En ImageUtil.decode e:"+e.getMessage());
        }

        return ret;
    }

}
