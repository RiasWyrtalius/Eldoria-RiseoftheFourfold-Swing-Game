package UI.Components;

import Items.Inventory;
import Items.Item;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class InventoryPanel extends JPanel {

    // COLORS UPDATED FOR WHITE THEME
    private static final Color SELECTION_COLOR = new Color(218, 165, 32);
    private static final Color HOVER_COLOR = new Color(230, 230, 230);
    private static final Color FONT_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private static final Font FONT_ITEM = new Font("JetBrains Mono", Font.PLAIN, 14);

    private JList<Item> itemList;
    private DefaultListModel<Item> listModel;
    private Consumer<Item> onItemSelected;
    private Inventory currentInventory;
    private int hoveredIndex = -1;

    public InventoryPanel() {
        setOpaque(true);
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        Border lineBorder = BorderFactory.createLineBorder(new Color(100, 100, 100));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, "Inventory");
        titledBorder.setTitleColor(Color.WHITE);
        titledBorder.setTitleColor(new Color(200, 200, 200));
        titledBorder.setTitleJustification(TitledBorder.CENTER);

        // Add padding inside
        this.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        listModel = new DefaultListModel<>();
        itemList = new JList<>(listModel) {
            @Override
            public String getToolTipText(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index > -1) {
                    Item item = getModel().getElementAt(index);
                    return "<html><b>" + item.getName() + "</b><br>" + item.getDescription() + "</html>";
                }
                return null;
            }
        };

        // 3. Set List Background to White
        itemList.setOpaque(true);
        itemList.setBackground(BACKGROUND_COLOR);
        itemList.setCellRenderer(new ItemListRenderer());
        itemList.setSelectionBackground(SELECTION_COLOR);
        itemList.setSelectionForeground(Color.WHITE);

        MouseAdapter unifiedMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = itemList.locationToIndex(e.getPoint());
                if (index != hoveredIndex) {
                    hoveredIndex = index;
                    itemList.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredIndex = -1;
                itemList.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = itemList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Item item = listModel.getElementAt(index);
                        if (onItemSelected != null) {
                            onItemSelected.accept(item);
                        }
                    }
                }
            }
        };

        itemList.addMouseListener(unifiedMouseAdapter);
        itemList.addMouseMotionListener(unifiedMouseAdapter);

        JScrollPane scrollPane = new JScrollPane(itemList);

        // 4. CRITICAL: Set ScrollPane and Viewport to White to remove "Grayness"
        scrollPane.setOpaque(true);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadInventory(Inventory inventory) {
        this.currentInventory = inventory;
        listModel.clear();
        List<Item> items = inventory.getAllItems();
        for (Item item : items) {
            if (inventory.getItemCount(item.getName()) > 0) {
                listModel.addElement(item);
            }
        }
    }

    public void setOnItemSelected(Consumer<Item> callback) {
        this.onItemSelected = callback;
    }

    public Item getSelectedItem() {
        return itemList.getSelectedValue();
    }

    private class ItemListRenderer extends JLabel implements ListCellRenderer<Item> {
        public ItemListRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(5, 10, 5, 10));
            setFont(FONT_ITEM);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Item> list, Item item, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            int count = (currentInventory != null) ? currentInventory.getItemCount(item.getName()) : 0;
            setText(item.getName() + " x" + count);

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(Color.WHITE);
            } else if (index == hoveredIndex) {
                setBackground(HOVER_COLOR); // Light gray hover
                setForeground(FONT_COLOR);  // Black text
            } else {
                setBackground(BACKGROUND_COLOR); // Pure White background
                setForeground(FONT_COLOR);       // Black text
            }

            return this;
        }
    }
}