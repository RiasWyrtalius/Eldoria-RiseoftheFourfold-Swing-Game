package UI.Components;
import javax.swing.*;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class OutlinedLabel extends JLabel {
    private Color outlineColor = Color.BLACK;
    private float strokeWidth = 2f;

    public OutlinedLabel(String text) {
        super(text);
    }

    public OutlinedLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public void setOutlineColor(Color color) {
        this.outlineColor = color;
        repaint(); // Re-draw the label when color changes
    }

    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 1. Setup Graphics2D for high-quality rendering
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String text = getText();
        if (text == null || text.isEmpty()) {
            g2.dispose();
            return;
        }

        // 2. Create the font shape
        Font font = getFont();
        FontMetrics fm = g2.getFontMetrics(font);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        // Calculate X position (support Left, Center, Right alignment)
        int x = 0;
        if (getHorizontalAlignment() == CENTER) {
            x = (getWidth() - textWidth) / 2;
        } else if (getHorizontalAlignment() == RIGHT) {
            x = getWidth() - textWidth - getInsets().right;
        } else {
            x = getInsets().left;
        }

        // Calculate Y position (vertically centered)
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        // 3. Convert text to a vector Shape
        g2.setFont(font);
        AffineTransform transform = g2.getTransform();
        transform.translate(x, y);
        g2.setTransform(transform);

        TextLayout textLayout = new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
        Shape shape = textLayout.getOutline(null);

        // 4. Draw the Outline
        g2.setColor(outlineColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.draw(shape);

        // 5. Fill the Text (The inner color)
        g2.setColor(getForeground());
        g2.fill(shape);

        g2.dispose();
    }
}