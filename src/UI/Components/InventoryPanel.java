package UI.Components;

import Items.Inventory;
import Items.Item;
import Resource.Animation.AssetManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.function.Consumer;

public class InventoryPanel extends JPanel {

    private static final Color SELECTION_COLOR = new Color(218, 165, 32, 100);
    private static final Color HOVER_COLOR = new Color(255, 255, 255, 40);
    private static final Color FONT_COLOR = new Color(245, 240, 220);
    private static final Font FONT_ITEM = new Font("Georgia", Font.PLAIN, 16);

    private JList<Item> itemList;
    private DefaultListModel<Item> listModel;
    private Consumer<Item> onItemSelected;
    private Inventory currentInventory;
    private int hoveredIndex = -1;

    public InventoryPanel() {
        setOpaque(false);
        this.setBorder(new EmptyBorder(20, 10, 10, 10));
        setLayout(new BorderLayout());

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

        itemList.setOpaque(false);
        itemList.setBackground(new Color(255, 255, 255, 0));
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
//                itemList.repaint();
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
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D) g.create();
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        int arc = 20;
//        int borderThickness = 2;
//        int x = 5, y = 5, w = getWidth() - 10, h = getHeight() - 10;
//
//        g2.setColor(new Color(0, 0, 0, 180));
//        g2.fillRoundRect(x, y, w, h, arc, arc);
//
//        g2.setColor(new Color(218, 165, 32));
//        g2.setStroke(new BasicStroke(borderThickness));
//        g2.drawRoundRect(x, y, w, h, arc, arc);
//
//        g2.dispose();
//    }

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
                setBackground(HOVER_COLOR);
                setForeground(FONT_COLOR);
            } else {
                setBackground(new Color(0, 0, 0, 0));
                setForeground(FONT_COLOR);
            }

            return this;
        }
    }
}