package net.devip.picscramble;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_APPLICATION_NOTES;

final class Exif {
    
    private static final Logger log = LoggerFactory.getLogger(Exif.class);
    
    private int tileSideLen;
    
    private List<Integer> rotationSeqDegrees;
    
    public Exif() {
    
    }
    
    public void setTileSideLen(int tileSideLen) {
        
        this.tileSideLen = tileSideLen;
    }
    
    public void setRotationSeqDegrees(List<Integer> rotationSeqDegrees) {
        
        this.rotationSeqDegrees = rotationSeqDegrees;
    }
    
    public int getTileSideLen() {
        
        return tileSideLen;
    }
    
    public List<Integer> getRotationSeqDegrees() {
        
        return rotationSeqDegrees;
    }
    
    /**
     Based on https://github.com/apache/commons-imaging/blob/master/src/test/java/org/apache/commons/imaging/examples
     /WriteExifMetadataExample.java
     */
    private void writeInternal(File jpegFile) throws IOException, ImageReadException, ImageWriteException {
        
        TiffOutputSet outputSet = null;
        
        // note that metadata might be null if no metadata is found.
        ImageMetadata metadata = Imaging.getMetadata(jpegFile);
        JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
        if (jpegMetadata != null) {
            
            // note that exif might be null if no Exif metadata is found.
            TiffImageMetadata exif = jpegMetadata.getExif();
            if (exif != null) {
                
                // TiffImageMetadata class is immutable (read-only).
                // TiffOutputSet class represents the Exif data to write.
                //
                // Usually, we want to update existing Exif metadata by
                // changing
                // the values of a few fields, or adding a field.
                // In these cases, it is easiest to use getOutputSet() to
                // start with a "copy" of the fields read from the image.
                outputSet = exif.getOutputSet();
            }
        }
        
        // if file does not contain any exif metadata, we create an empty
        // set of exif metadata. Otherwise, we keep all of the other
        // existing tags.
        if (outputSet == null) {
            
            outputSet = new TiffOutputSet();
        }
        
        byte[] appNotes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {
            
            dos.writeInt(tileSideLen);
            for (Integer deg : rotationSeqDegrees) {
                
                dos.writeInt(deg);
            }
            
            dos.flush();
            baos.flush();
            
            appNotes = baos.toByteArray();
        }
        
        TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
        
        exifDirectory.removeField(EXIF_TAG_APPLICATION_NOTES);
        exifDirectory.add(EXIF_TAG_APPLICATION_NOTES, appNotes);
    
        File tempFile = new File(jpegFile.getParentFile(), "temp-delme-scrambled.jpg");
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
    
            new ExifRewriter().updateExifMetadataLossless(jpegFile, bos, outputSet);
        }
    
        tempFile.renameTo(jpegFile);
    }
    
    private void readApplicationNotesInternal(File jpegFile) throws IOException, ImageReadException, ImageWriteException {
        
        ImageMetadata metadata = Imaging.getMetadata(jpegFile);
        if (!(metadata instanceof JpegImageMetadata)) {
            
            throw new RuntimeException("No ImageMetadata is not an instance of JpegImageMetadata!");
        }
        
        JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
        if (jpegMetadata == null) {
            
            throw new RuntimeException("No JpegImageMetadata!");
        }
        
        TiffField appNotesField = jpegMetadata.findEXIFValue(EXIF_TAG_APPLICATION_NOTES);
        if (appNotesField == null) {
            
            throw new RuntimeException("No EXIF_TAG_APPLICATION_NOTES!");
        }
        
        byte[] appNotes = appNotesField.getByteArrayValue();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(appNotes);
             DataInputStream dis = new DataInputStream(bais)) {
            
            this.tileSideLen = dis.readInt();
            
            this.rotationSeqDegrees = new ArrayList<>();
            while (dis.available() > 0) {
                
                this.rotationSeqDegrees.add(dis.readInt());
            }
        }
    }
    
    public void readApplicationNotes(File jpegFile) {
        
        try {
    
            readApplicationNotesInternal(jpegFile);
        } catch (Exception ex) {
            
            throw new RuntimeException(ex);
        }
    }
    
    public void write(File jpegFile) {
        
        try {
    
            writeInternal(jpegFile);
        } catch (Exception ex) {
            
            throw new RuntimeException(ex);
        }
    }
}
