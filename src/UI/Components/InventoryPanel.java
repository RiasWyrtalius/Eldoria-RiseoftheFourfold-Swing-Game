package UI.Components;

import Items.Inventory;
import Items.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class InventoryPanel extends JPanel {
    private JList<Item> itemList;
    private DefaultListModel<Item> listModel;
    // super freaking cool callback when the item is either like double clicked or
    // selected
    private Consumer<Item> onItemSelected;
    Inventory currentInventory;

    public InventoryPanel() {
        setLayout(new BorderLayout());
        listModel = new DefaultListModel<>();

        // our custom jList dawg my gawddd sfhsohfsuhfs
        itemList = new JList<>(listModel) {
            @Override
            public String getToolTipText(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index > -1) {
                    Item item = getModel().getElementAt(index);
                    return "<html><b>" + item.getName() + "</b><br>" +
                            item.getDescription() + "</html>";
                }
                return null;
            }
        };

        itemList.setCellRenderer(new ItemListRenderer());

        itemList.addMouseListener(new MouseAdapter() {
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
        });

        JScrollPane scrollPane = new JScrollPane(itemList);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadInventory(Inventory inventory) {
        this.currentInventory = inventory;
        listModel.clear();
        List<Item> items = inventory.getAllItems();

        for (Item item : items) {
            // add to da list if item is quantifiable
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

    // inner class renderer that controls how each row looks
    private class ItemListRenderer extends JLabel implements ListCellRenderer<Item> {
        public ItemListRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Item> list, Item item, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            int count = 0;
            if (currentInventory != null) {
                count = currentInventory.getItemCount(item.getName());
            }
            setText(item.getName() + " (x" + count + ")");

            // assuming 32x32 size for list icons but its not tho
//            ImageIcon icon = AssetManager.getInstance().getImage(item.getIconPath(), 32, 32);
//            setIcon(icon);

//            if (isSelected) {
//                setBackground(list.getSelectionBackground());
//                setForeground(list.getSelectionForeground());
//            } else {
//                setBackground(list.getBackground());
//                setForeground(list.getForeground());
//            }

            return this;
        }
    }
}
