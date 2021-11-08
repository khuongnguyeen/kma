package io.kma.results.readercccd.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import jj2000.j2k.decoder.Decoder;
import jj2000.j2k.util.ParameterList;


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import colorspace.ColorSpace;
import colorspace.ColorSpaceException;
import icc.ICCProfileException;
import jj2000.j2k.codestream.HeaderInfo;
import jj2000.j2k.codestream.reader.BitstreamReaderAgent;
import jj2000.j2k.codestream.reader.HeaderDecoder;
//import jj2000.j2k.decoder.Decoder;
import jj2000.j2k.decoder.DecoderSpecs;
import jj2000.j2k.entropy.decoder.EntropyDecoder;
import jj2000.j2k.image.BlkImgDataSrc;
import jj2000.j2k.image.ImgDataConverter;
import jj2000.j2k.image.invcomptransf.InvCompTransf;
import jj2000.j2k.quantization.dequantizer.Dequantizer;
import jj2000.j2k.roi.ROIDeScaler;
import jj2000.j2k.util.ISRandomAccessIO;
//import jj2000.j2k.util.ParameterList;
import jj2000.j2k.wavelet.synthesis.InverseWT;
import jj2000.j2k.image.output.ImgWriterPPM;

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

    /*
    private static String pinfoDecoder[][] = Decoder.getAllParameters();
    public static Bitmap decode(byte[] input) {
        // Get the dfault parameter values
        ParameterList defpl = new ParameterList();
        for (int i = pinfoDecoder.length - 1; i >= 0; --i) {
            if (pinfoDecoder[i][3] != null) {
                defpl.put(pinfoDecoder[i][0], pinfoDecoder[i][3]);
            }
        }
        ParameterList pl = new ParameterList(defpl);
        //pl.setProperty("rate", "3");
        Decoder dec = new Decoder(pl);
        //return dec.run(input);
        dec.run();
    }
    */
    /*
    public static String
            JPEG_MIME_TYPE = "image/jpeg",
            JPEG2000_MIME_TYPE = "image/jp2",
            JPEG2000_ALT_MIME_TYPE = "image/jpeg2000",
            WSQ_MIME_TYPE = "image/x-wsq";

    private ImageUtil() {
    }
    */


    //public static Bitmap read(InputStream inputStream, int imageLength, String mimeType) throws IOException {
        /* DEBUG
        synchronized(inputStream) {
            DataInputStream dataIn = new DataInputStream(inputStream);
            byte[] bytes = new byte[(int)imageLength];
            dataIn.readFully(bytes);
            inputStream = new ByteArrayInputStream(bytes);
        }
        END DEBUG */
/*
        if (JPEG2000_MIME_TYPE.equalsIgnoreCase(mimeType) || JPEG2000_ALT_MIME_TYPE.equalsIgnoreCase(mimeType)) {
            org.jmrtd.jj2000.Bitmap bitmap = org.jmrtd.jj2000.JJ2000Decoder.decode(inputStream);
            return toAndroidBitmap(bitmap);
        } else if (WSQ_MIME_TYPE.equalsIgnoreCase(mimeType)) {
            org.jnbis.Bitmap bitmap = org.jnbis.WSQDecoder.decode(inputStream);
            return toAndroidBitmap(bitmap);
        } else {
            return BitmapFactory.decodeStream(inputStream);
        }
    }*/

    /* ONLY PRIVATE METHODS BELOW */
/*
    private static Bitmap toAndroidBitmap(org.jmrtd.jj2000.Bitmap bitmap) {
        int[] intData = bitmap.getPixels();
        return Bitmap.createBitmap(intData, 0, bitmap.getWidth(), bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    }

    private static Bitmap toAndroidBitmap(org.jnbis.Bitmap bitmap) {
        byte[] byteData = bitmap.getPixels();
        int[] intData = new int[byteData.length];
        for (int j = 0; j < byteData.length; j++) {
            intData[j] = 0xFF000000 | ((byteData[j] & 0xFF) << 16) | ((byteData[j] & 0xFF) << 8) | (byteData[j] & 0xFF);
        }
        return Bitmap.createBitmap(intData, 0, bitmap.getWidth(), bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    }

    static class BoundedInputStream extends FilterInputStream {

        private long bound;
        private long position;

        protected BoundedInputStream(InputStream inputStream, long bound) {
            super(inputStream);
            this.position = 0;
            this.bound = bound;
        }

        public int read() throws IOException {
            if (position >= bound) { return -1; }
            try {
                return super.read();
            } finally {
                position++;
            }
        }
    }

    public interface ProgressListener {
        void previewImageAvailable(int bytesProcessedCount, Bitmap image);
    }
    */


    /*
    public Bitmap decodeJJ2000(InputStream is) throws EOFException, IOException,
            ColorSpaceException, ICCProfileException {
        ISRandomAccessIO in = new ISRandomAccessIO(is);
        ParameterList defpl = new ParameterList();
        String[][] param = Decoder.getAllParameters();

        for (int i = param.length - 1; i >= 0; i--) {
            if (param[i][3] != null)
                defpl.put(param[i][0], param[i][3]);
        }
        // Create parameter list using defaults
        ParameterList pl = new ParameterList(defpl);

        // **** File Format ****
        // If the codestream is wrapped in the jp2 fileformat, Read the
        // file format wrapper
        MyFileFormatReader ff = new MyFileFormatReader(in);
        ff.readFileFormat();
        if (ff.JP2FFUsed) {
            in.seek(ff.getFirstCodeStreamPos());
        }

        // **** header decoder ****
        HeaderInfo hi = new HeaderInfo();
        HeaderDecoder hd = null;
        DecoderSpecs decSpec = null;
        hd = new HeaderDecoder(in, pl, hi);
        decSpec = hd.getDecoderSpecs();
        // Get demixed bitdepths
        int nCompCod = hd.getNumComps();
        int[] depth = new int[nCompCod];
        for (int i = 0; i < nCompCod; i++) {
            depth[i] = hd.getOriginalBitDepth(i);
        }

        // **** Bit stream reader ****
        BitstreamReaderAgent breader = BitstreamReaderAgent.createInstance(in,
                hd, pl, decSpec, pl.getBooleanParameter("cdstr_info"), hi);

        // **** Entropy decoder ****
        EntropyDecoder entdec = hd.createEntropyDecoder(breader, pl);

        // **** ROI de-scaler ****
        ROIDeScaler roids = hd.createROIDeScaler(entdec, pl, decSpec);

        // **** Dequantizer ****
        Dequantizer deq = hd.createDequantizer(roids, depth, decSpec);

        // full page inverse wavelet transform
        InverseWT invWT = InverseWT.createInstance(deq, decSpec);
        int res = breader.getImgRes();
        invWT.setImgResLevel(res);

        // **** Data converter **** (after inverse transform module)
        ImgDataConverter converter = new ImgDataConverter(invWT, 0);

        // **** Inverse component transformation ****
        InvCompTransf ictransf = new InvCompTransf(converter, decSpec, depth,
                pl);

        // **** Color space mapping ****
        ColorSpace csMap;
        BlkImgDataSrc color = null;
        BlkImgDataSrc palettized;
        BlkImgDataSrc resampled;
        BlkImgDataSrc channels;
        if (ff.JP2FFUsed && pl.getParameter("nocolorspace").equals("off")) {
            csMap = new ColorSpace(in, hd, pl);
            channels = hd.createChannelDefinitionMapper(ictransf, csMap);
            resampled = hd.createResampler(channels, csMap);
            palettized = hd.createPalettizedColorSpaceMapper(resampled, csMap);
            color = hd.createColorSpaceMapper(palettized, csMap);
        } else { // Skip colorspace mapping
            color = ictransf;
        }
        // This is the last image in the decoding chain
        BlkImgDataSrc decodedImage = color;
        if (color == null) {
            decodedImage = ictransf;
        }
        // write out the image
        ImgWriterPPM imwriter = new ImgWriterPPM(decodedImage,0,1,2);
        //ImgStreamWriter imwriter = new ImgStreamWriter(decodedImage, 0, 1, 2);
        return imwriter.getImage();
    }*/


    public class MyFileFormatReader implements FileFormatBoxes {
        private RandomAccessIO in;
        private Vector codeStreamPos;
        private Vector codeStreamLength;
        public boolean JP2FFUsed;

        public MyFileFormatReader(RandomAccessIO in) {
            this.in = in;
        }

        public void readFileFormat() throws IOException, EOFException {
            boolean foundCodeStreamBoxes = false;
            long longLength = 0L;
            boolean jp2HeaderBoxFound = false;
            boolean lastBoxFound = false;

            try {
           /* if(this.in.readInt() != 12)
                throw new IOException("File is neither valid JP2 file nor valid JPEG 2000 codestream");
            else if(this.in.readInt() != 1783636000)
                throw new IOException("File is neither valid JP2 file nor valid JPEG 2000 codestream");
            else if(this.in.readInt() != 218793738)
            //if(this.in.readInt() != 12 || this.in.readInt() != 1783636000 || this.in.readInt() != 218793738)
            {
                this.in.seek(0);
                short marker = this.in.readShort();
                if(marker != -177) {
                    throw new Error("File is neither valid JP2 file nor valid JPEG 2000 codestream");
                }

                this.JP2FFUsed = false;
                this.in.seek(0);
                return;
            }

            this.JP2FFUsed = true;
            if(!this.readFileTypeBox()) {
                throw new Error("Invalid JP2 file: File Type box missing");
            }*/

                this.in.seek(0); // <--- pruebas. para intentar posicionar el buffer.

                while(!lastBoxFound) {
                    int pos = this.in.getPos();
                    int length = this.in.readInt();
                    if(pos + length == this.in.length()) {
                        lastBoxFound = true;
                    }

                    int box = this.in.readInt();
                    if(length == 0) {
                        lastBoxFound = true;
                        length = this.in.length() - this.in.getPos();
                    } else {
                        if(length == 1) {
                            longLength = this.in.readLong();
                            throw new IOException("File too long.");
                        }

                        longLength = 0L;
                    }

                    switch(box) {
                        case 1685074537:
                            this.readIntPropertyBox(length);
                            break;
                        case 1785737827:
                            if(!jp2HeaderBoxFound) {
                                throw new Error("Invalid JP2 file: JP2Header box not found before Contiguous codestream box ");
                            }

                            this.readContiguousCodeStreamBox((long)pos, length, longLength);
                            break;
                        case 1785737832:
                            if(jp2HeaderBoxFound) {
                                throw new Error("Invalid JP2 file: Multiple JP2Header boxes found");
                            }

                            this.readJP2HeaderBox((long)pos, length, longLength);
                            jp2HeaderBoxFound = true;
                            break;
                        case 1969843814:
                            this.readUUIDInfoBox(length);
                            break;
                        case 1970628964:
                            this.readUUIDBox(length);
                            break;
                        case 2020437024:
                            this.readXMLBox(length);
                            break;
                        default:
                            FacilityManager.getMsgLogger().printmsg(2, "Unknown box-type: 0x" + Integer.toHexString(box));
                    }

                    if(!lastBoxFound) {
                        this.in.seek(pos + length);
                    }
                }
            } catch (EOFException var11) {
                throw new Error("EOF reached before finding Contiguous Codestream Box");
            }

            if(this.codeStreamPos.size() == 0) {
                throw new Error("Invalid JP2 file: Contiguous codestream box missing");
            }
        }

        public boolean readFileTypeBox() throws IOException, EOFException {
            long longLength = 0L;
            boolean foundComp = false;
            int pos = this.in.getPos();
            int length = this.in.readInt();
            if(length == 0) {
                throw new Error("Zero-length of Profile Box");
            } else if(this.in.readInt() != 1718909296) {
                return false;
            } else if(length == 1) {
                longLength = this.in.readLong();
                throw new IOException("File too long.");
            } else {
                this.in.readInt();
                this.in.readInt();
                int nComp = (length - 16) / 4;

                for(int i = nComp; i > 0; --i) {
                    if(this.in.readInt() == 1785737760) {
                        foundComp = true;
                    }
                }

                if(!foundComp) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        public boolean readJP2HeaderBox(long pos, int length, long longLength) throws IOException, EOFException {
            if(length == 0) {
                throw new Error("Zero-length of JP2Header Box");
            } else {
                return true;
            }
        }

        public boolean readContiguousCodeStreamBox(long pos, int length, long longLength) throws IOException, EOFException {
            int ccpos = this.in.getPos();
            if(this.codeStreamPos == null) {
                this.codeStreamPos = new Vector();
            }

            this.codeStreamPos.addElement(new Integer(ccpos));
            if(this.codeStreamLength == null) {
                this.codeStreamLength = new Vector();
            }

            this.codeStreamLength.addElement(new Integer(length));
            return true;
        }

        public void readIntPropertyBox(int length) {
        }

        public void readXMLBox(int length) {
        }

        public void readUUIDBox(int length) {
        }

        public void readUUIDInfoBox(int length) {
        }

        public long[] getCodeStreamPos() {
            int size = this.codeStreamPos.size();
            long[] pos = new long[size];

            for(int i = 0; i < size; ++i) {
                pos[i] = ((Integer)((Integer)this.codeStreamPos.elementAt(i))).longValue();
            }

            return pos;
        }

        public int getFirstCodeStreamPos() {
            return ((Integer)((Integer)this.codeStreamPos.elementAt(0))).intValue();
        }

        public int getFirstCodeStreamLength() {
            return ((Integer)((Integer)this.codeStreamLength.elementAt(0))).intValue();
        }
    }
}
