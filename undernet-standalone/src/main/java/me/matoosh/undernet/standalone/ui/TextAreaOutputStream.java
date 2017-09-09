package me.matoosh.undernet.standalone.ui;

/**
 * An outputstream which writes its contents to a text area.
 * Used for a standalone console.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaOutputStream extends OutputStream {

    /**
     * The text area to write to.
     */
    private final JTextArea textArea;
    /**
     * String builder.
     */
    private final StringBuilder sb = new StringBuilder();
    /**
     * The message prefix.
     */
    private String title;

    public TextAreaOutputStream(final JTextArea textArea, String title) {
        this.textArea = textArea;
        this.title = title;
        sb.append(title + "> ");
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public void write(int b) throws IOException {

        if (b == '\r')
            return;

        if (b == '\n') {
            final String text = sb.toString() + "\n";
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(text);
                }
            });
            sb.setLength(0);
            sb.append(title + "> ");
            return;
        }

        sb.append((char) b);
    }
}
