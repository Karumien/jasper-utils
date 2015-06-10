/*
 * Copyright (c) 2014 Karumien s.r.o.
 * 
 * The contractor, Karumien s.r.o., does not take any responsibility for defects
 * arising from unauthorized changes to the source code.
 */
package cz.i24.util.jasper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * ValueObject for QR Code generation in JasperReports.
 * 
 * @author <a href="miroslav.svoboda@karumien.com">Miroslav Svoboda</a>
 * @version 1.0
 * @since 27.04.2014 09:45:12
 */
public class QRCodeColored {

    public static final int BLACK = 0xFF000000;

    public static final int WHITE = 0xFFFFFFFF;

    public static final String JPG = "JPG";

    public static final String GIF = "GIF";

    public static final String PNG = "PNG";

    private final HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();

    private Writer qrWriter = new QRCodeWriter();

    private int width = 125;

    private int height = 125;

    private String imageType = PNG;

    private int onColor = MatrixToImageConfig.BLACK;

    private int offColor = MatrixToImageConfig.WHITE;

    public String getImageType() {
        return this.imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    private String text;

    public static QRCodeColored from(String text) {
        QRCodeColored instance = new QRCodeColored();
        instance.text = text;
        return instance;
    }

    /**
     * Overrides the default error correction by supplying a {@link com.google.zxing.EncodeHintType#ERROR_CORRECTION}
     * hint to {@link com.google.zxing.qrcode.QRCodeWriter#encode}
     *
     * @return the current QRCode object
     */
    public QRCodeColored withErrorCorrection(ErrorCorrectionLevel level) {
        return this.withHint(EncodeHintType.ERROR_CORRECTION, level);
    }

    /**
     * Sets hint to {@link com.google.zxing.qrcode.QRCodeWriter#encode}
     *
     * @return the current QRCode object
     */
    public QRCodeColored withHint(EncodeHintType hintType, Object value) {
        this.hints.put(hintType, value);
        return this;
    }

    /**
     * Overrides the size of the qr from its default 125x125
     *
     * @param width
     *            the width in pixels
     * @param height
     *            the height in pixels
     * @return the current QRCode object
     */
    public QRCodeColored withSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Overrides the size of the qr from its default 125x125
     *
     * @param width
     *            the width in pixels
     * @param height
     *            the height in pixels
     * @return the current QRCode object
     */
    public QRCodeColored withColor(int onColor, int offColor) {
        this.onColor = onColor;
        this.offColor = offColor;
        return this;
    }

    private BitMatrix createMatrix() throws WriterException {
        return this.qrWriter.encode(this.text, com.google.zxing.BarcodeFormat.QR_CODE, this.width, this.height,
                this.hints);
    }

    private void writeToStream(OutputStream stream) throws IOException, WriterException {
        MatrixToImageWriter.writeToStream(this.createMatrix(), this.imageType, stream, new MatrixToImageConfig(
                this.onColor, this.offColor));
    }

    /**
     * returns a {@link ByteArrayOutputStream} representation of the QR code
     *
     * @return qrcode as stream
     */
    public ByteArrayOutputStream stream() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.writeTo(stream);
        return stream;
    }

    /**
     * writes a representation of the QR code to the supplied {@link OutputStream}
     *
     * @param stream
     *            the {@link OutputStream} to write QR Code to
     */
    public void writeTo(OutputStream stream) {
        try {
            this.writeToStream(stream);
        } catch (Exception e) {
            throw new IllegalStateException("QRCode genertate", e);
        }
    }

    /**
     * Overrides the imageType from its default {@link #PNG}
     *
     * @param imageType
     *            the you would like the resulting QR to be
     * @return the current QRCode object
     */
    public QRCodeColored to(String imageType) {
        this.imageType = imageType;
        return this;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public HashMap<EncodeHintType, Object> getHints() {
        return this.hints;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOnColor() {
        return this.onColor;
    }

    public void setOnColor(int onColor) {
        this.onColor = onColor;
    }

    public int getOffColor() {
        return this.offColor;
    }

    public void setOffColor(int offColor) {
        this.offColor = offColor;
    }

}
