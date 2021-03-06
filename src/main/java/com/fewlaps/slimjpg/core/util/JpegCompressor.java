package com.fewlaps.slimjpg.core.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

public class JpegCompressor {

    private static final String JPG = "jpg";

    public byte[] writeJpg(byte[] input, int quality, boolean keepMetadata) throws Exception {
        ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(input));
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        ImageReader reader = readers.next();
        reader.setInput(iis, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream((outputStream));

        final ImageWriter writer = ImageIO.getImageWritersByFormatName(JPG).next();
        writer.setOutput(imageOutputStream);

        JPEGImageWriteParam writeParam = (JPEGImageWriteParam) writer.getDefaultWriteParam();
        writeParam.setCompressionMode(MODE_EXPLICIT);

        float appliedQuality = quality / 100f;
        if (appliedQuality < 0) {
            writeParam.setCompressionQuality(0);
        } else {
            writeParam.setCompressionQuality(appliedQuality);
        }

        IIOMetadata metadata = null;
        if (keepMetadata) {
            metadata = reader.getImageMetadata(0);
            writeParam.setOptimizeHuffmanTables(true);
        }

        try {
            BufferedImage bufferedImage = reader.read(0);
            writer.write(null, new IIOImage(bufferedImage, null, metadata), writeParam);
            dispose(reader, writer);
            return outputStream.toByteArray();
        } catch (Exception e) {
            dispose(reader, writer);
            throw e;
        }
    }

    private void dispose(ImageReader reader, ImageWriter writer) {
        writer.dispose();
        reader.dispose();
    }
}