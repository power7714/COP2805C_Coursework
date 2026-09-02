package cop2805;

/**
 * AgentClient.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 * 
 * This program loads an encrypted file, sends it to the server, and shows the result.
 * It uses the  Caesar cipher method for decryption.
 *
 * Single-file Swing client. Inner classes handle specific jobs:
 *   - BackgroundPanel  : scales the background image to fill the window
 *   - TransparentPanel : invisible layout panel so background shows through
 *   - LcdPanel         : paints a pager-style LCD bezel and screen behind text
 *   - PlasticButton    : custom painted button with raised plastic look
 *   - StyleKit         : all colors, fonts, and shared style helpers
 *   - DialogHelper     : one-line popup shortcuts
 *   - FileLoader       : file chooser and file reading
 *   - ServerConnection : HTTP POST to the PHP server
 *   - AgentClientTests : unit tests, no JUnit needed
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

public class AgentClient extends JFrame {

    // UID added to satisfy Eclipse warning
	private static final long serialVersionUID = -8158834363884869685L;
	private JTextArea encryptedArea;
    private JTextArea decryptedArea;
    private PlasticButton selectFileButton;
    private PlasticButton decipherButton;
    private JLabel statusLabel;

    // Place your 1920x1080 PNG in the project root folder.
    private static final String BG_IMAGE_PATH = "bg4.jpg";

    // -------------------------------------------------------
    // main()
    // Starts the GUI on the Swing event thread.
    // To run tests instead, swap the body to: AgentClientTests.runAll()
    // -------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AgentClient().setVisible(true));
        //AgentClientTests.runAll();
    }

    public AgentClient() {
        super("Sterling Intelligence Service -- Secure Messaging");
        buildWindow();
    }

    // buildWindow()
    // Assembles the root panel and all child panels inside it.
    private void buildWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 540);
        setMinimumSize(new Dimension(660, 540));
        setLocationRelativeTo(null);

        BackgroundPanel root = new BackgroundPanel(BG_IMAGE_PATH);
        root.setLayout(new BorderLayout(0, 0));
        root.add(buildTitlePanel(),  BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildStatusBar(),   BorderLayout.SOUTH);

        setContentPane(root);
    }

    // buildTitlePanel()
    // Agency name and subtitle at the top.
    private JPanel buildTitlePanel() {
        JPanel panel = new TransparentPanel(new GridLayout(2, 1, 0, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));

        JLabel title = new JLabel("STERLING INTELLIGENCE SERVICE", SwingConstants.CENTER);
        title.setFont(StyleKit.FONT_TITLE);
        title.setForeground(StyleKit.COLOR_GOLD);

        JLabel sub = new JLabel("CLASSIFIED COMMUNICATIONS PLATFORM", SwingConstants.CENTER);
        sub.setFont(StyleKit.FONT_LABEL);
        sub.setForeground(StyleKit.COLOR_GOLD);

        panel.add(title);
        panel.add(sub);
        return panel;
    }

    // buildCenterPanel()
    // Two stacked LCD sections: encrypted on top, decrypted below.
    private JPanel buildCenterPanel() {
        JPanel panel = new TransparentPanel(new GridLayout(2, 1, 0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 20, 8, 20));
        panel.add(buildEncryptedSection());
        panel.add(buildDecryptedSection());
        return panel;
    }

    // buildEncryptedSection()
    // Top LCD pager displaying the raw encrypted text.
    // Buttons: SELECT FILE and CLEAR.
    private JPanel buildEncryptedSection() {
        JPanel wrapper = new TransparentPanel(new BorderLayout(0, 8));

        JLabel label = new JLabel("ENCRYPTED MESSAGE");
        label.setFont(StyleKit.FONT_LABEL);
        label.setForeground(StyleKit.COLOR_GOLD);

        encryptedArea = new JTextArea();
        StyleKit.styleLcdTextArea(encryptedArea);
        encryptedArea.setToolTipText("Load an encrypted file to see its contents here.");

        LcdPanel lcd = new LcdPanel(encryptedArea);

        JPanel buttons = new TransparentPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        selectFileButton = new PlasticButton("SELECT FILE");
        selectFileButton.setToolTipText("Open a file chooser to load an encrypted file.");
        selectFileButton.addActionListener(e -> handleSelectFile());

        PlasticButton clearBtn = new PlasticButton("CLEAR");
        clearBtn.setToolTipText("Clear the encrypted text area.");
        clearBtn.addActionListener(e -> {
            encryptedArea.setText("");
            setStatus("Cleared.", StyleKit.COLOR_TEXT_FG);
        });

        buttons.add(selectFileButton);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(clearBtn);

        wrapper.add(label,   BorderLayout.NORTH);
        wrapper.add(lcd,     BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return wrapper;
    }

    // buildDecryptedSection()
    // Bottom LCD pager displaying the decoded message.
    // Buttons: DECIPHER and CLEAR.
    private JPanel buildDecryptedSection() {
        JPanel wrapper = new TransparentPanel(new BorderLayout(0, 8));

        JLabel label = new JLabel("DECRYPTED MESSAGE");
        label.setFont(StyleKit.FONT_LABEL);
        label.setForeground(StyleKit.COLOR_GOLD);

        decryptedArea = new JTextArea();
        StyleKit.styleLcdTextArea(decryptedArea);
        decryptedArea.setEditable(false);
        decryptedArea.setToolTipText("The decoded message will appear here after deciphering.");

        LcdPanel lcd = new LcdPanel(decryptedArea);

        JPanel buttons = new TransparentPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        decipherButton = new PlasticButton("DECIPHER");
        decipherButton.setToolTipText("Send the encrypted text to the server for decoding.");
        decipherButton.addActionListener(e -> handleDecipher());

        PlasticButton clearBtn = new PlasticButton("CLEAR");
        clearBtn.setToolTipText("Clear the decrypted text area.");
        clearBtn.addActionListener(e -> {
            decryptedArea.setText("");
            setStatus("Cleared.", StyleKit.COLOR_TEXT_FG);
        });

        buttons.add(decipherButton);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(clearBtn);

        wrapper.add(label,   BorderLayout.NORTH);
        wrapper.add(lcd,     BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return wrapper;
    }

    // buildStatusBar()
    // Thin bar at the bottom showing the current status.
    private JPanel buildStatusBar() {
        JPanel bar = new TransparentPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, StyleKit.COLOR_BORDER),
            BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        statusLabel = new JLabel("Ready.");
        statusLabel.setFont(StyleKit.FONT_STATUS);
        statusLabel.setForeground(StyleKit.COLOR_TEXT_FG);
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    // handleSelectFile()
    // Opens the file chooser and loads the file text into the top area.
    private void handleSelectFile() {
        setStatus("Opening file chooser...", StyleKit.COLOR_TEXT_FG);
        String text = FileLoader.openFileAndRead(this);
        if (text == null) {
            setStatus("File selection cancelled.", StyleKit.COLOR_TEXT_FG);
            return;
        }
        encryptedArea.setText(text);
        setStatus("File loaded. Ready to decipher.", StyleKit.COLOR_STATUS_OK);
    }

    // handleDecipher()
    // Validates input, sends it to the server using a background thread,
    // and writes the result to the decrypted area.
    //
    // Special case: if the text is "shutdown" (any case), the app
    // asks for confirmation and then closes instead of contacting
    // the server.
    private void handleDecipher() {
        String encryptedText = encryptedArea.getText().trim();

        if (encryptedText.isEmpty()) {
            DialogHelper.showError(this,
                "No Text to Decipher",
                "The encrypted message area is empty.\n\nPlease load a file first."
            );
            setStatus("Error: Nothing to send.", StyleKit.COLOR_STATUS_ERR);
            return;
        }

        // We intercept the shutdown command before it reaches the server.
        // We ask the user to confirm so they do not close by accident.
        if (encryptedText.equalsIgnoreCase("shutdown")) {
            boolean confirmed = DialogHelper.showConfirm(this,
                "Shutdown Confirmation",
                "Send shutdown command and close the application?"
            );
            if (confirmed) {
                setStatus("Shutting down...", StyleKit.COLOR_STATUS_ERR);
                dispose();
                System.exit(0);
            }
            return;
        }

        setBusy(true);
        decryptedArea.setText("");
        setStatus("Connecting to server...", StyleKit.COLOR_TEXT_FG);

        // We run the network call off the main thread so the window stays responsive
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return ServerConnection.sendToServer(encryptedText);
            }
            @Override
            protected void done() {
                try {
                    decryptedArea.setText(get());
                    setStatus("Decryption complete.", StyleKit.COLOR_STATUS_OK);
                } catch (Exception ex) {
                    String msg = getRootMessage(ex);
                    DialogHelper.showError(AgentClient.this, "Server Error",
                        "Could not get a response from the server.\n\n" + msg);
                    setStatus("Error: " + msg, StyleKit.COLOR_STATUS_ERR);
                } finally {
                    setBusy(false);
                }
            }
        };
        worker.execute();
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    private void setBusy(boolean busy) {
        decipherButton.setEnabled(!busy);
        decipherButton.setText(busy ? "WORKING..." : "DECIPHER");
    }

    private String getRootMessage(Exception e) {
        Throwable cause = e.getCause();
        if (cause != null && cause.getMessage() != null) return cause.getMessage();
        return e.getMessage() != null ? e.getMessage() : "Unknown error.";
    }

    // INNER CLASS: LcdPanel
    //
    // Wraps a JTextArea inside a painted pager-style LCD display.
    // Draws the outer plastic bezel, the dark green screen, horizontal
    // scanlines across the screen, a soft phosphor glow in the center,
    // and a glare reflection strip across the top edge.
    @SuppressWarnings("serial")
	private static class LcdPanel extends JPanel {

        // How thick the painted plastic bezel is on each side
    	// Also set the rounded corners of the screen and bezel here. (arc)
    	// Size is in pixels.
        private static final int BEZEL      = 10;
        private static final int BEZEL_ARC  = 14;
        private static final int SCREEN_ARC =  6;

        // Overlay effect colors
        private static final Color LCD_GREEN      = new Color(10,  35,  12);
        private static final Color LCD_GREEN_MID  = new Color(15,  45,  18);
        private static final Color SCANLINE_COLOR = new Color( 0,   0,   0, 28);
        private static final Color GLOW_COLOR     = new Color(60, 180,  60, 18);
        private static final Color GLARE_COLOR    = new Color(255, 255, 255, 22);

        // Bezel colors
        private static final Color BEZEL_TOP    = new Color(75, 75, 75);
        private static final Color BEZEL_BOTTOM = new Color(28, 28, 28);
        private static final Color BEZEL_SHADOW = new Color( 0,  0,  0, 120);
        private static final Color BEZEL_SHINE  = new Color(255, 255, 255, 35);

        private static final BufferedImage LCD_IMAGE = loadSharedImage("lcd.png");

        // loadSharedImage()
        // Loads a PNG from the project root. Returns null if missing.
        // Static so the file is only read from disk one time.
        private static BufferedImage loadSharedImage(String filename) {
            try {
                File f = new File(filename);
                if (f.exists()) return ImageIO.read(f);
                System.out.println("LcdPanel: image not found: " + filename + " -- using fallback.");
            } catch (IOException e) {
                System.out.println("LcdPanel: could not load " + filename + ": " + e.getMessage());
            }
            return null;
        }

        public LcdPanel(JTextArea textArea) {
            setLayout(new BorderLayout());
            setOpaque(false);

            if (LCD_IMAGE != null) {
                textArea.setOpaque(false);
            } else {
                textArea.setOpaque(true);
                textArea.setBackground(LCD_GREEN);
            }

            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setOpaque(false);

            scroll.getViewport().setOpaque(false);
            scroll.getViewport().setBackground(new Color(0, 0, 0, 0));

            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.getVerticalScrollBar().setBackground(new Color(20, 50, 20));

            add(scroll, BorderLayout.CENTER);
            setBorder(BorderFactory.createEmptyBorder(BEZEL, BEZEL, BEZEL, BEZEL));
        }

        // paintComponent()
        // Draws the bezel first, then the screen area (image or
        // fallback gradient), then the overlay effects on top.
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            drawBezel    (g2, w, h);
            drawScreen   (g2, w, h);
            drawScanlines(g2, w, h);
            drawGlow     (g2, w, h);
            drawGlare    (g2, w, h);
        }

        // Rounded plastic bezel with a gradient from dark gray at top to near-black at bottom.
        private void drawBezel(Graphics2D g2, int w, int h) {
            RoundRectangle2D bezel = new RoundRectangle2D.Float(0, 0, w, h, BEZEL_ARC, BEZEL_ARC);
            g2.setPaint(new GradientPaint(0, 0, BEZEL_TOP, 0, h, BEZEL_BOTTOM));
            g2.fill(bezel);

            g2.setColor(BEZEL_SHINE);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, BEZEL_ARC, BEZEL_ARC));

            g2.setColor(BEZEL_SHADOW);
            g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, BEZEL_ARC, BEZEL_ARC));
        }

        // Screen area inside the bezel.
        // If lcd.png loaded, it is scaled to fill the screen area.
        // If not, the dark green gradient fallback is painted instead.
        private void drawScreen(Graphics2D g2, int w, int h) {
            int sx = BEZEL, sy = BEZEL;
            int sw = w - BEZEL * 2, sh = h - BEZEL * 2;

            Shape screenClip = new RoundRectangle2D.Float(sx, sy, sw, sh,
                                                          SCREEN_ARC, SCREEN_ARC);
            g2.setClip(screenClip);

            if (LCD_IMAGE != null) {
                g2.drawImage(LCD_IMAGE, sx, sy, sw, sh, null);
            } else {
                g2.setPaint(new GradientPaint(sx, sy, LCD_GREEN_MID, sx, sy + sh, LCD_GREEN));
                g2.fillRoundRect(sx, sy, sw, sh, SCREEN_ARC, SCREEN_ARC);
            }

            g2.setClip(null);

            g2.setColor(new Color(0, 0, 0, 80));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(screenClip);
        }

        // Simulating horizontal scanlines that uses one dark stripe every two pixels across the screen.
        private void drawScanlines(Graphics2D g2, int w, int h) {
            g2.setColor(SCANLINE_COLOR);
            for (int y = BEZEL; y < h - BEZEL; y += 2) {
                g2.drawLine(BEZEL, y, w - BEZEL, y);
            }
        }

        // Soft radial green glow from the center that simulates phosphor backlight bleed.
        private void drawGlow(Graphics2D g2, int w, int h) {
            int cx = w / 2, cy = h / 2;
            int rx = (w - BEZEL * 2) / 2;
            int ry = (h - BEZEL * 2) / 2;

            RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(cx, cy),
                Math.max(rx, ry),
                new float[] { 0.0f, 1.0f },
                new Color[]  { GLOW_COLOR, new Color(0, 0, 0, 0) }
            );
            g2.setPaint(glow);
            g2.fillRoundRect(BEZEL, BEZEL, w - BEZEL * 2, h - BEZEL * 2,
                             SCREEN_ARC, SCREEN_ARC);
        }

        // Semi-transparent white glare strip across the top quarter of the screen.
        // Fades from slightly white at the top to fully transparent part way down.
        private void drawGlare(Graphics2D g2, int w, int h) {
            int sx = BEZEL + 4, sy = BEZEL + 3;
            int sw = w - BEZEL * 2 - 8;
            int sh = (h - BEZEL * 2) / 4;

            g2.setPaint(new GradientPaint(sx, sy, GLARE_COLOR,
                                          sx, sy + sh, new Color(255, 255, 255, 0)));
            g2.fillRoundRect(sx, sy, sw, sh, SCREEN_ARC, 0);
        }
    }

    // INNER CLASS: PlasticButton
    //
    // Fully custom painted button. Resembling a physical plastic button.
    @SuppressWarnings("serial")
	private static class PlasticButton extends JButton {

        // Fallback gradient colors that are used only when button.png is not found
        private static final Color BTN_TOP    = new Color(55, 65, 90);
        private static final Color BTN_BOTTOM = new Color(18, 22, 38);
        private static final Color BTN_PRESS  = new Color(12, 15, 28);

        // Edge and effect colors that are always drawn on top of the image or gradient
        private static final Color BTN_BORDER = new Color(212, 175, 55);
        private static final Color BTN_SHINE  = new Color(255, 255, 255, 45);
        private static final Color BTN_SHADOW = new Color(  0,   0,  0, 100);

        // Label text colors
        private static final Color TEXT_IDLE  = new Color(212, 175, 55);
        private static final Color TEXT_PRESS = new Color(160, 130, 40);
        private static final Color TEXT_OFF   = new Color( 80,  80, 80);

        // button.png is loaded once and shared across all PlasticButton instances.
        // The image is scaled to fit each button's size at paint time.
        private static final BufferedImage BTN_IMAGE = loadSharedImage("button.png");

        // loadSharedImage()
        // Loads a PNG from the project root. Returns null if missing.
        // Static so the file is only read from disk one time.
        private static BufferedImage loadSharedImage(String filename) {
            try {
                File f = new File(filename);
                if (f.exists()) return ImageIO.read(f);
                System.out.println("PlasticButton: image not found: " + filename + " -- using fallback.");
            } catch (IOException e) {
                System.out.println("PlasticButton: could not load " + filename + ": " + e.getMessage());
            }
            return null;
        }

        private boolean hovered = false;

        public PlasticButton(String text) {
            super(text);
            setFont(StyleKit.FONT_BUTTON);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(140, 36));

            // Lighten the button slightly when the mouse is over it
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            });
        }

        // paintComponent()
        // Draws shadow, then the button body (image or gradient),
        // then the gold border, shine strip, and text label.
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w   = getWidth();
            int h   = getHeight();
            int arc = 8;

            boolean pressed = getModel().isPressed();
            boolean enabled = isEnabled();

            drawShadow(g2, w, h, arc, pressed);
            drawBody  (g2, w, h, arc, pressed, enabled);
            drawBorder(g2, w, h, arc, enabled);
            drawShine (g2, w, h, arc, pressed);
            drawLabel (g2, w, h,      pressed, enabled);
        }

        // Drop shadow under the button. Hidden when pressed so it looks sunken.
        private void drawShadow(Graphics2D g2, int w, int h, int arc, boolean pressed) {
            if (pressed) return;
            g2.setColor(BTN_SHADOW);
            g2.fillRoundRect(2, 3, w - 2, h - 2, arc, arc);
        }

        // Button body using an image or fallback gradient.
        // When pressed: shifts down 1px and dims to give a sunken feel.
        // When hovered with image: draws a semi-transparent bright overlay.
        private void drawBody(Graphics2D g2, int w, int h, int arc,
                              boolean pressed, boolean enabled) {
            int yOffset = pressed ? 2 : 0;

            // Clip to the rounded button shape so the image does not bleed out
            Shape clip = new RoundRectangle2D.Float(0, yOffset, w - 2, h - 2, arc, arc);
            g2.setClip(clip);

            if (BTN_IMAGE != null) {
                // Draw the image scaled to the button's full size
                g2.drawImage(BTN_IMAGE, 0, yOffset, w - 2, h - 2, null);

                // When pressed, draw a dark overlay to darken the image
                if (pressed) {
                    g2.setColor(new Color(0, 0, 0, 80));
                    g2.fill(clip);
                }
                // When hovered and enabled, draw a subtle bright overlay
                else if (hovered && enabled) {
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fill(clip);
                }
                // When disabled, draw a dark desaturating overlay
                if (!enabled) {
                    g2.setColor(new Color(0, 0, 0, 120));
                    g2.fill(clip);
                }
            } else {
                // Fallback gradient body when no image is available
                Color top    = pressed ? BTN_PRESS : (hovered && enabled) ? BTN_TOP.brighter() : BTN_TOP;
                Color bottom = pressed ? BTN_PRESS : BTN_BOTTOM;
                g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
                g2.fill(clip);
            }

            g2.setClip(null);
        }

        private void drawBorder(Graphics2D g2, int w, int h, int arc, boolean enabled) {
            g2.setColor(enabled ? BTN_BORDER : new Color(80, 70, 30));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 3, h - 3, arc, arc);
        }

        // A thin glare strip that appears across the top third of the button
        // to simulate a "shine" on the button.
        // Disappears when pressed so the button looks flat.
        private void drawShine(Graphics2D g2, int w, int h, int arc, boolean pressed) {
            if (pressed) return;
            g2.setPaint(new GradientPaint(0, 2, BTN_SHINE, 0, h / 3,
                                          new Color(255, 255, 255, 0)));
            g2.fillRoundRect(2, 2, w - 6, h / 3, arc, arc);
        }

        private void drawLabel(Graphics2D g2, int w, int h, boolean pressed, boolean enabled) {
            g2.setFont(getFont());
            g2.setColor(!enabled ? TEXT_OFF : pressed ? TEXT_PRESS : TEXT_IDLE);

            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();

            // Simulates a button pressed look by moving the button down 1px.
            // Similar to a button on a web page.
            if (pressed) ty += 1;
            g2.drawString(getText(), tx, ty);
        }
    }

    // INNER CLASS: BackgroundPanel
    // Scales the background PNG to fill the full window.
    @SuppressWarnings("serial")
	private static class BackgroundPanel extends JPanel {

        private BufferedImage image;

        public BackgroundPanel(String path) {
            setOpaque(true);
            try {
                File f = new File(path);
                if (f.exists()) {
                    image = ImageIO.read(f);
                } else {
                    System.out.println("Background image not found: " + path);
                    System.out.println("Using fallback gradient.");
                }
            } catch (IOException e) {
                System.out.println("Could not load background image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            } else {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(10, 20, 45),
                                              0, getHeight(), new Color(5, 10, 20)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // INNER CLASS: TransparentPanel
    // Plain layout container with no background paint.
    // Used everywhere inside the window so the background shows through.
    @SuppressWarnings("serial")
	private static class TransparentPanel extends JPanel {
        public TransparentPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }
    }

    // INNER CLASS: StyleKit
    // All colors, fonts, and shared style helpers.
    // You can change these values to change the theme of the app.
    private static class StyleKit {

        // App-level colors
        static final Color COLOR_GOLD       = new Color(212, 175,  55);
        static final Color COLOR_BORDER     = new Color(212, 175,  55, 150);
        static final Color COLOR_TEXT_FG    = new Color(200, 220, 255);
        static final Color COLOR_STATUS_OK  = new Color(100, 220, 130);
        static final Color COLOR_STATUS_ERR = new Color(220,  80,  80);

        // LCD screen colors
        static final Color LCD_TEXT        = new Color(100, 230, 100);
        // Fonts
        static final Font FONT_TITLE  = new Font("Serif",      Font.BOLD,   22);
        static final Font FONT_LABEL  = new Font("SansSerif",  Font.BOLD,   12);
        static final Font FONT_BUTTON = new Font("SansSerif",  Font.BOLD,   12);
        static final Font FONT_STATUS = new Font("SansSerif",  Font.ITALIC, 11);

        // Bold monospaced font makes the text look like a real pager or calculator display.
        static final Font FONT_LCD = new Font("Monospaced", Font.BOLD, 13);

        // Applies the LCD pager look to a text area.
        static void styleLcdTextArea(JTextArea area) {
            area.setFont(FONT_LCD);
            area.setForeground(LCD_TEXT);
            area.setCaretColor(LCD_TEXT);
            area.setSelectionColor(new Color(40, 120, 40));
            area.setSelectedTextColor(new Color(180, 255, 180));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        }
    }

    // INNER CLASS: DialogHelper
    //
    // Themed dialogs that match that match the color scheme
    // of the main frame. Each dialog is a JDialog.
    //
    // There are three dialog types:
    //   showError   -- red accent, single OK button
    //   showInfo    -- gold accent, single OK button
    //   showConfirm -- gold accent, YES and NO buttons, returns true for YES
    private static class DialogHelper {

        // Accent color used for the top stripe on each dialog type
        private static final Color ACCENT_ERROR   = new Color(180,  50,  50);
        private static final Color ACCENT_INFO    = new Color(212, 175,  55);
        private static final Color ACCENT_CONFIRM = new Color(212, 175,  55);

        // Background colors for the dialog body
        private static final Color DLG_BG     = new Color( 12,  22,  48);
        private static final Color DLG_BG2    = new Color(  8,  15,  35);
        private static final Color DLG_BORDER = new Color(212, 175,  55, 180);

        // showError()
        // Dark dialog with a red accent stripe and an OK button.
        static void showError(Component parent, String title, String message) {
            showDialog(parent, title, message, ACCENT_ERROR, false);
        }

        // showInfo()
        // Dark dialog with a gold accent stripe and an OK button.
        @SuppressWarnings("unused")
		static void showInfo(Component parent, String title, String message) {
            showDialog(parent, title, message, ACCENT_INFO, false);
        }

        // showConfirm()
        // Dark dialog with YES and NO buttons.
        // Returns true if the user clicked YES.
        static boolean showConfirm(Component parent, String title, String message) {
            return showDialog(parent, title, message, ACCENT_CONFIRM, true);
        }

        // showDialog()
        // Builds and displays the modal dialog
        // confirmMode: true shows YES/NO, false shows just OK.
        // Returns true only if the user clicked YES in confirm mode.
        private static boolean showDialog(Component parent, String title,
                                          String message, Color accent,
                                          boolean confirmMode) {

            // Finds the parent Frame so the dialog can be modal against it
            Frame owner = (parent instanceof Frame)
                ? (Frame) parent
                : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

            JDialog dialog = new JDialog(owner, title, true); //Setting to true will open the modal
            dialog.setUndecorated(true);
            dialog.setResizable(false);

            // Holds the user's answer when in confirm mode
            boolean[] answer = { false };

            // Root panel: dark gradient background with gold border
            JPanel root = new JPanel(new BorderLayout(0, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    // Dark navy gradient background
                    g2.setPaint(new GradientPaint(0, 0, DLG_BG, 0, getHeight(), DLG_BG2));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    // Gold border around the whole dialog
                    g2.setColor(DLG_BORDER);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                }
            };
            root.setOpaque(false);

            // Here we create the accent stripe and title at the top
            JPanel header = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    // Solid accent color stripe
                    g2.setColor(accent);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    // Thin bright line at the very top
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.drawLine(0, 0, getWidth(), 0);
                }
            };
            header.setOpaque(false);
            header.setPreferredSize(new Dimension(0, 32));
            header.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

            JLabel titleLabel = new JLabel(title.toUpperCase());
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            // Use dark text on the colored stripe for readability
            titleLabel.setForeground(new Color(15, 20, 40));
            titleLabel.setVerticalAlignment(SwingConstants.CENTER);
            header.add(titleLabel, BorderLayout.CENTER);

            // Message area in the center
            JPanel body = new JPanel(new BorderLayout());
            body.setOpaque(false);
            body.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

            JTextArea msgArea = new JTextArea(message);
            msgArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
            msgArea.setForeground(new Color(200, 220, 255));
            msgArea.setBackground(new Color(0, 0, 0, 0));
            msgArea.setOpaque(false);
            msgArea.setEditable(false);
            msgArea.setFocusable(false);
            msgArea.setLineWrap(true);
            msgArea.setWrapStyleWord(true);
            body.add(msgArea, BorderLayout.CENTER);

            JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
            buttonRow.setOpaque(false);

            if (confirmMode) {
                // YES button sets answer to true and closes
                PlasticButton yesBtn = new PlasticButton("YES");
                yesBtn.setPreferredSize(new Dimension(100, 34));
                yesBtn.addActionListener(e -> {
                    answer[0] = true;
                    dialog.dispose();
                });

                // NO button leaves answer false and closes
                PlasticButton noBtn = new PlasticButton("NO");
                noBtn.setPreferredSize(new Dimension(100, 34));
                noBtn.addActionListener(e -> dialog.dispose());

                buttonRow.add(yesBtn);
                buttonRow.add(noBtn);
            } else {
                // OK button to close the dialog
                PlasticButton okBtn = new PlasticButton("OK");
                okBtn.setPreferredSize(new Dimension(100, 34));
                okBtn.addActionListener(e -> dialog.dispose());
                buttonRow.add(okBtn);
            }

            root.add(header,    BorderLayout.NORTH);
            root.add(body,      BorderLayout.CENTER);
            root.add(buttonRow, BorderLayout.SOUTH);

            dialog.setContentPane(root);
            dialog.setSize(380, confirmMode ? 200 : 180);
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);

            return answer[0];
        }
    }

    // INNER CLASS: FileLoader
    // Opens a JFileChooser, reads the selected file, and returns
    // its contents as a String. Returns null if the user cancelled.
    private static class FileLoader {

        static String openFileAndRead(Component parent) {
            File file = showChooser(parent);
            if (file == null) return null;
            return readFile(file, parent);
        }

        private static File showChooser(Component parent) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Encrypted File");
            return chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION
                ? chooser.getSelectedFile() : null;
        }

        private static String readFile(File file, Component parent) {
            if (!file.exists()) {
                DialogHelper.showError(parent, "File Not Found",
                    "The selected file does not exist:\n" + file.getAbsolutePath());
                return null;
            }
            if (!file.canRead()) {
                DialogHelper.showError(parent, "Permission Denied",
                    "Cannot read this file:\n" + file.getAbsolutePath());
                return null;
            }
            try {
                return readContents(file);
            } catch (IOException e) {
                DialogHelper.showError(parent, "Read Error",
                    "Could not read the file:\n" + e.getMessage());
                return null;
            }
        }

        // Reads all lines and joins them with newline characters.
        private static String readContents(File file) throws IOException {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (!first) sb.append("\n");
                    sb.append(line);
                    first = false;
                }
            }
            return sb.toString();
        }
    }

    // INNER CLASS: ServerConnection
    // Sends encrypted text to the PHP server via HTTP POST.
    // Returns the decrypted result as a String.
    private static class ServerConnection {

        private static final String SERVER_URL = "https://sixactualstudios.com/decrypt.php";
        private static final int    TIMEOUT_MS = 10_000;

        @SuppressWarnings("deprecation")
		static String sendToServer(String encryptedText) throws IOException {
            URL url = new URL(SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setDoOutput(true);

            try {
                try (OutputStreamWriter w = new OutputStreamWriter(
                        conn.getOutputStream(), StandardCharsets.UTF_8)) {
                    w.write(encryptedText);
                    w.flush();
                }
                int code = conn.getResponseCode();
                if (code != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Server error " + code + ": "
                        + readStream(conn.getErrorStream()));
                }
                return readStream(conn.getInputStream());
            } finally {
                conn.disconnect();
            }
        }

        // Reads every line from the stream and returns them joined as one String.
        private static String readStream(InputStream stream) throws IOException {
            if (stream == null) return "";
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) sb.append(line).append("\n");
            }
            String result = sb.toString();
            return result.endsWith("\n") ? result.substring(0, result.length() - 1) : result;
        }
    }

    // INNER CLASS: AgentClientTests
    // Unit tests for the cipher logic.
    // To run: temporarily swap main() body to AgentClientTests.runAll()
    static class AgentClientTests {

        private static int passed = 0;
        private static int failed = 0;

        public static void runAll() {
            System.out.println("=".repeat(50));
            System.out.println("  Sterling Intelligence -- Unit Tests");
            System.out.println("=".repeat(50));

            testDecryption();
            testRoundTrip();
            testEdgeCases();
            testKnownValues();

            System.out.println("-".repeat(50));
            System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
            System.out.println("=".repeat(50));
        }

        private static void testDecryption() {
            System.out.println("\n  [Decryption Tests]");
            check("Assignment test: j[ij'()* = test1234", decrypt("j[ij'()*"), "test1234");
            check("Empty string stays empty",              decrypt(""),           "");
            check("'A' decrypts to 'K'",                  decrypt("A"),          "K");
        }

        private static void testRoundTrip() {
            System.out.println("\n  [Round-Trip Tests]");
            String s1 = "Hello World";    check("Round-trip: Hello World",  decrypt(encrypt(s1)), s1);
            String s2 = "Agent 007";      check("Round-trip: Agent phrase", decrypt(encrypt(s2)), s2);
            String s3 = "1234567890";     check("Round-trip: Numbers",      decrypt(encrypt(s3)), s3);
        }

        private static void testEdgeCases() {
            System.out.println("\n  [Edge Case Tests]");
            check("Null input returns empty",   safeDecrypt(null), "");
            check("Long string does not crash",
                decrypt("j[ij".repeat(500)).length() > 0 ? "ok" : "fail", "ok");
            check("Space decrypts to '*'", decrypt(" "), "*");
        }

        private static void testKnownValues() {
            System.out.println("\n  [Known Value Tests]");
            check("'j' decrypts to 't'",       decrypt("j"),    "t");
            check("'[' decrypts to 'e'",       decrypt("["),    "e");
            check("'i' decrypts to 's'",       decrypt("i"),    "s");
            check("'j[ij' decrypts to 'test'", decrypt("j[ij"), "test");
        }

        // Adds 10 to each character
        private static String decrypt(String text) {
            if (text == null || text.isEmpty()) return "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) sb.append((char)(text.charAt(i) + 10));
            return sb.toString();
        }

        // Subtracts 10 from each character
        // used only in tests for round-tripping.
        private static String encrypt(String text) {
            if (text == null || text.isEmpty()) return "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) sb.append((char)(text.charAt(i) - 10));
            return sb.toString();
        }

        private static String safeDecrypt(String text) {
            return text == null ? "" : decrypt(text);
        }

        private static void check(String name, String actual, String expected) {
            if (expected.equals(actual)) {
                System.out.printf("  [PASS] %s%n", name);
                passed++;
            } else {
                System.out.printf("  [FAIL] %s%n", name);
                System.out.printf("         Expected : \"%s\"%n", expected);
                System.out.printf("         Actual   : \"%s\"%n", actual);
                failed++;
            }
        }
    }
}