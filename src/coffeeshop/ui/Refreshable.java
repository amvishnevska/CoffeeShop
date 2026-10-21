package coffeeshop.ui;

/**
 * Page of the main window that must reload its data from the shop
 * every time it is shown (data could be changed on another page).
 */
public interface Refreshable {

    void refresh();
}
